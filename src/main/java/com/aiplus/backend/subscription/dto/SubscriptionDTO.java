package com.aiplus.backend.subscription.dto;

import java.time.LocalDate;

import com.aiplus.backend.subscription.model.SubscriptionStatus;
import com.aiplus.backend.subscriptionPlans.dto.SubscriptionPlanDto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubscriptionDTO {

    private Long id;

    /** Associated subscription plan. */
    private SubscriptionPlanDto plan;

    /** Subscription start date. */
    private LocalDate startDate;

    /** Next billing date. */
    private LocalDate nextBillingDate;

    /** Subscription status (PENDING, ACTIVE, etc.). */
    private SubscriptionStatus status;

    /** True if recurring. */
    private boolean recurring;

}
