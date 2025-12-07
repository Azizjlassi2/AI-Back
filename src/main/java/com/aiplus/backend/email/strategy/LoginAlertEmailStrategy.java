package com.aiplus.backend.email.strategy;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Component
public class LoginAlertEmailStrategy implements EmailStrategy {

    private final JavaMailSender mailSender;

    public LoginAlertEmailStrategy(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public boolean supports(EmailType type) {
        return type == EmailType.LOGIN_ALERT;
    }

    @Override
    public void sendEmail(String to, String payload) {
        try {
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, "utf-8");

            helper.setTo(to);
            helper.setSubject("Security Alert: New Login Detected");
            helper.setText("""
                    <h2>Security Alert</h2>
                    <p>We detected a new login to your Ai+ account.</p>
                    <p>If this was not you, please reset your password immediately.</p>
                    """, true);

            mailSender.send(mime);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send login alert email", e);
        }
    }
}
