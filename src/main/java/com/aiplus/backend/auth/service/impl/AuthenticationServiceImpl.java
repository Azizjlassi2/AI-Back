package com.aiplus.backend.auth.service.impl;

import java.time.LocalDateTime;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.aiplus.backend.auth.dto.LoginRequest;
import com.aiplus.backend.auth.dto.LoginResponse;
import com.aiplus.backend.auth.exceptions.InvalidCredentialsException;
import com.aiplus.backend.auth.exceptions.UserNotFoundException;
import com.aiplus.backend.auth.jwt.JwtTokenProvider;
import com.aiplus.backend.auth.service.AuthenticationService;
import com.aiplus.backend.users.model.User;
import com.aiplus.backend.users.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException("Incorrect email or password");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        String token = jwtTokenProvider.generateToken(user.getEmail());
        LocalDateTime expiresAt = jwtTokenProvider.getExpirationDateFromJWT(token).toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        return new LoginResponse(token, expiresAt, user.getEmail(), user.getName(), user.getRole().name(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }
}
