package com.aiplus.backend.email.strategy;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.aiplus.backend.email.exceptions.AccountActivationEmailException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Component
public class AccountActivationEmailStrategy implements EmailStrategy {

    private final JavaMailSender mailSender;

    public AccountActivationEmailStrategy(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public boolean supports(EmailType type) {
        return type == EmailType.ACCOUNT_ACTIVATION;
    }

    @Override
    public void sendEmail(String to, String activationUrl) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setTo(to);
            helper.setSubject("Account Activation - Ai+");
            helper.setText(
                    "Hi,<br><br>" +
                            "Welcome to Ai+! Please activate your account by clicking the link below:<br><br>" +
                            "<a href=\"" + activationUrl + "\">Activate Account</a><br><br>" +
                            "If you did not sign up for Ai+, please ignore this email.<br><br>" +
                            "Thank you,<br>" +
                            "The Ai+ Team",
                    true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new AccountActivationEmailException("Failed to send account activation email", e);
        }
    }
}