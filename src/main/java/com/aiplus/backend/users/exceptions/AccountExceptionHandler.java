package com.aiplus.backend.users.exceptions;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.aiplus.backend.utils.responses.ApiError;

@RestControllerAdvice
public class AccountExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ AccountNotFoundException.class, ClientAccountNotFoundException.class })
    protected ResponseEntity<ApiError> handleAccountNotFound(UserNotFoundException ex) {
        ApiError err = new ApiError(HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage(), List.of());
        return new ResponseEntity<>(err, HttpStatus.NOT_FOUND);
    }

}
