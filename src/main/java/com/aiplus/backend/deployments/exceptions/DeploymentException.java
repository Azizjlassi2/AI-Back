package com.aiplus.backend.deployments.exceptions;

public class DeploymentException extends RuntimeException {
    private final String errorCode;

    public DeploymentException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
