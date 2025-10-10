package com.aiplus.backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Configuration class for detailed HTTP request logging. Provides richer
 * context for debugging and observability.
 */
@Configuration
public class RequestLoggingConfig {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingConfig.class);

    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter() {

            @Override
            protected void beforeRequest(HttpServletRequest request, String message) {
                log.info(buildDetailedMessage("Incoming Request", request));
            }

            @Override
            protected void afterRequest(HttpServletRequest request, String message) {
                log.info(buildDetailedMessage("Request Completed", request));
            }

            private String buildDetailedMessage(String phase, HttpServletRequest request) {
                StringBuilder msg = new StringBuilder();
                msg.append("\n==================== ").append(phase).append(" ====================\n")
                        .append("➡️  Method: ").append(request.getMethod()).append("\n").append("🌍  URI: ")
                        .append(request.getRequestURI())
                        .append(request.getQueryString() != null ? "?" + request.getQueryString() : "").append("\n")
                        .append("📡  Remote Address: ").append(request.getRemoteAddr()).append("\n")
                        .append("👤  User-Agent: ").append(request.getHeader("User-Agent")).append("\n")
                        .append("🔑  Authorization: ")
                        .append(request.getHeader("Authorization") != null ? "[PRESENT]" : "[NONE]").append("\n")
                        .append("📦  Content-Type: ").append(request.getContentType()).append("\n")
                        .append("📏  Content-Length: ").append(request.getContentLengthLong()).append("\n")
                        .append("=============================================================");
                return msg.toString();
            }
        };

        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setIncludeHeaders(true);
        filter.setMaxPayloadLength(10000); // Limit payload size to prevent log flooding
        filter.setAfterMessagePrefix(""); // We override before/after messages manually

        return filter;
    }
}
