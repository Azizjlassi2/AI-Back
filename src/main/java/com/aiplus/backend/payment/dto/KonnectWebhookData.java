package com.aiplus.backend.payment.dto;

import lombok.Builder;
import lombok.Data;

/**
 * DTO representing the data received from Konnect webhook notifications.
 * Currently empty, can be extended based on webhook payload structure.
 */
@Data
@Builder
public class KonnectWebhookData {

    private String payment_ref;

}
