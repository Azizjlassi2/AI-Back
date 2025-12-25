package com.aiplus.backend.payment.factory;

import org.springframework.stereotype.Component;

import com.aiplus.backend.payment.strategies.PaymentFailedStrategy;
import com.aiplus.backend.payment.strategies.PaymentStatusStrategy;
import com.aiplus.backend.payment.strategies.PaymentSuccessStrategy;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class PaymentStatusStrategyFactory {
    private final PaymentSuccessStrategy success;
    private final PaymentFailedStrategy failed;

    public PaymentStatusStrategy get(String status) {
        return switch (status.toLowerCase()) {
        case "true" -> success;
        case "false" -> failed;
        // ...
        default -> throw new IllegalArgumentException("Unknown status: " + status);
        };
    }
}
