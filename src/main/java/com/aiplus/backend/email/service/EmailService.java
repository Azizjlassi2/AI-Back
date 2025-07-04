package com.aiplus.backend.email.service;

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.aiplus.backend.email.exceptions.NoEmailStrategyFoundException;
import com.aiplus.backend.email.strategy.EmailStrategy;
import com.aiplus.backend.email.strategy.EmailType;

@Async
@Service
public class EmailService {

    private final List<EmailStrategy> strategies;

    public EmailService(List<EmailStrategy> strategies) {
        this.strategies = strategies;
    }

    public void sendEmail(EmailType type, String to, String payload) {
        strategies.stream()
                .filter(s -> s.supports(type))
                .findFirst()
                .orElseThrow(() -> new NoEmailStrategyFoundException("No email strategy found for type: " + type))
                .sendEmail(to, payload);
    }
}