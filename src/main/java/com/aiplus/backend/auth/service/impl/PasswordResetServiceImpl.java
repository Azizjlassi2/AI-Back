package com.aiplus.backend.auth.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.aiplus.backend.auth.dto.PasswordResetRequest;
import com.aiplus.backend.auth.dto.PasswordUpdateRequest;
import com.aiplus.backend.auth.exceptions.ExpiredTokenException;
import com.aiplus.backend.auth.exceptions.InvalidCredentialsException;
import com.aiplus.backend.auth.exceptions.InvalidTokenException;
import com.aiplus.backend.auth.exceptions.UserNotFoundException;
import com.aiplus.backend.auth.model.PasswordResetToken;
import com.aiplus.backend.auth.repository.PasswordResetTokenRepository;
import com.aiplus.backend.auth.service.PasswordResetService;
import com.aiplus.backend.auth.service.PasswordResetTokenFactory;
import com.aiplus.backend.config.FrontendProperties;
import com.aiplus.backend.email.service.EmailService;
import com.aiplus.backend.email.strategy.EmailType;
import com.aiplus.backend.users.model.User;
import com.aiplus.backend.users.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final FrontendProperties frontendProperties;

    @Override
    public void initiateReset(String email) {
        User user = userService.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        tokenRepository.deleteByUser(user);
        PasswordResetToken token = PasswordResetTokenFactory.createTokenForUser(user);
        tokenRepository.save(token);

        String resetUrl = frontendProperties.getUrl() + "/reset-password?token=" + token.getToken();

        emailService.sendEmail(EmailType.PASSWORD_RESET, user.getEmail(), resetUrl);
    }

    @Override
    public void resetPassword(PasswordResetRequest request) {
        PasswordResetToken token = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new InvalidTokenException("Invalid token"));

        if (token.isExpired()) {
            throw new ExpiredTokenException("Token expired");
        }

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userService.saveUser(user);
        tokenRepository.delete(token);
    }

    @Override
    public void updatePassword(User user, PasswordUpdateRequest request) {

        if (passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userService.saveUser(user);
        } else {
            throw new InvalidCredentialsException("Mot de passe Incorrect !");
        }
    }

}
