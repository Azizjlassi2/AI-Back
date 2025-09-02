package com.aiplus.backend.auth.service.impl;

import java.time.LocalDateTime;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import com.aiplus.backend.auth.dto.LoginRequest;
import com.aiplus.backend.auth.dto.LoginResponse;
import com.aiplus.backend.auth.exceptions.InvalidCredentialsException;
import com.aiplus.backend.auth.jwt.JwtTokenProvider;
import com.aiplus.backend.auth.model.RefreshToken;
import com.aiplus.backend.auth.service.AuthenticationService;
import com.aiplus.backend.auth.service.RefreshTokenService;
import com.aiplus.backend.users.mapper.AccountMapper;
import com.aiplus.backend.users.model.User;
import com.aiplus.backend.users.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final AccountMapper accountMapper;

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException("Incorrect Email or Password");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userService.findByEmail(request.getEmail());

        String token = jwtTokenProvider.generateToken(user.getEmail());
        RefreshToken rt = refreshTokenService.createForUser(user);
        LocalDateTime expiresAt = jwtTokenProvider.getExpirationDateFromJWT(token).toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();

        return new LoginResponse(token, rt.getToken(), expiresAt, user.getEmail(), user.getName(),
                accountMapper.toAccountDto(user.getAccount()),
                user.getRole().name(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        // Retrieves the current authentication information from the SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Checks if the user is authenticated (authentication is not null and
        // authenticated)
        if (authentication != null && authentication.isAuthenticated()) {

            // Logs out the user by clearing their authentication info from the
            // SecurityContext
            new SecurityContextLogoutHandler().logout(request, response, authentication);

        }
    }
}
