package com.aiplus.backend.email.strategy;

public interface EmailStrategy {
    boolean supports(EmailType type);

    void sendEmail(String to, String payload);
}
