package com.aiplus.backend.models.exceptions;

import java.util.Collections;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.aiplus.backend.utils.responses.ApiError;

@RestControllerAdvice
public class AiModelExceptionHandler {

        @ExceptionHandler({ AiModelNotFoundException.class, DeveloperNotFoundException.class,
                        TaskNotFoundException.class })
        protected ResponseEntity<ApiError> handleNotFound(RuntimeException ex) {
                ApiError apiError = new ApiError(HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage(),
                                Collections.emptyList());
                return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler({ RoleBasedAccessDeniedException.class })
        protected ResponseEntity<ApiError> handleInsufficientRoleException(RuntimeException ex) {
                ApiError apiError = new ApiError(HttpStatus.FORBIDDEN.value(), "Insufficient Role", ex.getMessage(),
                                Collections.emptyList());
                return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
        }

        @ExceptionHandler({ AiModelNameAlreadyExistsException.class })
        protected ResponseEntity<ApiError> handleModelNameAlreadyExistsException(RuntimeException ex) {
                ApiError apiError = new ApiError(HttpStatus.CONFLICT.value(), "Model Name Already Exists",
                                ex.getMessage(), Collections.emptyList());
                return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
        }

        @ExceptionHandler({ ModelAccessDeniedException.class, AuthorizationDeniedException.class })
        protected ResponseEntity<ApiError> handleModelAccessDeniedException(RuntimeException ex) {
                ApiError apiError = new ApiError(HttpStatus.FORBIDDEN.value(), "Not allowed to view / edit this model",
                                ex.getMessage(), Collections.emptyList());
                return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
        }

        @ExceptionHandler(Exception.class)
        protected ResponseEntity<ApiError> handleAll(Exception ex) {
                ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error",
                                ex.getLocalizedMessage(), Collections.emptyList());
                return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
        }
}
