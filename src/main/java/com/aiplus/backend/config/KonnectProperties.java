package com.aiplus.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "konnect.webhook")
@Data
public class KonnectProperties {
    private String url;

}
