package com.aiplus.backend.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.aiplus.backend.payment.model.PaymentGateway;
import com.aiplus.backend.payment.model.PaymentStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentSummaryDTO {
    private Long id;
    private String transactionId;
    private String orderId;

    private BigDecimal amount;
    private String currency;
    private PaymentGateway gateway;
    private PaymentStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime completedAt;

}
