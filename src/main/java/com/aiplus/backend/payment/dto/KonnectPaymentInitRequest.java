package com.aiplus.backend.payment.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KonnectPaymentInitRequest {

    private String receiverWalletId;
    private String token;
    private Long amount;
    private String orderId;
    private List<String> acceptedPaymentMethods;
    private String webhook;
    private String description;
    private boolean silentWebhook;
    private String successUrl;
    private String failUrl;

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

}
