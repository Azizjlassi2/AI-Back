package com.aiplus.backend.deployments.exceptions;

public class InvalidApiKeyException extends RuntimeException {

    public InvalidApiKeyException(String msg) {
        super(msg);
    }
}