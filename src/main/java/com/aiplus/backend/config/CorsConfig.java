package com.aiplus.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final FrontendProperties frontendProperties;
    private final KonnectProperties konnectProperties;

    public CorsConfig(FrontendProperties frontendProperties, KonnectProperties konnectProperties) {
        this.frontendProperties = frontendProperties;
        this.konnectProperties = konnectProperties;
    }

    @Override
    public void addCorsMappings(@org.springframework.lang.NonNull CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins(frontendProperties.getUrl(), konnectProperties.getUrl())
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS").allowedHeaders("*").allowCredentials(true);
    }
}
