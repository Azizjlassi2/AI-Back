package com.aiplus.backend.users.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Represents update data for a Client account.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ClientAccountUpdateRequest extends AccountUpdateRequest {

    private String web_site;
    private String bio;
    private String address;
    private String company;
    private String job_title;

    /**
     * Konnect wallet ID for receiving payments.
     */
    private String konnect_wallet_id;

    @Pattern(regexp = "^[0-9]{8}$", message = "Phone number must be 8  digits")
    private String phone_number;

}
