package com.aiplus.backend.email.strategy;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Component
public class PaymentSuccessfulEmailStrategy implements EmailStrategy {

    private final JavaMailSender mailSender;

    public PaymentSuccessfulEmailStrategy(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public boolean supports(EmailType type) {
        return type == EmailType.PAYMENT_SUCCESSFUL;
    }

    @Override
    public void sendEmail(String to, String payload) {
        try {
            var json = new ObjectMapper().readTree(payload);
            String amount = json.get("amount").asText();
            String invoiceUrl = json.get("invoiceUrl").asText();

            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, "utf-8");

            helper.setTo(to);
            helper.setSubject("Payment Confirmation - Ai+");
            helper.setText("""
                    <h3>Payment Successful</h3>
                    <p>Amount: %s</p>
                    <p><a href="%s">Download Invoice</a></p>
                    """.formatted(amount, invoiceUrl), true);

            mailSender.send(mime);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send payment confirmation email", e);
        }
    }
}
