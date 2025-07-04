package com.aiplus.backend.auth.service;

import com.aiplus.backend.auth.dto.LoginRequest;
import com.aiplus.backend.auth.dto.LoginResponse;

public interface AuthenticationService {
    LoginResponse login(LoginRequest request);
}