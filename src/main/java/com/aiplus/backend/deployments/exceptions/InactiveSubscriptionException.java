package com.aiplus.backend.deployments.exceptions;

public class InactiveSubscriptionException extends RuntimeException {
    public InactiveSubscriptionException(String msg) {
        super(msg);
    }
}