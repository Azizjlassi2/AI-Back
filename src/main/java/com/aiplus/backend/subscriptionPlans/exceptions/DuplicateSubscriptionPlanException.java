package com.aiplus.backend.subscriptionPlans.exceptions;

public class DuplicateSubscriptionPlanException extends RuntimeException {
    public DuplicateSubscriptionPlanException(String name) {
        super("A subscription plan named '" + name + "' already exists.");
    }
}
