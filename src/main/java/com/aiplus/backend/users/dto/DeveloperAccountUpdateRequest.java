package com.aiplus.backend.users.dto;

import lombok.Data;

/**
 * Represents update data for a Developer account.
 */
@Data
public class DeveloperAccountUpdateRequest extends AccountUpdateRequest {

    private String web_site;
    private String bio;
    private String phone_number;
    private String address;
    private String linkedin;
    private String github;

    private String docker_username;
    private String docker_pat;

    public String getDockerUsername() {
        return docker_username;
    }

    public String getDockerPat() {
        return docker_pat;
    }

}
