package com.aiplus.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * Properties for Konnect integration. Maps to configuration properties prefixed
 * with "konnect". Includes webhook URL and base URL for CORS configuration.
 */
@Component
@Data
public class KonnectProperties {

    @Value("${app.konnect.webhook.url}")
    private String url;

    @Value("${konnect.base.url}")
    private String baseUrl;

}
