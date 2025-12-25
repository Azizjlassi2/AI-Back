package com.aiplus.backend.email.strategy;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.mail.internet.MimeMessage;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Email strategy for notifying developers about new subscribers
 */
@Component
public class ModelNewSubscriberEmailStrategy implements EmailStrategy {

    private final JavaMailSender mailSender;
    private final ObjectMapper objectMapper;

    public ModelNewSubscriberEmailStrategy(JavaMailSender mailSender, ObjectMapper objectMapper) {
        this.mailSender = mailSender;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(EmailType type) {
        return type == EmailType.MODEL_NEW_SUBSCRIBER;
    }

    @Override
    public void sendEmail(String to, String payload) {
        try {
            // Parse le payload JSON
            Map<String, Object> data = objectMapper.readValue(payload, HashMap.class);

            String modelName = (String) data.get("modelName");
            String clientName = (String) data.get("clientName");
            String clientEmail = (String) data.get("clientEmail");
            String planName = (String) data.get("planName");
            Double price = (Double) data.get("price");
            String currency = (String) data.get("currency");
            String startDate = (String) data.get("startDate");
            Long subscriptionId = Long.valueOf(data.get("subscriptionId").toString());
            Long modelId = Long.valueOf(data.get("modelId").toString());

            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, "utf-8");

            helper.setTo(to);
            helper.setSubject("🎯 Nouvel Abonné pour Votre Modèle '" + modelName + "' !");

            String htmlContent = buildDeveloperEmailContent(modelName, clientName, clientEmail, planName, price,
                    currency, startDate, subscriptionId, modelId);

            helper.setText(htmlContent, true);

            mailSender.send(mime);
            System.out.println("Email de notification développeur envoyé à: " + to);

        } catch (Exception e) {
            throw new RuntimeException("Échec de l'envoi de l'email de notification développeur", e);
        }
    }

    private String buildDeveloperEmailContent(String modelName, String clientName, String clientEmail, String planName,
            Double price, String currency, String startDate, Long subscriptionId, Long modelId) {

        DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        String formattedDate = java.time.LocalDate.parse(startDate).format(displayFormatter);

        // Calculer votre revenu (par exemple 70% du prix)
        Double developerRevenue = price * 0.7;

        return """
                <!DOCTYPE html>
                <html lang="fr">
                <head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Nouvel Abonné - AI+ Developer</title>
                    <style> /* styles… */ </style>
                </head>
                <body>
                  <div class="container">
                    <div class="header">
                      <h1>🎯 Nouvel Abonné !</h1>
                      <p>Votre modèle a un nouveau client</p>
                    </div>
                    <div class="content">
                      <h2>Félicitations ! Votre modèle <strong>%s</strong> a un nouvel abonné.</h2>
                      <div class="revenue-badge">💰 + %s %s de revenu généré</div>
                      <div class="details-card">
                        <h3>📋 Détails du Client</h3>
                        <p><strong>Nom :</strong> %s</p>
                        <p><strong>Email :</strong> %s</p>
                        <p><strong>Plan choisi :</strong> %s</p>
                        <p><strong>Prix :</strong> %s %s</p>
                        <p><strong>Votre revenu (70%%) :</strong> <strong>%s %s</strong></p>
                        <p><strong>Date d'activation :</strong> %s</p>
                        <p><strong>ID Abonnement :</strong> SUB-%s</p>
                        <p><strong>ID Modèle :</strong> MODEL-%s</p>
                      </div>
                    </div>
                  </div>
                </body>
                </html>
                """.formatted(modelName,
                // revenue
                developerRevenue, currency,
                // client info
                clientName, clientEmail, planName,
                // price
                price, currency,
                // revenue again
                developerRevenue, currency,
                // date
                formattedDate,
                // ids
                subscriptionId.toString(), modelId.toString());
    }

}