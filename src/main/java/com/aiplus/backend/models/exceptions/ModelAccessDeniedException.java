package com.aiplus.backend.models.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ModelAccessDeniedException extends RuntimeException {

    public ModelAccessDeniedException() {
    }

    public ModelAccessDeniedException(String message) {
        super(message);
    }

}
