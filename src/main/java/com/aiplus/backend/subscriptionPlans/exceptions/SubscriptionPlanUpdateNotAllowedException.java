package com.aiplus.backend.subscriptionPlans.exceptions;

public class SubscriptionPlanUpdateNotAllowedException extends RuntimeException {
    public SubscriptionPlanUpdateNotAllowedException(Long id) {
        super("Subscription plan with ID " + id + " cannot be updated because it is already active.");
    }
}
