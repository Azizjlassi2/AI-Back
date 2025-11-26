package com.aiplus.backend.subscription.dto;

import com.aiplus.backend.payment.model.PaymentGateway;

import lombok.Builder;
import lombok.Data;

/***
 * DTO for creating a new subscription.
 */
@Data
@Builder
public class SubscriptionCreateDTO {

    private String modelName;
    private String planName;

    private PaymentGateway paymentMethod;
    private Long clientId;
    private Long planId;

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String city;
    private String zipCode;

}
