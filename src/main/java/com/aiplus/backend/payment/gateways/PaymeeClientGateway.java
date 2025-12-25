package com.aiplus.backend.payment.gateways;

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

import com.aiplus.backend.payment.config.PaymeeConfig;
import com.aiplus.backend.payment.dto.PaymeePaymentInitRequest;
import com.aiplus.backend.payment.dto.PaymeePaymentInitResponse;
import com.aiplus.backend.payment.exception.PaymentInitializationException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymeeClientGateway {

    private final PaymeeConfig paymeeConfig;
    private final RestTemplate rest = new RestTemplate();

    // init payment
    public PaymeePaymentInitResponse initPayment(PaymeePaymentInitRequest request) {
        String url = paymeeConfig.getApiUrl() + "/payments/create";

        // Set up headers and other request details as needed
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // add Authorization header to add token instead of apiKey
        headers.set("Authorization", "Token " + paymeeConfig.getApiKey());

        HttpEntity<PaymeePaymentInitRequest> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<PaymeePaymentInitResponse> response = rest.exchange(url, HttpMethod.POST, entity,
                    new ParameterizedTypeReference<PaymeePaymentInitResponse>() {
                    });

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new PaymentInitializationException(
                        "Paymee API returned non-success status: " + response.getStatusCode());
            }
            PaymeePaymentInitResponse body = response.getBody();
            if (body == null) {
                throw new PaymentInitializationException("Paymee API response body is empty");
            }
            System.out.println("Paymee API response " + body);

            log.info("Paymee Payment Init Response: " + body);
            return body;
        } catch (HttpClientErrorException e) {
            throw new PaymentInitializationException(
                    "Client error while calling Paymee API: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(),
                    e);
        } catch (HttpServerErrorException e) {
            throw new PaymentInitializationException(
                    "Server error from Paymee API: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
        } catch (ResourceAccessException e) {
            throw new PaymentInitializationException("Failed to connect to Paymee API: " + e.getMessage(), e);
        } catch (PaymentInitializationException | RestClientException e) {
            throw new PaymentInitializationException("Unexpected error during payment initialization", e);
        }

    }

}