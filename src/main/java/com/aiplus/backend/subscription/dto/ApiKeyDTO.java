package com.aiplus.backend.subscription.dto;

import java.time.Instant;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiKeyDTO {

    private Long id;

    private SubscriptionDTO subscription;

    private String key;

    private Instant createdAt;

}
