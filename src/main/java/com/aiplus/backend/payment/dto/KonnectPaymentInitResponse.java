package com.aiplus.backend.payment.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
/**
 * Response DTO for Konnect payment initialization
 */
public class KonnectPaymentInitResponse {
    private String paymentRef;
    private String payUrl;
}
