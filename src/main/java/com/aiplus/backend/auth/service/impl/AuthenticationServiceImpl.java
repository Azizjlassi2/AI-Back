package com.aiplus.backend.auth.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.aiplus.backend.auth.dto.LoginRequest;
import com.aiplus.backend.auth.dto.LoginResponse;
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
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        String token = jwtTokenProvider.generateToken(user.getEmail());
        return new LoginResponse(token, user.getEmail(), user.getName(), user.getRole().name(), user.getCreatedAt(),
                user.getUpdatedAt());
    }
}
