package com.aiplus.backend.models.exceptions;

public class SubscriptionPlanNotFoundException extends RuntimeException {
    public SubscriptionPlanNotFoundException(Long id) {
        super("Subscription Plan not found with id: " + id);
    }

}
