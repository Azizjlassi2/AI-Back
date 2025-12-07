package com.aiplus.backend.payment.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Request DTO for Paymee payment initialization
 * 
 */
@Data
@Builder
public class PaymeePaymentInitRequest {
    private Double amount;
    private String note;
    private String first_name;
    private String last_name;
    private String email;
    private String phone;
    private String return_url;
    private String cancel_url;
    private String webhook_url;
    private String order_id;

}
