package com.aiplus.backend.auth.exceptions;

import java.util.Collections;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.aiplus.backend.utils.responses.ApiError;

@RestControllerAdvice
public class AuthExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({
            UserNotFoundException.class,
            InvalidTokenException.class,
            ExpiredTokenException.class
    })
    protected ResponseEntity<ApiError> handleNotFound(RuntimeException ex) {
        return buildApiError(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    protected ResponseEntity<ApiError> handleConflict(EmailAlreadyExistsException ex) {
        return buildApiError(HttpStatus.CONFLICT, "Conflict", ex.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    protected ResponseEntity<ApiError> handleUnauthorized(InvalidCredentialsException ex) {
        return buildApiError(HttpStatus.UNAUTHORIZED, "Unauthorized", ex.getMessage());
    }

    @ExceptionHandler(InvalidRoleException.class)
    protected ResponseEntity<ApiError> handleBadRequest(InvalidRoleException ex) {
        return buildApiError(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiError> handleAll(Exception ex) {
        return buildApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "An unexpected error occurred");
    }

    private ResponseEntity<ApiError> buildApiError(HttpStatus status, String error, String message) {
        ApiError apiError = new ApiError(status.value(), status.getReasonPhrase(), message, Collections.emptyList());
        return new ResponseEntity<>(apiError, status);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ApiError apiError = new ApiError(status.value(), "Malformed JSON", ex.getLocalizedMessage(),
                Collections.emptyList());
        return handleExceptionInternal(ex, apiError, headers, status, request);
    }
}
