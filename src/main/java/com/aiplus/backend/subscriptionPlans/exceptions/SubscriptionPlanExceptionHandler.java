package com.aiplus.backend.subscriptionPlans.exceptions;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.aiplus.backend.utils.responses.ApiError;

/**
 * Centralized exception handler for SubscriptionPlan-related exceptions.
 */
@RestControllerAdvice
public class SubscriptionPlanExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles cases where a subscription plan is not found.
     */
    @ExceptionHandler(SubscriptionPlanNotFoundException.class)
    protected ResponseEntity<ApiError> handleSubscriptionPlanNotFound(SubscriptionPlanNotFoundException ex) {
        ApiError error = new ApiError(HttpStatus.NOT_FOUND.value(), "Subscription Plan Not Found", ex.getMessage(),
                List.of());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles cases where a duplicate subscription plan is being created.
     */
    @ExceptionHandler(DuplicateSubscriptionPlanException.class)
    protected ResponseEntity<ApiError> handleDuplicateSubscriptionPlan(DuplicateSubscriptionPlanException ex) {
        ApiError error = new ApiError(HttpStatus.CONFLICT.value(), "Duplicate Subscription Plan", ex.getMessage(),
                List.of());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    /**
     * Handles cases where updates to an existing subscription plan are not allowed.
     */
    @ExceptionHandler(SubscriptionPlanUpdateNotAllowedException.class)
    protected ResponseEntity<ApiError> handleSubscriptionPlanUpdateNotAllowed(
            SubscriptionPlanUpdateNotAllowedException ex) {
        ApiError error = new ApiError(HttpStatus.FORBIDDEN.value(), "Subscription Plan Update Not Allowed",
                ex.getMessage(), List.of());
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }
}
