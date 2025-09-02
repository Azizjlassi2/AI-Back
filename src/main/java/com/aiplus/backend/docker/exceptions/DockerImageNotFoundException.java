package com.aiplus.backend.docker.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Thrown when the requested Docker image or its tags are not found
@ResponseStatus(HttpStatus.NOT_FOUND)
public class DockerImageNotFoundException extends RuntimeException {
    public DockerImageNotFoundException(String imageName) {
        super("Docker image not found: " + imageName);
    }

}