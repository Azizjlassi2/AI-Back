package com.aiplus.backend.auth.service;

import com.aiplus.backend.auth.dto.RegisterRequest;
import com.aiplus.backend.auth.dto.RegisterResponse;

public interface RegistrationService {
    RegisterResponse register(RegisterRequest request);
}
