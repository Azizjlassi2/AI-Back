package com.aiplus.backend.payment.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;

import com.aiplus.backend.config.FrontendProperties;
import com.aiplus.backend.payment.config.PaymeeConfig;
import com.aiplus.backend.payment.dto.PaymeePaymentInitRequest;
import com.aiplus.backend.payment.dto.PaymeePaymentInitResponse;
import com.aiplus.backend.payment.factory.PaymentStatusStrategyFactory;
import com.aiplus.backend.payment.gateways.PaymeeClientGateway;
import com.aiplus.backend.payment.model.Payment;
import com.aiplus.backend.payment.model.PaymentGateway;
import com.aiplus.backend.payment.model.PaymentStatus;
import com.aiplus.backend.payment.repository.PaymentRepository;
import com.aiplus.backend.payment.strategies.PaymentFailedStrategy;
import com.aiplus.backend.payment.strategies.PaymentPendingStrategy;
import com.aiplus.backend.payment.strategies.PaymentStatusStrategy;
import com.aiplus.backend.payment.strategies.PaymentSuccessStrategy;
import com.aiplus.backend.subscription.dto.SubscriptionCreateDTO;
import com.aiplus.backend.subscription.model.Subscription;
import com.aiplus.backend.subscription.repository.SubscriptionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service("PAYMEE")
public class PaymeePaymentService implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final FrontendProperties frontendProperties;
    private final PaymentStatusStrategyFactory paymentStatusStrategyFactory;

    private final PaymeeClientGateway paymeeClientGateway;
    private final PaymeeConfig paymeeConfig;

    /**
     * Retries the given API call with exponential backoff.
     * 
     * @param apiCall     the API call to retry
     * @param maxAttempts maximum number of attempts
     * @return the result of the API call
     * @throws Exception if all attempts fail
     */
    private <T> T retry(Supplier<T> apiCall, int maxAttempts) {
        int attempt = 1;
        while (true) {
            try {
                return apiCall.get();
            } catch (Exception e) {
                if (attempt >= maxAttempts) {
                    throw e;
                }
                long backoff = (long) Math.pow(2, attempt) * 1000;
                log.warn("API call failed on attempt {}, retrying after {} ms", attempt, backoff, e);
                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(ie);
                }
                attempt++;
            }
        }
    }

    /**
     * build the payment request
     */
    private PaymeePaymentInitRequest buildPaymeePaymentInitRequest(Subscription subscription, SubscriptionCreateDTO dto,
            String orderID) {

        String note = buildPaymentDescription(subscription);
        String successUrl = frontendProperties.getUrl() + "/client/payment-confirmation";
        String failureUrl = frontendProperties.getUrl() + "/client/payment-error/";

        PaymeePaymentInitRequest request = PaymeePaymentInitRequest.builder().note(note).first_name(dto.getFirstName())
                .last_name(dto.getLastName()).email(dto.getEmail()).phone(dto.getPhoneNumber())
                .amount(subscription.getPlan().getPrice()).order_id(orderID).return_url(successUrl)
                .cancel_url(failureUrl).webhook_url(paymeeConfig.getWebhookUrl()).build();
        return request;

    }

    /**
     * Builds a user-friendly payment description for Konnect.
     */
    private String buildPaymentDescription(Subscription subscription) {
        return String.format("Thank you for selecting the '%s - %s' plan! "
                + "We're processing your payment, and your subscription will activate automatically once verified.",
                subscription.getPlan().getModel().getName(), subscription.getPlan().getName());
    }

    /**
     * Generates a unique order ID for the payment based on subscription details.
     */
    private String generateOrderId(Subscription subscription) {
        return String.format("SUB-%s-%s-%s-%s-%s", subscription.getPlan().getModel().getId(),
                subscription.getPlan().getId(), subscription.getClient().getId(),
                DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now()),
                UUID.randomUUID().toString().substring(0, 6).toUpperCase());
    }

    /**
     * Generates a unique transaction ID for the payment.
     */
    private String generateTransactionId() {
        return String.format("TX-%s-%s-%s", PaymentGateway.PAYMEE.name(),
                UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now()));
    }

    /**
     * Creates a Payment record in PENDING status for the given subscription.
     */
    private Payment createPaymentRecord(Subscription subscription, String orderID) {

        String transactionId = generateTransactionId();

        Payment payment = Payment.builder().transactionId(transactionId).orderId(orderID).user(subscription.getClient())
                .subscription(subscription).amount(BigDecimal.valueOf(subscription.getPlan().getPrice()))
                .currency(subscription.getPlan().getCurrency()).gateway(PaymentGateway.PAYMEE)
                .status(PaymentStatus.PENDING).build();

        return paymentRepository.save(payment);
    }

    @Transactional
    public PaymeePaymentInitResponse initiatePaymentForSubscription(Subscription subscription,
            SubscriptionCreateDTO dto) {
        log.info("Initiating payment for subscription id={}, clientId={}, planId={}", subscription.getId(),
                subscription.getClient().getId(), subscription.getPlan().getId());
        String orderID = generateOrderId(subscription);

        Payment payment = createPaymentRecord(subscription, orderID);
        log.info("Created payment record id={}, orderId={}", payment.getId(), payment.getOrderId());

        PaymeePaymentInitRequest request = buildPaymeePaymentInitRequest(subscription, dto, orderID);

        PaymeePaymentInitResponse response = retry(() -> paymeeClientGateway.initPayment(request), 3);
        log.info("Paymee payment initiation response: {}", response);

        payment.setStatus(PaymentStatus.PROCESSING);
        payment.setGatewayTransactionId(response.getData().getToken());

        log.info(" \nSet gateway transaction ID: {} \n", response.getData().getToken());

        paymentRepository.save(payment);

        subscription.setPayment(payment);
        subscriptionRepository.save(subscription);

        log.info("Payment initiated successfully for subscription id={}, payment id={}", subscription.getId(),
                payment.getId());

        return response;

    }

    @Transactional
    @Override
    public void handleWebhook(Map<String, String> obj) {

        log.info("Handling Paymee webhook with payload: {}", obj);

        String paymentToken = obj.get("token");
        String status = obj.get("payment_status");
        if (paymentToken == null || status == null) {
            log.error("Invalid webhook data: missing token or payment_status");
            return;
        }
        log.info("Processing webhook for payment token: {}, status: {}", paymentToken, status);

        PaymentStatusStrategy strategy = paymentStatusStrategyFactory.get(status);

        strategy.process(paymentToken, (Map) obj);

    }

}
