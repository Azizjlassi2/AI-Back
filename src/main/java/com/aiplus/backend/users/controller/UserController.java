package com.aiplus.backend.users.controller;

import java.util.List;
import java.util.stream.Collectors;

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

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    // getAllUsers
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserDto> userDtos = users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDtos);
    }

    // getUserById
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        UserDto userDto = userMapper.toDto(user);
        return ResponseEntity.ok(userDto);
    }

    // createUser
    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        if (userService.existsByEmail(userDto.getEmail())) {
            throw new EmailAlreadyUsedException(userDto.getEmail());
            // UserDto has @Data annotation
        }
        User createdUser = userService.save(userMapper.toEntity(userDto));
        UserDto createdUserDto = userMapper.toDto(createdUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUserDto);
    }

    // updateUser
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        if (userService.findById(id) == null) {
            throw new UserNotFoundException(id);
        }
        User updatedUser = userService.updateUser(id, userMapper.toEntity(userDto));
        UserDto updatedUserDto = userMapper.toDto(updatedUser);
        return ResponseEntity.ok(updatedUserDto);
    }

    // deleteUser
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.findById(id) == null) {
            throw new UserNotFoundException(id);
        }
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // getUserByEmail
    @GetMapping("/email")
    public ResponseEntity<UserDto> getUserByEmail(@RequestParam String email) {
        User user = userService.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User with email " + email + " not found");
        }
        UserDto userDto = userMapper.toDto(user);
        return ResponseEntity.ok(userDto);
    }
}
