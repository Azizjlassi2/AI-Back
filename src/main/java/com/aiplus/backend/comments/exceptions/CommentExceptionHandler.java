package com.aiplus.backend.comments.exceptions;

import java.util.Collections;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.aiplus.backend.utils.responses.ApiError;

@RestControllerAdvice

public class CommentExceptionHandler {

    @ExceptionHandler(CommentNotFoundException.class)
    protected ResponseEntity<ApiError> handleCommentNotFound(RuntimeException ex) {
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage(),
                Collections.emptyList());
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }
}
