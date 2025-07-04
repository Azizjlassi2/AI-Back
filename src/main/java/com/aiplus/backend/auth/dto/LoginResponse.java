package com.aiplus.backend.auth.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {

    private String token;
    private String tokenType = "Bearer";
    private String email;
    private String username;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;

    public LoginResponse(String token, String email, String username, String role, LocalDateTime createdAt,
            LocalDateTime updateAt) {
        this.token = token;
        this.email = email;
        this.username = username;
        this.role = role;
        this.createdAt = createdAt;
        this.updateAt = updateAt;
    }

}
