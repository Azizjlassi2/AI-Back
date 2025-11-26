package com.aiplus.backend.subscriptionPlans.exceptions;

public class SubscriptionPlanDeletionNotAllowedException extends RuntimeException {
    public SubscriptionPlanDeletionNotAllowedException(Long id) {
        super("Subscription plan with ID " + id + " cannot be deleted as it has active subscriptions.");
    }
}
