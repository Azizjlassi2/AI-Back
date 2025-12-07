package com.aiplus.backend.payment.service;

import com.aiplus.backend.payment.dto.PaymentInitResponse;
import com.aiplus.backend.subscription.dto.SubscriptionCreateDTO;
import com.aiplus.backend.subscription.model.Subscription;

public interface PaymentService {

    PaymentInitResponse initiatePaymentForSubscription(Subscription subscription, SubscriptionCreateDTO dto);

    void handleWebhook(String paymentRef);

}
