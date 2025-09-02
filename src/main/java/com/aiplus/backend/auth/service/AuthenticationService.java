package com.aiplus.backend.auth.service;

import com.aiplus.backend.auth.dto.LoginRequest;
import com.aiplus.backend.auth.dto.LoginResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthenticationService {
    LoginResponse login(LoginRequest request);

    void logout(HttpServletRequest request, HttpServletResponse response);
}