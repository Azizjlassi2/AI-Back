package com.aiplus.backend.email.strategy;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Email strategy for payment successful notifications
 * 
 */
@Component
public class PaymentSuccessfulEmailStrategy implements EmailStrategy {

    private final JavaMailSender mailSender;
    private final ObjectMapper objectMapper;

    public PaymentSuccessfulEmailStrategy(JavaMailSender mailSender, ObjectMapper objectMapper) {
        this.mailSender = mailSender;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(EmailType type) {
        return type == EmailType.PAYMENT_SUCCESSFUL;
    }

    @Override
    public void sendEmail(String to, String payload) {
        try {
            // Parse le payload JSON
            Map<String, Object> data = objectMapper.readValue(payload, HashMap.class);

            String clientName = (String) data.get("clientName");
            String modelName = (String) data.get("modelName");
            String planName = (String) data.get("planName");
            Double price = (Double) data.get("price");
            String currency = (String) data.get("currency");
            String startDate = (String) data.get("startDate");
            Long subscriptionId = data.get("subscriptionId") != null
                    ? Long.valueOf(data.get("subscriptionId").toString())
                    : null;
            String apiKey = (String) data.get("apiKey");

            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, "utf-8");

            helper.setTo(to);
            helper.setSubject("🎉 Confirmation de Paiement - Votre Abonnement AI+ est Activé !");

            // Construction du corps HTML amélioré
            String htmlContent = buildHtmlEmailContent(clientName, modelName, planName, price, currency, startDate,
                    subscriptionId, apiKey);

            helper.setText(htmlContent, true);

            // Optionnel: Ajouter des pièces jointes ou des images inline
            // helper.addInline("logo", new ClassPathResource("static/images/logo.png"));

            mailSender.send(mime);
            System.out.println("Email de confirmation de paiement envoyé à: " + to);

        } catch (Exception e) {
            throw new RuntimeException("Échec de l'envoi de l'email de confirmation de paiement", e);
        }
    }

    private String buildHtmlEmailContent(String clientName, String modelName, String planName, Double price,
            String currency, String startDate, Long subscriptionId, String apiKey) {

        DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        String formattedDate = java.time.LocalDate.parse(startDate).format(displayFormatter);

        // Construire HTML avec concaténation + placeholders %s
        String htmlTemplate = """
                <!DOCTYPE html>
                <html lang="fr">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1.0">
                  <title>Confirmation de Paiement - AI+</title>
                  <style>
                    /* styles simplifiés ou existants */
                    body { font-family: Arial, sans-serif; margin:0; padding:0; background:#f4f4f4; }
                    .container { max-width:600px; margin:auto; background:white; padding:20px; }
                    .header { background:#667eea; color:white; padding:20px; text-align:center; }
                    .content { padding:20px; }
                    .api-key-box { font-family: monospace; background:#f0f0f0; padding:10px; word-break: break-all; }
                  </style>
                </head>
                <body>
                  <div class="container">
                    <div class="header">
                      <h1>Paiement Confirmé !</h1>
                    </div>
                    <div class="content">
                      <p>Bonjour <strong>%s</strong>,</p>
                      <p>Votre paiement pour le plan <strong>%s / %s</strong> a été traité avec succès.</p>
                      <p><strong>Montant :</strong> %s %s</p>
                      <p><strong>Date d'activation :</strong> %s</p>
                      <p><strong>ID abonnement :</strong> SUB-%s</p>
                      <div class="api-key-box">
                        API Key : %s
                      </div>
                      <p>Merci pour votre confiance !</p>
                    </div>
                  </div>
                </body>
                </html>
                """;

        return String.format(htmlTemplate, clientName, modelName, planName, price.toString(), currency, formattedDate,
                subscriptionId.toString(), apiKey != null ? apiKey : "");
    }

}