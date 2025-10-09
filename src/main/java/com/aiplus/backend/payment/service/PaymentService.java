package com.aiplus.backend.payment.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aiplus.backend.payment.dto.KonnectWebhookData;
import com.aiplus.backend.payment.dto.PaymentUpdateDTO;
import com.aiplus.backend.payment.gateways.KonnectClientGateway;
import com.aiplus.backend.payment.model.Payment;
import com.aiplus.backend.payment.model.PaymentGateway;
import com.aiplus.backend.payment.model.PaymentStatus;
import com.aiplus.backend.payment.repository.PaymentRepository;
import com.aiplus.backend.subscription.model.Subscription;
import com.aiplus.backend.subscription.model.SubscriptionStatus;
import com.aiplus.backend.subscription.repository.SubscriptionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

        private final PaymentRepository paymentRepository;
        private final SubscriptionRepository subscriptionRepository;
        private final KonnectClientGateway konnectClient;
        private final SimpMessagingTemplate messagingTemplate; // Injected for WebSocket sends

        @Value("${app.webhook.url}")
        private String webhookUrl;
        @Value("${app.base.url}")
        private String baseUrl;

        @Value("${konnect.platform.wallet.id}")
        private String platformWalletId;
        @Value("${app.platform.commission.percent:10}")
        private BigDecimal PLATFORM_COMMISSION_PERCENT; // 10 by default

        /**
         * Initiate payment for a subscription. - creates Payment (PENDING) - calls
         * Konnect /payments/init-payment with receiverWalletId = platformWalletId -
         * updates Payment.gatewayTransactionId and sets status PROCESSING - returns
         * payUrl (redirect target for frontend)
         */
        @Transactional
        public String initiatePaymentForSubscription(Subscription subscription) {
                log.info("--------------------------------------------------------------------\n");

                log.info("Initiating payment for subscription id={} clientId={} planId={}", subscription.getId(),
                                subscription.getClient().getId(), subscription.getPlan().getId());
                log.info("--------------------------------------------------------------------\n");
                // Generate unique orderId
                String orderId = String.format("SUB-%s-%s-%s-%s-%s", subscription.getPlan().getModel().getId(),
                                subscription.getPlan().getId(), subscription.getClient().getId(),
                                DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now()),
                                UUID.randomUUID().toString().substring(0, 6).toUpperCase());
                // Generate unique transactionId
                String transactionId = String.format("TX-%s-%s-%s", PaymentGateway.KONNECT.name(),
                                UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                                DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now()));

                // Build Payment record
                Payment payment = Payment.builder().transactionId(transactionId).orderId(orderId)
                                .userId(subscription.getClient().getId()).subscription(subscription)
                                .amount(BigDecimal.valueOf(subscription.getPlan().getPrice()))
                                .currency(subscription.getPlan().getCurrency()).gateway(PaymentGateway.KONNECT)
                                .status(PaymentStatus.PENDING).build();

                paymentRepository.save(payment);
                log.info("Created payment record id={} orderId={}", payment.getId(), payment.getOrderId());
                log.info("--------------------------------------------------------------------\n");

                log.info("Converting amount {} {} to millimes for Konnect", payment.getAmount(), payment.getCurrency());
                // Convert price to millimes (digit-by-digit care)
                // Example: price = 12.345 (TND) => millimes = price * 1000 = 12345
                BigDecimal price = payment.getAmount();
                BigDecimal multiplier = new BigDecimal(1000); // 1 TND = 1000 millimes
                BigDecimal amountInMillimesBD = price.multiply(multiplier).setScale(0, RoundingMode.HALF_UP);
                long amountInMillimes = amountInMillimesBD.longValueExact();

                log.info("Initiating Konnect payment for amount={} millimes, orderId={}, userId={}", amountInMillimes,

                                payment.getOrderId(), payment.getUserId());
                log.info("--------------------------------------------------------------------\n");
                // Call Konnect init-payment
                String msg = "Payment initiated for the '" + subscription.getPlan().getModel().getName() + " - "
                                + subscription.getPlan().getName()
                                + "' plan. Your subscription will activate automatically once the transaction is verified.";

                Map<String, Object> resp = konnectClient.initPayment(platformWalletId, amountInMillimes,
                                payment.getOrderId(), baseUrl + webhookUrl, msg,
                                subscription.getPlan().getModel().getId());
                log.info("Konnect init-payment response: {}", resp);
                log.info("--------------------------------------------------------------------\n");
                // Expect response contains payUrl and paymentRef (names depend on Konnect)
                String payUrl = (String) resp.get("payUrl");
                String paymentRef = (String) resp.get("paymentRef");

                payment.setGatewayTransactionId(paymentRef);
                payment.setStatus(PaymentStatus.PROCESSING);
                paymentRepository.save(payment);

                log.info("Initiated Konnect payment paymentRef={} for paymentId={}", paymentRef, payment.getId());
                return payUrl;
        }

        /**
         * Handles Konnect webhook notification. - Fetches payment details via GET
         * /payments/{paymentRef} - If "completed", updates Payment to COMPLETED,
         * calculates commission, initiates transfer to developer, activates
         * Subscription, and sends WebSocket notification to the client. - For other
         * statuses, updates to FAILED without activation or notification.
         */
        @Transactional
        public void handleWebhook(KonnectWebhookData data) {
                log.info("Handling webhook for paymentRef={}", data.getPayment_ref());

                // Fetch details from Konnect
                Map<String, Object> details = konnectClient.getPaymentDetails(data.getPayment_ref());
                @SuppressWarnings("unchecked")
                Map<String, Object> paymentData = (Map<String, Object>) details.get("payment");
                String konnectStatus = (String) paymentData.get("status");

                Payment payment = paymentRepository.findByGatewayTransactionId(data.getPayment_ref()).orElseThrow(
                                () -> new RuntimeException("Payment not found for ref: " + data.getPayment_ref()));

                if ("completed".equalsIgnoreCase(konnectStatus)) {
                        payment.setStatus(PaymentStatus.COMPLETED);
                        payment.setCompletedAt(LocalDateTime.now());
                        paymentRepository.save(payment);

                        // Process commission and transfer (90% to developer)
                        Subscription subscription = payment.getSubscription();
                        BigDecimal totalAmount = payment.getAmount();
                        BigDecimal commission = totalAmount.multiply(PLATFORM_COMMISSION_PERCENT
                                        .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP));
                        BigDecimal developerAmount = totalAmount.subtract(commission);

                        String developerWalletId = subscription.getDeveloper().getKonnectWalletId();
                        initiateTransfer(developerWalletId, developerAmount); // Implement as per previous; assumed
                                                                              // method

                        // Activate subscription
                        subscription.setStatus(SubscriptionStatus.ACTIVE);
                        subscription.setStartDate(LocalDate.now());
                        subscriptionRepository.save(subscription);

                        // Send WebSocket notification to user (using email as username)
                        String username = subscription.getClient().getEmail(); // Assuming email is username
                        PaymentUpdateDTO update = new PaymentUpdateDTO("COMPLETED", subscription.getId(),
                                        "Subscription activated successfully.");
                        messagingTemplate.convertAndSendToUser(username, "/payment-updates", update);
                        log.info("Sent WebSocket update to user={} for subscriptionId={}", username,
                                        subscription.getId());
                } else {
                        payment.setStatus(PaymentStatus.FAILED);
                        paymentRepository.save(payment);
                        log.warn("Payment failed for ref={}", data.getPayment_ref());
                }
        }

        /**
         * Initiates transfer to developer's wallet after commission. - Converts amount
         * to millimes and calls assumed Konnect /transfers endpoint.
         */
        private void initiateTransfer(String recipientWalletId, BigDecimal amount) {
                BigDecimal amountInMillimesBD = amount.multiply(BigDecimal.valueOf(1000)).setScale(0,
                                RoundingMode.HALF_UP);
                long amountInMillimes = amountInMillimesBD.longValueExact();

                // Implement Konnect transfer call (adapt based on actual API)
                Map<String, Object> body = new HashMap<>();
                body.put("recipientWalletId", recipientWalletId);
                body.put("amount", amountInMillimes);
                body.put("token", "TND");
                body.put("description", "Developer payout after commission");

                // Use restTemplate or konnectClient to POST /transfers (assumed)
                log.info("Transferring {} millimes to developer wallet={}", amountInMillimes, recipientWalletId);
                // Add actual API call here; throw exception on failure
        }
}