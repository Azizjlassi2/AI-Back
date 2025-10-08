package com.aiplus.backend.users.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Represents update data for a Developer account.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DeveloperAccountUpdateRequest extends AccountUpdateRequest {

    private String web_site;
    private String bio;
    private String address;
    private String linkedin;
    private String github;
    @Pattern(regexp = "^[0-9]{8}$", message = "Phone number must be 8  digits")
    private String phone_number;

    private String docker_username;
    private String docker_pat;
    private String konnect_wallet_id;

    public String getDockerUsername() {
        return docker_username;
    }

    public String getDockerPat() {
        return docker_pat;
    }

}
