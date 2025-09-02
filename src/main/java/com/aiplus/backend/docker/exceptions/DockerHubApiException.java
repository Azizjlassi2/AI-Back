package com.aiplus.backend.docker.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// General exception for unexpected Docker Hub API errors
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class DockerHubApiException extends RuntimeException {
    public DockerHubApiException(String msg) {
        super(msg);
    }

    public DockerHubApiException(String msg, Throwable cause) {
        super(msg, cause);
    }
}