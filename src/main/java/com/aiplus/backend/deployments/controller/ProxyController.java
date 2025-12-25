package com.aiplus.backend.deployments.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.aiplus.backend.deployments.exceptions.InactiveSubscriptionException;
import com.aiplus.backend.deployments.exceptions.InstanceNotDeployedException;
import com.aiplus.backend.deployments.exceptions.InvalidApiKeyException;
import com.aiplus.backend.deployments.proxy.ProxyService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/models/{modelId}")
@RequiredArgsConstructor
public class ProxyController {

    private final ProxyService proxyService;

    @RequestMapping(value = "/**", method = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
            RequestMethod.DELETE })
    public Mono<ResponseEntity<String>> proxyRequest(@PathVariable long modelId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader, @RequestHeader HttpHeaders headers,
            @RequestBody(required = false) String body, HttpServletRequest request) {

        String rawApiKey = extractBearerToken(authHeader);

        if (rawApiKey == null || rawApiKey.isEmpty()) {
            return Mono.just(ResponseEntity.status(401).body("Missing API key"));
        }

        String path = extractPathFromRequest(request, modelId); // e.g., request.getServletPath().substring(...)
        String method = request.getMethod(); // Injectez HttpServletRequest si besoin

        return proxyService.forwardRequest(modelId, path, method, body, rawApiKey)
                .map(response -> ResponseEntity.ok(response))
                .onErrorResume(InvalidApiKeyException.class,
                        e -> Mono.just(ResponseEntity.status(401).body(e.getMessage())))
                .onErrorResume(InactiveSubscriptionException.class,
                        e -> Mono.just(ResponseEntity.status(403).body(e.getMessage())))
                .onErrorResume(InstanceNotDeployedException.class,
                        e -> Mono.just(ResponseEntity.status(403).body(e.getMessage())))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    private String extractBearerToken(String authHeader) {
        return authHeader != null && authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null;
    }

    /**
     * Extrait le sub-path après /v1/models/{modelName}/ Exemple: Pour URI
     * "/v1/models/arabicbert/predict", retourne "/predict"
     */
    private String extractPathFromRequest(HttpServletRequest request, Long modelId) {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String prefix = contextPath + "/v1/models/" + modelId + "/";
        if (uri.startsWith(prefix)) {
            String subPath = uri.substring(prefix.length() - 1); // Inclut le / initial

            int queryIndex = subPath.indexOf('?');
            return queryIndex > 0 ? subPath.substring(0, queryIndex) : subPath;
        }
        return "";
    }
}