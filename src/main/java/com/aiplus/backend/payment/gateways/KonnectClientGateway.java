package com.aiplus.backend.payment.gateways;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.aiplus.backend.config.FrontendProperties;

import lombok.RequiredArgsConstructor;

/**
 * Client for Konnect payment gateway API
 */
@Component
@RequiredArgsConstructor
public class KonnectClientGateway {

    @Value("${konnect.api.url}")
    private String konnectApiUrl;

    @Value("${konnect.api.key}")
    private String apiKey;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Value("${app.konnect.webhook.url}")
    private String konnectWebhookUrl;

    private final RestTemplate rest = new RestTemplate();

    private final FrontendProperties frontendProperties;

    /**
     * Initiate a payment request to Konnect
     * 
     */
    public Map<String, Object> initPayment(String receiverWalletId, long amountInMillimes, String orderId,
            String description, Long modelId) {

        String successUrl = frontendProperties.getUrl() + "/client/subscriptions/"; // TODO: make configurable
        String url = konnectApiUrl + "/payments/init-payment";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", apiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("receiverWalletId", receiverWalletId);
        body.put("token", "TND"); // or dynamic
        body.put("amount", amountInMillimes); // in millimes
        body.put("orderId", orderId);
        body.put("acceptedPaymentMethods", List.of("wallet", "bank_card", "e-DINAR"));
        body.put("webhook", konnectWebhookUrl);
        body.put("silentWebhook", true);
        body.put("description", description);
        body.put("successUrl", successUrl);

        HttpEntity<Map<String, Object>> req = new HttpEntity<>(body, headers);

        ResponseEntity<Map> resp = rest.postForEntity(url, req, Map.class);
        return resp.getBody();
    }

    /**
     * Get payment details from Konnect
     */
    public Map<String, Object> getPaymentDetails(String paymentRef) {
        String url = konnectApiUrl + "/payments/" + paymentRef;
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);
        HttpEntity<Void> req = new HttpEntity<>(headers);
        ResponseEntity<Map> resp = rest.exchange(url, HttpMethod.GET, req, Map.class);
        return resp.getBody();
    }

}