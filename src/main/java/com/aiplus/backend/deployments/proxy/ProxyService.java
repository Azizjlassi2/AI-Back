package com.aiplus.backend.deployments.proxy;

import java.net.URI;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.aiplus.backend.deployments.exceptions.InactiveSubscriptionException;
import com.aiplus.backend.deployments.exceptions.InstanceNotDeployedException;
import com.aiplus.backend.deployments.exceptions.InvalidApiKeyException;
import com.aiplus.backend.deployments.model.DeployedInstance;
import com.aiplus.backend.subscription.model.ApiKey;
import com.aiplus.backend.subscription.model.Subscription;
import com.aiplus.backend.subscription.model.SubscriptionStatus;
import com.aiplus.backend.subscription.repository.ApiKeyRepository;
import com.aiplus.backend.utils.EncryptionUtil;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class ProxyService {

    @Value("${app.render.api.key}")
    private String renderApiKey;

    private final ApiKeyRepository apiKeyRepository;
    private final WebClient webClient;

    @Autowired
    public ProxyService(ApiKeyRepository apiKeyRepository) {
        this.apiKeyRepository = apiKeyRepository;
        this.webClient = WebClient.builder().build();
    }

    @Cacheable(value = "apiKeyValidations", key = "#rawKey") // Cache hashed pour perf, mais invalidez sur changements
    public DeployedInstance validateApiKeyAndGetInstance(String rawKey) {

        String hashedKey = EncryptionUtil.encrypt(rawKey);
        ApiKey apiKey = apiKeyRepository.findByKeyHash(hashedKey)
                .orElseThrow(() -> new InvalidApiKeyException("API key does not exist"));

        Subscription subscription = apiKey.getSubscription();
        if (subscription.getStatus() != SubscriptionStatus.ACTIVE) {
            throw new InactiveSubscriptionException("Subscription is not active");
        }

        DeployedInstance instance = subscription.getDeployedInstance();
        if (instance == null || instance.getStatus() != DeployedInstance.InstanceStatus.RUNNING) {
            throw new InstanceNotDeployedException("Instance does not exist or is not running");
        }

        return instance;
    }

    public Mono<String> forwardRequest(Long modelId, String path, String method, String body, String rawApiKey) {
        DeployedInstance instance = validateApiKeyAndGetInstance(rawApiKey);
        String targetUrl = instance.getBaseUrl() + path;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + renderApiKey);

        headers.set("Accept-Charset", "UTF-8");
        headers.setContentType(MediaType.APPLICATION_JSON);

        /**
         * Construisez et envoyez la requête au déploiement ciblé
         * 
         */
        return webClient.method(HttpMethod.valueOf(method)).uri(URI.create(targetUrl)).headers(h -> h.addAll(headers))
                .contentType(MediaType.APPLICATION_JSON).acceptCharset(StandardCharsets.UTF_8)
                .bodyValue(body != null ? body : "{}").retrieve().bodyToMono(String.class).doOnError(e -> {
                    /* Log error */ log.error("Error forwarding request to {}: {}", targetUrl, e.getMessage());
                });
    }

    /**
     * TODO : Add method to update this metrics for the deployed instance private
     * String instanceType; // e.g., "free", "standard" private String region; //
     * e.g., "oregon" private int cpu; private int memory; private int storage;
     * 
     * private float cpuUsage; private float memoryUsage;
     * 
     * private LocalDateTime deployedAt; private String baseUrl; // URL de
     * l'instance déployée private int port; // Port exposé (e.g., 8080)
     * 
     * private int totalRequests; private int requestsPerMinute; private int
     * successfulRequests; private int failedRequests; private float
     * averageResponseTime;
     */

}
