// Global exception handler
package com.aiplus.backend.email.exceptions;

import java.util.Collections;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.aiplus.backend.utils.responses.ApiError;

@RestControllerAdvice
public class EmailExceptionHandler {

    @ExceptionHandler({
            PasswordResetEmailException.class,
            AccountActivationEmailException.class,
            ContactMessageEmailException.class,
            NoEmailStrategyFoundException.class
    })
    public ResponseEntity<ApiError> handleEmailExceptions(RuntimeException ex) {
        ApiError apiError = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), // `error`
                ex.getMessage(), // `message`
                Collections.singletonList(ex.getLocalizedMessage()) // `details`
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
    }
}
