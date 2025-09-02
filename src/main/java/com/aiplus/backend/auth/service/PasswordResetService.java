package com.aiplus.backend.auth.service;

import com.aiplus.backend.auth.dto.PasswordResetRequest;
import com.aiplus.backend.auth.dto.PasswordUpdateRequest;
import com.aiplus.backend.users.model.User;

public interface PasswordResetService {
    void initiateReset(String email);

    void resetPassword(PasswordResetRequest request);

    void updatePassword(User user, PasswordUpdateRequest request);
}