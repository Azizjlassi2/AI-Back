package com.aiplus.backend.payment.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aiplus.backend.payment.gateways.KonnectClientGateway;
import com.aiplus.backend.payment.model.Payment;
import com.aiplus.backend.payment.model.PaymentGateway;
import com.aiplus.backend.payment.model.PaymentStatus;
import com.aiplus.backend.payment.repository.PaymentRepository;
import com.aiplus.backend.subscription.model.Subscription;
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

        @Value("${app.webhook.url}")
        private String webhookUrl;
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
                                payment.getOrderId(), webhookUrl, msg, subscription.getPlan().getModel().getId());
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
         * Handle webhook callback (paymentRef from Konnect). - fetches Konnect payment
         * details - if completed: mark Payment COMPLETED, apply commission, create
         * payout to developer - if failed: mark payment FAILED
         * 
         * @Transactional public void processKonnectPaymentCallback(String paymentRef) {
         *                log.info("Processing Konnect callback for ref={}",
         *                paymentRef); Payment payment =
         *                paymentRepository.findByGatewayTransactionId(paymentRef)
         *                .orElseThrow(() -> new IllegalArgumentException("Payment not
         *                found with ref: " + paymentRef));
         * 
         *                // Idempotency: if already completed -> nothing to do if
         *                (payment.getStatus() == PaymentStatus.COMPLETED) {
         *                log.info("Payment {} already COMPLETED, ignoring webhook",
         *                payment.getId()); return; }
         * 
         *                // Fetch payment details from Konnect Map<String, Object> body
         *                = konnectClient.getPaymentDetails(paymentRef); // assume the
         *                response has a structure: { "payment": { "status":
         *                "completed", // "amount": 12345, ... } } Map<String, Object>
         *                paymentData = (Map<String, Object>) body.get("payment");
         *                String status = paymentData.get("status").toString();
         * 
         *                if ("completed".equalsIgnoreCase(status)) { // mark payment
         *                complete payment.setStatus(PaymentStatus.COMPLETED);
         *                payment.setCompletedAt(LocalDateTime.now());
         *                paymentRepository.save(payment);
         * 
         *                // commission calculation (10%) BigDecimal total =
         *                payment.getAmount(); // e.g., 12.345 // Commission percent
         *                e.g. 10 -> we compute 10% = total * 10 / 100 BigDecimal
         *                commission =
         *                total.multiply(PLATFORM_COMMISSION_PERCENT).divide(BigDecimal.valueOf(100),
         *                6, RoundingMode.HALF_UP); // Net to developer BigDecimal net =
         *                total.subtract(commission);
         * 
         *                log.info("Payment {} completed: total={}, commission={},
         *                net={}", payment.getId(), total, commission, net);
         * 
         *                // Convert net to millimes for payout BigDecimal netMillimesBD
         *                = net.multiply(BigDecimal.valueOf(1000)).setScale(0,
         *                RoundingMode.HALF_UP); long netMillimes =
         *                netMillimesBD.longValueExact();
         * 
         *                // Payout to developer's konnect wallet (platform pushes the
         *                remaining) Subscription subscription =
         *                payment.getSubscription(); DeveloperAccount dev =
         *                subscription.getDeveloper();
         * 
         *                String developerWallet = dev.getKonnectWalletId(); if
         *                (developerWallet == null || developerWallet.isEmpty()) {
         *                log.error("Developer wallet missing for developer id={}",
         *                dev.getId()); // Consider refund or manual handling - here we
         *                mark paid but warn } else { String payoutOrderId = "payout-" +
         *                payment.getOrderId(); Map<String, Object> payoutResp =
         *                konnectClient.createPayout(developerWallet, netMillimes,
         *                payoutOrderId, "Payout for subscription " +
         *                subscription.getId()); log.info("Payout response: {}",
         *                payoutResp); // Optionally store payoutRef from payoutResp
         *                into a new Payment record of type // PAYOUT (left as exercise)
         *                }
         * 
         *                // Activate subscription
         *                subscription.setStatus(com.aiplus.backend.subscription.model.SubscriptionStatus.ACTIVE);
         *                subscription.setStartDate(java.time.LocalDate.now()); // set
         *                nextBillingDate according to plan.billingPeriod (out of scope
         *                for now) subscriptionRepository.save(subscription);
         * 
         *                } else { // treat anything else as failed/pending
         *                PaymentStatus newStatus =
         *                "processing".equalsIgnoreCase(status) ?
         *                PaymentStatus.PROCESSING : PaymentStatus.FAILED;
         *                payment.setStatus(newStatus); paymentRepository.save(payment);
         *                log.warn("Payment {} status updated to {}", payment.getId(),
         *                newStatus); } }
         */

}