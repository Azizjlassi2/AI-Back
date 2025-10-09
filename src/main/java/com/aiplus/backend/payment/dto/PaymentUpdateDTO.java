package com.aiplus.backend.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO for sending payment update notifications via WebSocket. - Includes status
 * (e.g., "COMPLETED", "FAILED") and subscription ID for frontend handling.
 */
@Data
@AllArgsConstructor
public class PaymentUpdateDTO {
    private String status;
    private Long subscriptionId;
    private String message; // Optional: Additional details like "Subscription activated"
}