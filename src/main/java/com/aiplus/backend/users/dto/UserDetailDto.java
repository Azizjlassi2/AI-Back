package com.aiplus.backend.users.dto;

import java.time.LocalDate;

import org.springframework.data.annotation.ReadOnlyProperty;

import lombok.Data;

@Data
public class UserDetailDto {
    @ReadOnlyProperty
    private Long id;

    @ReadOnlyProperty
    private String email;

    private String username;

    @ReadOnlyProperty
    private String role;

    @ReadOnlyProperty
    private AccountDto account;

    @ReadOnlyProperty
    private LocalDate createdAt;

}