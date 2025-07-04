package com.aiplus.backend.email.strategy;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.aiplus.backend.email.exceptions.PasswordResetEmailException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Component
public class PasswordResetEmailStrategy implements EmailStrategy {

    private final JavaMailSender mailSender;

    public PasswordResetEmailStrategy(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public boolean supports(EmailType type) {
        return type == EmailType.PASSWORD_RESET;
    }

    @Override
    public void sendEmail(String to, String resetUrl) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setTo(to);
            helper.setSubject("Password Reset Request");
            helper.setText(
                    "Hi,<br><br>" +
                            "Click the link to reset your password:<br>" +
                            "<a href=\"" + resetUrl + "\">Reset</a><br><br>" +
                            "If you didn't request this, ignore the message.",
                    true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new PasswordResetEmailException("Failed to send password reset email", e);
        }
    }
}
