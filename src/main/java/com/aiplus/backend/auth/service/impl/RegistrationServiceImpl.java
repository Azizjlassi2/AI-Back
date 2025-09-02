package com.aiplus.backend.auth.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.aiplus.backend.auth.dto.RegisterRequest;
import com.aiplus.backend.auth.dto.RegisterResponse;
import com.aiplus.backend.auth.exceptions.InvalidRoleException;
import com.aiplus.backend.auth.service.RegistrationService;
import com.aiplus.backend.users.model.Role;
import com.aiplus.backend.users.model.User;
import com.aiplus.backend.users.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Override
    public RegisterResponse register(RegisterRequest request) {
        if (userService.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getUsername());

        try {
            user.setRole(Role.valueOf(request.getRole().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new InvalidRoleException(request.getRole());
        }

        userService.saveUser(user);
        return new RegisterResponse("User registered successfully", user.getEmail());
    }
}
