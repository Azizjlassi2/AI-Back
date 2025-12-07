package com.aiplus.backend.payment.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * Response DTO for Paymee payment initialization
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class PaymeePaymentInitResponse extends PaymentInitResponse {
    private boolean status;
    private String message;
    private int code;
    private PaymeePaymentData data;

    /**
     * Inner class representing the payment data
     */
    @Data
    @RequiredArgsConstructor
    public static class PaymeePaymentData {

        private String token;
        private String order_id;
        private String first_name;
        private String last_name;
        private String email;
        private String phone;
        private String note;
        private double amount;
        private String payment_url;
    }

}