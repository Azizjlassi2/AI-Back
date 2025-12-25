package com.aiplus.backend.payment.strategies;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.aiplus.backend.email.service.EmailService;
import com.aiplus.backend.email.strategy.EmailType;
import com.aiplus.backend.payment.model.PaymentStatus;
import com.aiplus.backend.payment.repository.PaymentRepository;
import com.aiplus.backend.subscription.model.ApiKey;
import com.aiplus.backend.subscription.model.Subscription;
import com.aiplus.backend.subscription.model.SubscriptionStatus;
import com.aiplus.backend.subscription.repository.SubscriptionRepository;
import com.aiplus.backend.subscription.service.ApiKeyService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/***
 * Strategy for handling successful payments.
 * 
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class PaymentSuccessStrategy implements PaymentStatusStrategy {

    private final PaymentRepository paymentRepository;
    private final SubscriptionRepository subscriptionRepository;

    private final ApiKeyService apiKeyService;
    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    @Override
    public void process(String token, Map<String, Object> data) {
        log.info("Processing successful payment for token: " + token);
        // Update payment status in the database
        paymentRepository.findByTransactionId(token).ifPresentOrElse(payment -> {
            payment.setStatus(PaymentStatus.COMPLETED);
            paymentRepository.save(payment);
            log.info("Payment with token " + token + " marked as SUCCESS.");
        }, () -> {
            log.error("Payment with token " + token + " not found.");
        });

        // update subscription status
        subscriptionRepository.findByPaymentGatewayTransactionId(token).ifPresentOrElse(subscription -> {
            subscription.setStatus(SubscriptionStatus.ACTIVE);
            subscriptionRepository.save(subscription);
            log.info("Subscription with payment token " + token + " activated.");

            // notify developer
            log.info(" Sending notification to developer for subscription id=" + subscription.getId());
            notifyDeveloper(subscription);
            log.info("Developer notification sent for subscription id=" + subscription.getId());

            // notify client
            log.info("Sending notification to client for subscription id=" + subscription.getId());
            notifyClient(subscription);
            log.info("Client notification sent for subscription id=" + subscription.getId());

        }, () -> {
            log.error("Subscription with payment token " + token + " not found.");
        });

    }

    // Dans la méthode notifyClient :
    private void notifyClient(Subscription subscription) {
        try {
            String clientEmail = subscription.getClient().getUser().getEmail();

            // Créer un payload structuré pour l'email
            Map<String, Object> emailPayload = new HashMap<>();
            emailPayload.put("clientName", subscription.getClient().getUser().getName());

            emailPayload.put("modelName", subscription.getPlan().getModel().getName());
            emailPayload.put("planName", subscription.getPlan().getName());
            emailPayload.put("price", subscription.getPlan().getPrice());
            emailPayload.put("currency", subscription.getPlan().getCurrency());
            emailPayload.put("startDate", subscription.getStartDate().toString());
            emailPayload.put("subscriptionId", subscription.getId());

            // Récupérer la clé API (si disponible)
            List<ApiKey> apiKeys = apiKeyService.getApiKeysByClientId(subscription.getClient().getId());
            if (!apiKeys.isEmpty()) {
                emailPayload.put("apiKey", apiKeys.get(0).getKey());
            }

            // Convertir en JSON string
            String payloadJson = objectMapper.writeValueAsString(emailPayload);

            emailService.sendEmail(EmailType.PAYMENT_SUCCESSFUL, clientEmail, payloadJson);
            log.info("Email de confirmation envoyé au client: {}", clientEmail);

        } catch (Exception e) {
            log.error("Échec de l'envoi de l'email au client", e);
        }
    }

    // Dans la méthode notifyDeveloper :
    private void notifyDeveloper(Subscription subscription) {
        try {
            String developerEmail = subscription.getPlan().getModel().getDeveloperAccount().getUser().getEmail();

            // Créer un payload structuré pour l'email développeur
            Map<String, Object> emailPayload = new HashMap<>();
            emailPayload.put("modelName", subscription.getPlan().getModel().getName());
            emailPayload.put("clientName", subscription.getClient().getUser().getName());
            emailPayload.put("clientEmail", subscription.getClient().getUser().getEmail());
            emailPayload.put("planName", subscription.getPlan().getName());
            emailPayload.put("price", subscription.getPlan().getPrice());
            emailPayload.put("currency", subscription.getPlan().getCurrency());
            emailPayload.put("startDate", subscription.getStartDate().toString());
            emailPayload.put("subscriptionId", subscription.getId());
            emailPayload.put("modelId", subscription.getPlan().getModel().getId());

            // Convertir en JSON string
            String payloadJson = objectMapper.writeValueAsString(emailPayload);

            emailService.sendEmail(EmailType.MODEL_NEW_SUBSCRIBER, developerEmail, payloadJson);
            log.info("Email de notification envoyé au développeur: {}", developerEmail);

        } catch (Exception e) {
            log.error("Échec de l'envoi de l'email au développeur", e);
        }
    }
}