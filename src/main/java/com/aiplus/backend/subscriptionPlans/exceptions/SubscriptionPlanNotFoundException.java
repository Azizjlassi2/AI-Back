package com.aiplus.backend.subscriptionPlans.exceptions;

public class SubscriptionPlanNotFoundException extends RuntimeException {
    public SubscriptionPlanNotFoundException(Long id) {
        super("Subscription plan with ID " + id + " not found.");
    }

    public SubscriptionPlanNotFoundException(String name) {
        super("Subscription plan with name '" + name + "' not found.");
    }
}
