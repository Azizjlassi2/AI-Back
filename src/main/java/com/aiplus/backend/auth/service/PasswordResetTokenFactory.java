package com.aiplus.backend.auth.service;

import com.aiplus.backend.auth.model.PasswordResetToken;
import com.aiplus.backend.users.model.User;

import java.util.UUID;

public class PasswordResetTokenFactory {
    public static PasswordResetToken createTokenForUser(User user) {
        return new PasswordResetToken(UUID.randomUUID().toString(), user);
    }
}