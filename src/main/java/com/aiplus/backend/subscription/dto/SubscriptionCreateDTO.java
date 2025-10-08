package com.aiplus.backend.subscription.dto;

import lombok.Builder;
import lombok.Data;

/***
 * DTO for creating a new subscription.
 */
@Data
@Builder
public class SubscriptionCreateDTO {

    private String modelName;
    private String planName;

    private String paymentMethod;

    private Long clientId;
    private Long planId;

}
