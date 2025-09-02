package com.aiplus.backend.models.exceptions;

public class AiModelNameAlreadyExistsException extends RuntimeException {
    public AiModelNameAlreadyExistsException(String modelName) {
        super("AI model with name '" + modelName + "' already exists.");
    }
}
