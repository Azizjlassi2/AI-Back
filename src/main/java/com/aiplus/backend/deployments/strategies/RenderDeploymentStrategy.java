package com.aiplus.backend.deployments.strategies;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.aiplus.backend.deployments.model.DeployedInstance;
import com.aiplus.backend.docker.service.DockerImageVerifier;
import com.aiplus.backend.models.model.AiModel;
import com.aiplus.backend.subscription.model.Subscription;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RenderDeploymentStrategy implements DeploymentStrategy {

    private final RestTemplate restTemplate;

    @Value("${app.render.api.key}")
    private String renderApiKey;

    @Value("${app.render.owner.id}")
    private String renderOwnerId;

    @Override
    public DeployedInstance deploy(AiModel model, Subscription subscription) {
        String dockerImage = model.getDeveloperAccount().getDockerUsername() + "/" + model.getImage();

        // Vérifiez l'image Docker (utilisez votre verifier)
        if (!new DockerImageVerifier(restTemplate).existsImage(model.getDeveloperAccount().getDockerUsername(),
                model.getDeveloperAccount().getDockerPat(), dockerImage)) {
            throw new RuntimeException("Docker image not found: " + dockerImage);
        }
        // Payload Render
        String url = "https://api.render.com/v1/services";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + renderApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> payload = Map.of("type", "web", "name",
                "ai-model-" + model.getName() + "-" + subscription.getId(), "ownerId", renderOwnerId, "image",
                Map.of("repository", dockerImage, "tag", "latest"), "envVars", Map.of("PORT", Map.of("value", "8080")),
                "plan", "free");

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

        if (response.getStatusCode() != HttpStatus.CREATED) {
            throw new RuntimeException("Render deployment failed");
        }

        Map body = response.getBody();
        String baseUrl = "https://" + body.get("slug") + ".onrender.com";

        return DeployedInstance.builder().subscription(subscription).status(DeployedInstance.InstanceStatus.DEPLOYING)
                .baseUrl(baseUrl).deployedAt(LocalDateTime.now())
                // ... autres champs
                .build();
    }
}