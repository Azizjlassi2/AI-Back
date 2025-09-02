package com.aiplus.backend.users.dto;

import lombok.Data;

@Data
public class UserSummaryDto {
    private Long id;
    private String username;
    private String email;
    private String role;

}
