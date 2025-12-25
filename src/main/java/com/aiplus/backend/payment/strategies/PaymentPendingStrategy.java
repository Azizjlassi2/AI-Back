package com.aiplus.backend.payment.strategies;

public class PaymentPendingStrategy implements PaymentStatusStrategy {
    @Override
    public void process(String token, java.util.Map<String, Object> data) {
        // Implement the logic to handle pending payment
        System.out.println("Processing pending payment for token: " + token);
        // Additional processing logic can be added here
    }

}
