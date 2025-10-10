package com.aiplus.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class KonnectProperties {

    @Value("${app.konnect.webhook.url}")
    private String url;

    @Value("${konnect.base.url}")
    private String baseUrl;

}
