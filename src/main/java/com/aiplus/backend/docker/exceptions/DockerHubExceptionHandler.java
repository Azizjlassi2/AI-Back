package com.aiplus.backend.docker.exceptions;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.aiplus.backend.utils.responses.ApiError;

@RestControllerAdvice
public class DockerHubExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(DockerImageNotFoundException.class)
    protected ResponseEntity<ApiError> handleDockerImageNotFound(DockerImageNotFoundException ex) {
        ApiError err = new ApiError(HttpStatus.NOT_FOUND.value(),
                "Not Found", ex.getMessage(), List.of());
        return new ResponseEntity<>(err, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DockerAuthenticationException.class)
    protected ResponseEntity<ApiError> handleDockerAuthentication(DockerAuthenticationException ex) {
        ApiError err = new ApiError(HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized", ex.getMessage(), List.of());
        return new ResponseEntity<>(err, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(DockerHubApiException.class)
    protected ResponseEntity<ApiError> handleDockerHubApiException(DockerHubApiException ex) {
        ApiError err = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error", ex.getMessage(), List.of());
        return new ResponseEntity<>(err, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
