package com.aiplus.backend.users.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aiplus.backend.users.dto.UserDto;
import com.aiplus.backend.users.exceptions.EmailAlreadyUsedException;
import com.aiplus.backend.users.exceptions.UserNotFoundException;
import com.aiplus.backend.users.mapper.UserMapper;
import com.aiplus.backend.users.model.User;
import com.aiplus.backend.users.service.UserService;
import com.aiplus.backend.utils.responses.ApiResponse;
import com.aiplus.backend.utils.responses.ResponseUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers() {
        List<UserDto> dtos = userService.getAllUsers().stream()
                .map(userMapper::toDto)
                .toList();
        return ResponseEntity.ok(ResponseUtil.success("Users fetched", dtos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable Long id) {
        UserDto dto = userMapper.toDto(userService.findById(id));
        return ResponseEntity.ok(ResponseUtil.success("User found", dto));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserDto>> createUser(@Valid @RequestBody UserDto userDto) {
        if (userService.existsByEmail(userDto.getEmail())) {
            throw new EmailAlreadyUsedException(userDto.getEmail());
        }
        UserDto created = userMapper.toDto(
                userService.save(userMapper.toEntity(userDto)));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseUtil.success("User created", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> updateUser(@PathVariable Long id,
            @Valid @RequestBody UserDto userDto) {
        UserDto updated = userMapper.toDto(
                userService.updateUser(id, userMapper.toEntity(userDto)));
        return ResponseEntity.ok(ResponseUtil.success("User updated", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ResponseUtil.success("User deleted", null));
    }

    @GetMapping("/email")
    public ResponseEntity<ApiResponse<UserDto>> getUserByEmail(@RequestParam String email) {
        User user = userService.findByEmail(email);
        if (user == null)
            throw new UserNotFoundException("User with email " + email + " not found");
        return ResponseEntity.ok(
                ResponseUtil.success("User found", userMapper.toDto(user)));
    }
}
