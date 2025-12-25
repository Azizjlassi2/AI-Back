package com.aiplus.backend.payment.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aiplus.backend.payment.factory.PaymentServiceFactory;
import com.aiplus.backend.payment.model.PaymentGateway;
import com.aiplus.backend.payment.service.PaymeePaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/payments/paymee/webhook")

public class PaymeeWebhookController {

    private final PaymentServiceFactory paymentServiceFactory;

    /**
     * Handle incoming webhook from Paymee (POST with JSON payload).
     */
    @PostMapping(path = "", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<Void> handleWebhook(@RequestParam MultiValueMap<String, String> formData) {

        log.info("Received Paymee webhook: {}", formData);
        // Extraction et validation basique (ajoutez plus de checks)
        String paymentToken = formData.getFirst("token");
        String status = formData.getFirst("payment_status");
        if (paymentToken == null || status == null) {
            log.error("Invalid webhook data");
            return ResponseEntity.badRequest().build();
        }

        log.info("Received Paymee webhook: token={}, status={}", paymentToken, status);

        // Conversion vers Map<String, Object> pour compatibilité service
        Map<String, String> dataMap = formData.toSingleValueMap();

        paymentServiceFactory.getPaymentService(PaymentGateway.PAYMEE).handleWebhook(dataMap);
        return ResponseEntity.ok().build();
    }

}
