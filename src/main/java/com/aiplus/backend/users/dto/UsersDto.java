package com.aiplus.backend.users.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class UsersDto {
    private Long id;
    private String email;
    private String username;
    private String role;
    private LocalDate createdAt;
}