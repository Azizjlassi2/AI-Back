package com.aiplus.backend.models.exceptions;

public class AiModelNotFoundException extends RuntimeException {

    public AiModelNotFoundException(Long id) {
        super("Model with ID " + id + " not found");
    }

    public AiModelNotFoundException(String name, String type) {
        super("Model with name '" + name + "' and type '" + type + "' not found");
    }

    public AiModelNotFoundException(String name) {
        super("Model with name '" + name + "' not found");
    }

}
