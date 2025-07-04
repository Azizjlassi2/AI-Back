package com.aiplus.backend.auth.service;

import com.aiplus.backend.auth.dto.PasswordResetRequest;

public interface PasswordResetService {
    void initiateReset(String email);

    void resetPassword(PasswordResetRequest request);
}