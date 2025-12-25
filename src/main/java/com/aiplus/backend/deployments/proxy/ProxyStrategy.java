package com.aiplus.backend.deployments.proxy;

import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ServerWebExchange;

public interface ProxyStrategy {
    ResponseEntity<?> forwardRequest(ServerWebExchange exchange, String targetUrl);
}