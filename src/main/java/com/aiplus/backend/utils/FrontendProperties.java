package com.aiplus.backend.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "frontend")
@Data
public class FrontendProperties {
    private String url;
}
