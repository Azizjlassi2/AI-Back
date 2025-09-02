package com.aiplus.backend.users.dto;

import lombok.Data;

@Data
public class UserCreationDto {
    private String email;
    private String password;
    private String username;
    private String role;

}