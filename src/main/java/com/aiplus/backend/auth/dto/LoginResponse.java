package com.aiplus.backend.auth.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.aiplus.backend.users.dto.AccountDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {

    private String token;
    private String refresh_token;
    private LocalDateTime expiresAt;

    private final String tokenType = "Bearer";

    private String email;
    private String username;
    private AccountDto account;
    private String role;
    private LocalDate createdAt;
    private LocalDate updateAt;

}
