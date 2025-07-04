package com.aiplus.backend.email.strategy;

/**
 * Enum representing different types of emails that can be sent.
 * This is used to identify the type of email being processed by the
 * EmailStrategy.
 * PASSWORD_RESET: Email sent for password reset requests.
 * ACCOUNT_ACTIVATION: Email sent for account activation.
 * CONTACT_MESSAGE: Email sent for contact messages.
 */
public enum EmailType {

    PASSWORD_RESET,
    ACCOUNT_ACTIVATION,
    CONTACT_MESSAGE
}
