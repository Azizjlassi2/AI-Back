package com.aiplus.backend.email.strategy;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.aiplus.backend.email.exceptions.ContactMessageEmailException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Component
public class ContactMessageEmailStrategy implements EmailStrategy {

    private final JavaMailSender mailSender;

    public ContactMessageEmailStrategy(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public boolean supports(EmailType type) {
        return type == EmailType.CONTACT_MESSAGE;
    }

    @Override
    public void sendEmail(String to, String unusedPayload) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setTo(to);
            helper.setSubject("Message Received - Ai+");
            helper.setText(
                    "Hi " + to + ",<br><br>" +
                            "Thank you for reaching out to Ai+. We have received your message and our team will get back to you as soon as possible.<br><br>"
                            +
                            "If you have any additional information to share, feel free to reply to this email.<br><br>"
                            +
                            "Best regards,<br>" +
                            "The Ai+ Team",
                    true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new ContactMessageEmailException("Failed to send contact confirmation email", e);
        }
    }
}