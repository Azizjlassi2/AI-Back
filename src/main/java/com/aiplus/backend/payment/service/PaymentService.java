package com.aiplus.backend.payment.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aiplus.backend.config.FrontendProperties;
import com.aiplus.backend.payment.dto.KonnectPaymentInitRequest;
import com.aiplus.backend.payment.dto.KonnectPaymentInitResponse;
import com.aiplus.backend.payment.dto.PaymentUpdateDTO;
import com.aiplus.backend.payment.gateways.KonnectClientGateway;
import com.aiplus.backend.payment.model.Payment;
import com.aiplus.backend.payment.model.PaymentGateway;
import com.aiplus.backend.payment.model.PaymentStatus;
import com.aiplus.backend.payment.repository.PaymentRepository;
import com.aiplus.backend.subscription.dto.SubscriptionCreateDTO;
import com.aiplus.backend.subscription.model.Subscription;
import com.aiplus.backend.subscription.model.SubscriptionStatus;
import com.aiplus.backend.subscription.repository.SubscriptionRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

        private final PaymentRepository paymentRepository;
        private final SubscriptionRepository subscriptionRepository;
        private final KonnectClientGateway konnectClient;
        private final SimpMessagingTemplate messagingTemplate;
        private final FrontendProperties frontendProperties;

        @Value("${app.konnect.webhook.url}")
        private String konnectWebhookUrl;

        @Value("${app.base.url}")
        private String baseUrl;

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
         * Builds the Konnect payment initiation request with all required fields.
         */
        private KonnectPaymentInitRequest buildKonnectRequest(Payment payment, Long amountInMillimes,
                        Subscription subscription, SubscriptionCreateDTO dto) {

                String description = buildPaymentDescription(subscription);
                String successUrl = frontendProperties.getUrl() + "/#/client/payment-confirmation";
                String failureUrl = frontendProperties.getUrl() + "/#/client/payment-error/";

                return KonnectPaymentInitRequest.builder()
                                .receiverWalletId(subscription.getPlan().getModel().getDeveloperAccount()
                                                .getKonnectWalletId())
                                .token("TND").amount(amountInMillimes).orderId(payment.getOrderId())
                                .acceptedPaymentMethods(List.of("wallet", "bank_card", "e-DINAR"))
                                .webhook(konnectWebhookUrl).silentWebhook(true).description(description)
                                .successUrl(successUrl).failUrl(failureUrl).firstName(dto.getFirstName())
                                .lastName(dto.getLastName()).email(dto.getEmail()).phoneNumber(dto.getPhoneNumber())
                                .build();
        }

        /**
         * Creates a Payment record in PENDING status for the given subscription.
         */
        private Payment createPaymentRecord(Subscription subscription) {
                String orderId = generateOrderId(subscription);
                String transactionId = generateTransactionId();

                Payment payment = Payment.builder().transactionId(transactionId).orderId(orderId)
                                .user(subscription.getClient()).subscription(subscription)
                                .amount(BigDecimal.valueOf(subscription.getPlan().getPrice()))
                                .currency(subscription.getPlan().getCurrency()).gateway(PaymentGateway.KONNECT)
                                .status(PaymentStatus.PENDING).build();

                return paymentRepository.save(payment);
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
                return String.format("TX-%s-%s-%s", PaymentGateway.KONNECT.name(),
                                UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                                DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now()));
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
         * Converts amount in TND to millimes (1 TND = 1000 millimes).
         */
        private Long convertToMillimes(BigDecimal amountInTND) {
                BigDecimal multiplier = new BigDecimal(1000);
                BigDecimal amountInMillimesBD = amountInTND.multiply(multiplier).setScale(0, RoundingMode.HALF_UP);
                return amountInMillimesBD.longValueExact();
        }

        /**
         * Initiates a payment for the given subscription. - Creates a Payment record in
         * PENDING status. - Converts amount to millimes. - Builds and sends the Konnect
         * payment initiation request with retries. - Updates Payment status to
         * PROCESSING upon successful initiation. - Logs all steps and errors for
         * traceability.
         */
        @Transactional
        public KonnectPaymentInitResponse initiatePaymentForSubscription(Subscription subscription,
                        SubscriptionCreateDTO dto) {
                log.info("Initiating payment for subscription id={}, clientId={}, planId={}", subscription.getId(),
                                subscription.getClient().getId(), subscription.getPlan().getId());

                Payment payment = createPaymentRecord(subscription);
                log.info("Created payment record id={}, orderId={}", payment.getId(), payment.getOrderId());

                Long amountInMillimes = convertToMillimes(payment.getAmount());
                log.info("Converted {} {} to {} millimes", payment.getAmount(), payment.getCurrency(),
                                amountInMillimes);

                KonnectPaymentInitRequest konnectRequest = buildKonnectRequest(payment, amountInMillimes, subscription,
                                dto);

                KonnectPaymentInitResponse response = retry(
                                () -> (KonnectPaymentInitResponse) konnectClient.initPayment(konnectRequest), 3);
                log.info("Konnect init-payment response: {}", response);

                payment.setStatus(PaymentStatus.PROCESSING);
                payment.setGatewayTransactionId(response.getPaymentRef());
                paymentRepository.save(payment);

                subscription.setPayment(payment);
                subscriptionRepository.save(subscription);

                log.info("Initiated Konnect payment paymentRef={} for paymentId={}", response.getPaymentRef(),
                                payment.getId());

                return response;
        }

        /**
         * Handles the Konnect webhook to confirm payment status. - Fetches payment
         * details from Konnect with retries. - Updates local Payment and Subscription
         * records based on status. - Sends WebSocket notifications to the client about
         * payment result. - Logs all steps and errors for traceability.
         */
        @Transactional
        public void handleWebhook(String paymentRef) {
                log.info("Handling webhook for paymentRef={}", paymentRef);

                try {
                        Map<String, Object> details = retry(() -> konnectClient.getPaymentDetails(paymentRef), 3);
                        log.info("Fetched payment details from Konnect: {}", details);

                        @SuppressWarnings("unchecked")
                        Map<String, Object> paymentData = (Map<String, Object>) details.get("payment");
                        if (paymentData == null) {
                                throw new IllegalStateException("Payment data missing in Konnect response");
                        }

                        String konnectStatus = (String) paymentData.get("status");
                        if (konnectStatus == null) {
                                throw new IllegalStateException("Status missing in Konnect payment data");
                        }

                        Payment payment = paymentRepository.findByTransactionId(paymentRef).orElseThrow(
                                        () -> new EntityNotFoundException("Payment not found for ref: " + paymentRef));

                        if ("completed".equalsIgnoreCase(konnectStatus)) {
                                updatePaymentToCompleted(payment);
                                activateSubscription(payment.getSubscription());
                                sendWebSocketSuccessNotification(payment.getSubscription());
                        } else {
                                updatePaymentToFailed(payment);
                                sendWebSocketFailureNotification(payment.getSubscription());
                        }
                } catch (EntityNotFoundException e) {
                        log.error("Entity not found during webhook handling for paymentRef={}: {}", paymentRef,
                                        e.getMessage());
                } catch (IllegalStateException e) {
                        log.error("Invalid Konnect response for paymentRef={}: {}", paymentRef, e.getMessage());
                        throw e;
                } catch (Exception e) {
                        log.error("Unexpected error handling webhook for paymentRef={}", paymentRef, e);
                        throw new RuntimeException("Webhook handling failed", e);
                }
        }

        /**
         * Updates the payment status to COMPLETED, sets the completion timestamp, and
         * saves the payment.
         */

        private void updatePaymentToCompleted(Payment payment) {
                payment.setStatus(PaymentStatus.COMPLETED);
                payment.setCompletedAt(LocalDateTime.now());
                paymentRepository.save(payment);
        }

        /**
         * Updates the payment status to FAILED and logs the failure event.
         */
        private void updatePaymentToFailed(Payment payment) {
                payment.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);
                log.warn("Payment failed for ref={}", payment.getTransactionId());
        }

        /**
         * Activates the subscription by setting its status to ACTIVE and updating the
         * start date.
         */
        private void activateSubscription(Subscription subscription) {
                subscription.setStatus(SubscriptionStatus.ACTIVE);
                subscription.setStartDate(LocalDate.now());
                subscriptionRepository.save(subscription);
        }

        /**
         * Sends a WebSocket notification to the client indicating payment success and
         * subscription activation.
         */
        private void sendWebSocketSuccessNotification(Subscription subscription) {
                String username = subscription.getClient().getUser().getEmail();
                PaymentUpdateDTO update = new PaymentUpdateDTO("COMPLETED", subscription.getId(),
                                "Subscription activated successfully.");
                messagingTemplate.convertAndSendToUser(username, "/payment-updates", update);
                log.info("Sent WebSocket update to user={} for subscriptionId={}", username, subscription.getId());
        }

        /**
         * Sends a WebSocket notification to the client indicating payment failure.
         */
        private void sendWebSocketFailureNotification(Subscription subscription) {
                String username = subscription.getClient().getUser().getEmail();
                PaymentUpdateDTO update = new PaymentUpdateDTO("FAILED", subscription.getId(),
                                "Payment failed. Please try again.");
                messagingTemplate.convertAndSendToUser(username, "/payment-updates", update);
                log.info("Sent WebSocket failure update to user={} for subscriptionId={}", username,
                                subscription.getId());
        }
}