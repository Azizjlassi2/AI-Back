package com.aiplus.backend.auth.contoller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aiplus.backend.auth.dto.LoginRequest;
import com.aiplus.backend.auth.dto.LoginResponse;
import com.aiplus.backend.auth.dto.PasswordResetRequest;
import com.aiplus.backend.auth.dto.PasswordUpdateRequest;
import com.aiplus.backend.auth.dto.RegisterRequest;
import com.aiplus.backend.auth.dto.RegisterResponse;
import com.aiplus.backend.auth.service.AuthenticationService;
import com.aiplus.backend.auth.service.PasswordResetService;
import com.aiplus.backend.auth.service.RegistrationService;
import com.aiplus.backend.users.model.User;
import com.aiplus.backend.utils.responses.ApiResponse;
import com.aiplus.backend.utils.responses.ResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;
    private final RegistrationService registrationService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest req) {

        LoginResponse resp = authenticationService.login(req);

        return ResponseEntity.ok(ResponseUtil.success("Login successful", resp));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@RequestBody RegisterRequest req) {
        RegisterResponse resp = registrationService.register(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.success("User registered successfully", resp));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestParam String email) {
        passwordResetService.initiateReset(email);
        return ResponseEntity.ok(ResponseUtil.success("Password reset link sent.", null));

    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody PasswordResetRequest req) {
        passwordResetService.resetPassword(req);
        return ResponseEntity.ok(ResponseUtil.success("Password has been reset", null));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        authenticationService.logout(request, response);
        return ResponseEntity.ok().body("Logged out successfully");
    }

    @PutMapping("/update-password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(@AuthenticationPrincipal User user,
            @RequestBody PasswordUpdateRequest req) {
        passwordResetService.updatePassword(user, req);
        return ResponseEntity.ok(ResponseUtil.success("Password has been updated", null));
    }

}