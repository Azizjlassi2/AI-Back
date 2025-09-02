package com.aiplus.backend.docker.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Thrown when Docker Hub authentication fails (e.g., invalid or expired PAT)
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class DockerAuthenticationException extends RuntimeException {
    public DockerAuthenticationException(String msg) {
        super(msg);
    }

    public DockerAuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}