package com.aiplus.backend.users.service;

import java.util.List;

import com.aiplus.backend.users.model.User;

public interface UserService {
    List<User> getAllUsers();

    User findById(Long id);

    User findByEmail(String email);

    boolean existsByEmail(String email);

    User save(User user);

    User updateUser(Long id, User user);

    void deleteUser(Long id);
}
