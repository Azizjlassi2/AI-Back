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
    private LocalDateTime expiresAt;
    private String tokenType = "Bearer";

    private String email;
    private String username;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;

    /**
     * Constructor for LoginResponse with token and expiration time.
     *
     * @param token     the JWT token
     * @param expiresAt the expiration time of the token
     * @param email     the email of the user
     * @param username  the username of the user
     * @param role      the role of the user
     * @param createdAt the creation time of the user
     * @param updateAt  the last update time of the user
     */
    public LoginResponse(String token, LocalDateTime expiresAt, String email, String username, String role,
            LocalDateTime createdAt,
            LocalDateTime updateAt) {
        this.token = token;
        this.expiresAt = expiresAt;
        this.email = email;
        this.username = username;
        this.role = role;
        this.createdAt = createdAt;
        this.updateAt = updateAt;
    }

}
