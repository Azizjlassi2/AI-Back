package com.aiplus.backend.payment.strategies;

import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class PaymentFailedStrategy implements PaymentStatusStrategy {
    @Override
    public void process(String token, Map<String, Object> data) {
        // Implement the logic to handle failed payment
        System.out.println("Processing failed payment for token: " + token);
        // Additional processing logic can be added here
    }

}
