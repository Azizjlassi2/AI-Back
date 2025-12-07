package com.aiplus.backend.payment.gateways;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.aiplus.backend.payment.dto.KonnectPaymentInitRequest;
import com.aiplus.backend.payment.dto.KonnectPaymentInitResponse;
import com.aiplus.backend.payment.exception.PaymentInitializationException;

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

    public KonnectPaymentInitResponse initPayment(KonnectPaymentInitRequest request) {
        String url = konnectApiUrl + "/payments/init-payment";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", apiKey);

        HttpEntity<KonnectPaymentInitRequest> entity = new HttpEntity<>(request, headers);
        System.out.println("Initiating payment with request: " + request);
        System.out.println("Using API key: " + apiKey);
        System.out.println("Using Konnect API URL: " + konnectApiUrl);
        System.out.println("Using frontend URL: " + frontendUrl);
        System.out.println("Using Konnect webhook URL: " + konnectWebhookUrl);
        System.out.println("Initiating payment with URL: " + url);
        System.out.println("Request entity: " + entity);

        try {
            ResponseEntity<Map<String, Object>> response = rest.exchange(url, HttpMethod.POST, entity,
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    });

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new PaymentInitializationException(
                        "Konnect API returned non-success status: " + response.getStatusCode());
            }

            Map<String, Object> body = response.getBody();
            if (body == null || body.isEmpty()) {
                throw new PaymentInitializationException("Konnect API response body is empty");
            }

            KonnectPaymentInitResponse konnectResponse = KonnectPaymentInitResponse.builder()
                    .paymentRef((String) body.get("paymentRef")).payUrl((String) body.get("payUrl")).build();

            return konnectResponse;

        } catch (HttpClientErrorException e) {
            throw new PaymentInitializationException("Client error while calling Konnect API: " + e.getStatusCode()
                    + " - " + e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException e) {
            throw new PaymentInitializationException(
                    "Server error from Konnect API: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
        } catch (ResourceAccessException e) {
            throw new PaymentInitializationException("Failed to connect to Konnect API: " + e.getMessage(), e);
        } catch (PaymentInitializationException | RestClientException e) {
            throw new PaymentInitializationException("Unexpected error during payment initialization", e);
        }
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