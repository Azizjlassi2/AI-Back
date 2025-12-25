package com.aiplus.backend.payment.factory;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.aiplus.backend.payment.model.PaymentGateway;
import com.aiplus.backend.payment.service.PaymentService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class PaymentServiceFactory {
    private final Map<String, PaymentService> services;

    public PaymentService getPaymentService(PaymentGateway gateway) {
        PaymentService svc = services.get(gateway.name().toUpperCase());

        if (svc == null) {
            throw new IllegalArgumentException("Unsupported payment gateway: " + gateway.name());
        }
        return svc;
    }
}
