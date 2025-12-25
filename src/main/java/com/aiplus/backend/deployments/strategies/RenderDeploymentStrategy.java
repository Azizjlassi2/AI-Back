package com.aiplus.backend.deployments.strategies;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.aiplus.backend.deployments.exceptions.DeploymentException;
import com.aiplus.backend.deployments.model.DeployedInstance;
import com.aiplus.backend.deployments.repository.DeployedInstanceRepository;
import com.aiplus.backend.docker.service.DockerImageVerifier;
import com.aiplus.backend.models.model.AiModel;
import com.aiplus.backend.subscription.model.Subscription;
import com.aiplus.backend.subscription.repository.SubscriptionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class RenderDeploymentStrategy implements DeploymentStrategy {

    private final RestTemplate restTemplate;
    private final DockerImageVerifier dockerImageVerifier;
    private final DeployedInstanceRepository deployedInstanceRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Value("${app.render.api.key}")
    private String renderApiKey;

    @Value("${app.render.owner.id}")
    private String renderOwnerId;

    @Value("${app.render.service.id}")
    private String renderServiceId;

    @Transactional
    @Override
    public DeployedInstance deploy(AiModel model, Subscription subscription) {
        log.info("Début du déploiement pour le modèle ID: {} et subscription ID: {}", model.getId(),
                subscription.getId());

        try {
            // Étape 1: Construction et vérification de l'image Docker
            String dockerUsername = model.getDeveloperAccount().getDockerUsername();
            String dockerPat = model.getDeveloperAccount().getDockerPat(); // Masquer dans logs réels
            String dockerImage = model.getImage();
            String fullDockerImage = dockerUsername + "/" + model.getImage();

            log.debug("Vérification de l'image Docker: {}", dockerImage);
            if (!dockerImageVerifier.existsImage(dockerUsername, dockerPat, dockerImage)) {
                String errorMsg = "Image Docker non trouvée: " + dockerImage;
                log.error(errorMsg);
                throw new DeploymentException(errorMsg, "DOCKER_IMAGE_NOT_FOUND");
            }
            log.info("Image Docker vérifiée avec succès: {}", dockerImage);

            log.info("Préparation du payload pour déclencher un déploiement sur Render");
            // Étape 2: Préparation pour déclencher un déploiement sur un service existant

            String renderDeployUrl = "https://api.render.com/v1/services/" + renderServiceId + "/deploys";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + renderApiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Payload optionnel pour create-deploy
            Map<String, Object> payload = Map.of("clearCache", "do_not_clear"); // Optionnel, peut être vide

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

            // Étape 3: Appel à l'API Render pour déclencher le déploiement
            log.debug("Envoi de la requête de déploiement à Render: {}", renderDeployUrl);
            ResponseEntity<Map> deployResponse = restTemplate.exchange(renderDeployUrl, HttpMethod.POST, request,
                    Map.class);

            if (deployResponse.getStatusCode() != HttpStatus.CREATED) {
                String errorMsg = "Échec du déclenchement du déploiement Render. Statut: "
                        + deployResponse.getStatusCodeValue();
                log.error(errorMsg + ". Réponse complète: {}", deployResponse.getBody());
                throw new DeploymentException(errorMsg, "RENDER_DEPLOY_FAILED");
            }

            // Étape 4: Récupération des détails du service pour obtenir le slug et
            // construire l'URL
            String renderServiceUrl = "https://api.render.com/v1/services/" + renderServiceId;
            HttpEntity<Void> serviceRequest = new HttpEntity<>(headers);
            ResponseEntity<Map> serviceResponse = restTemplate.exchange(renderServiceUrl, HttpMethod.GET,
                    serviceRequest, Map.class);

            if (serviceResponse.getStatusCode() != HttpStatus.OK) {
                String errorMsg = "Échec de la récupération des détails du service Render. Statut: "
                        + serviceResponse.getStatusCodeValue();
                log.error(errorMsg + ". Réponse complète: {}", serviceResponse.getBody());
                throw new DeploymentException(errorMsg, "RENDER_SERVICE_FETCH_FAILED");
            }
            log.info(" Détails du service Render : {}", serviceResponse.getBody());

            Map serviceBody = (Map) serviceResponse.getBody();
            if (serviceBody == null || !serviceBody.containsKey("slug")) {
                String errorMsg = "Réponse des détails du service Render invalide: slug manquant";
                log.error(errorMsg + ". Corps de réponse: {}", serviceBody);
                throw new DeploymentException(errorMsg, "RENDER_INVALID_SERVICE_RESPONSE");
            }
            log.info("Récupération du slug du service Render: {}", serviceBody.get("slug"));

            String baseUrl = "https://" + serviceBody.get("slug") + ".onrender.com";
            log.info("Déploiement déclenché avec succès. URL de base: {}", baseUrl);

            log.info("Création de l'instance déployée dans la base de données");
            OffsetDateTime odt = OffsetDateTime.parse(serviceBody.get("createdAt").toString());
            LocalDateTime deployedAt = odt.withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime();

            // Étape 5: Création de l'instance (Factory Pattern implicite via builder)
            DeployedInstance instance = DeployedInstance.builder().subscription(subscription)
                    .status(DeployedInstance.InstanceStatus.RUNNING).baseUrl(baseUrl).deployedAt(deployedAt)
                    .instanceType("free") // Exemple, adapter selon configuration ou réponse
                    .region("oregon") // À extraire de la réponse si disponible
                    .port(8080)
                    // Initialiser les métriques à 0 (cpu, memory, requests, etc.)
                    .cpu(0).memory(0).storage(0).cpuUsage(0f).memoryUsage(0f).totalRequests(0).requestsPerMinute(0)
                    .successfulRequests(0).failedRequests(0).averageResponseTime(0f).build();

            log.info("Instance déployée créée: {}", instance);
            // Persister via repository (injecté via DI)
            deployedInstanceRepository.save(instance);
            // Mettre à jour la subscription avec l'instance déployée
            subscription.setDeployedInstance(instance);
            subscriptionRepository.save(subscription);

            log.info("Déploiement terminé pour le modèle ID: {} et subscription ID: {}", model.getId(),
                    subscription.getId());

            return instance;

        } catch (RestClientException e) {
            String errorMsg = "Erreur lors de l'appel à l'API externe: " + e.getMessage();
            log.error(errorMsg, e);
            throw new DeploymentException(errorMsg, "EXTERNAL_API_ERROR");
        } catch (Exception e) {
            String errorMsg = "Erreur inattendue lors du déploiement: " + e.getMessage();
            log.error(errorMsg, e);
            throw new DeploymentException(errorMsg, "UNEXPECTED_ERROR");
        } finally {
            log.info("Fin du déploiement pour le modèle ID: {} et subscription ID: {}", model.getId(),
                    subscription.getId());
        }
    }
}