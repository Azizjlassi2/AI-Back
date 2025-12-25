package com.aiplus.backend.deployments.exceptions;

public class InstanceNotDeployedException extends RuntimeException {
    public InstanceNotDeployedException(String msg) {
        super(msg);
    }
}