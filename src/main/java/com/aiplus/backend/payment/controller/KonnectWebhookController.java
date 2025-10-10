package com.aiplus.backend.payment.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aiplus.backend.payment.service.PaymentService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/payments/webhook")
public class KonnectWebhookController {

    private static PaymentService paymentService;

    /**
     * Handle incoming webhook from Konnect
     * 
     */
    @GetMapping
    public void handleWebhook(@RequestParam String payment_ref) {
        log.info("Received webhook data: {}", payment_ref);
        paymentService.handleWebhook(payment_ref);
    }

}
