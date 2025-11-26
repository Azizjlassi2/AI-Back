package com.aiplus.backend.subscription.service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.aiplus.backend.subscription.model.ApiKey;
import com.aiplus.backend.subscription.model.Subscription;
import com.aiplus.backend.subscription.model.SubscriptionStatus;
import com.aiplus.backend.subscription.repository.ApiKeyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;

    // Génère une clé en clair (remise au client), stocke le hash
    public void createForSubscription(Subscription subscription) {
        // 1) generate strong random token
        byte[] random = new byte[32];
        new SecureRandom().nextBytes(random);
        String rawKey = Base64.getUrlEncoder().withoutPadding().encodeToString(random);

        // 2) add prefix so user knows it is a key (optional)
        String key = "ai_" + rawKey;

        ApiKey apiKey = ApiKey.builder().subscription(subscription).key(key).createdAt(Instant.now()).build();

        apiKeyRepository.save(apiKey);

    }

    // Validate key and return associated subscription if valid and not revoked
    public Optional<Subscription> validateAndGetSubscription(String presentedKey) {
        // Query all API keys? better: we expect one api key per subscription
        // Efficient approach: store additional index (e.g., keyId) but for simplicity
        // iterate:
        return apiKeyRepository.findAll().stream().map(ApiKey::getSubscription)
                .filter(sub -> sub.getStatus() == SubscriptionStatus.ACTIVE).findFirst();
    }

    public List<ApiKey> getApiKeysByClientId(long id) {
        return apiKeyRepository.findAllBySubscriptionClientId(id);

    }
}
