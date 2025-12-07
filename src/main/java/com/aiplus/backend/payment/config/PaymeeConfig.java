package com.aiplus.backend.payment.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * Configuration class for Paymee payment gateway
 */
@Component
@ConfigurationProperties(prefix = "paymee")
@Data
public class PaymeeConfig {

    private String apiKey;

    private String apiUrl;

    private String merchantId;

    private String webhookUrl;

}
