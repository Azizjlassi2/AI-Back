package com.aiplus.backend.models.exceptions;

public class EndpointNotFoundException extends RuntimeException {
    public EndpointNotFoundException(Long id) {
        super("Endpoint not found with id: " + id);
    }
}
