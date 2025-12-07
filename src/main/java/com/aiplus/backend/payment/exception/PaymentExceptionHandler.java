package com.aiplus.backend.payment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PaymentExceptionHandler {

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<String> handlePaymentException(PaymentException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PaymentInitializationException.class)
    public ResponseEntity<String> handlePaymentInitializationException(PaymentInitializationException ex) {
        return new ResponseEntity<>("Payment initialization error: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
