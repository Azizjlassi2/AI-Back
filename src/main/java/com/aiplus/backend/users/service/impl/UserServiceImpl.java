package com.aiplus.backend.users.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.aiplus.backend.users.exceptions.UserNotFoundException;
import com.aiplus.backend.users.factory.AccountFactory;
import com.aiplus.backend.users.model.User;
import com.aiplus.backend.users.repository.UserRepository;
import com.aiplus.backend.users.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final AccountFactory accountFactory;

    public UserServiceImpl(UserRepository userRepository, AccountFactory accountFactory) {
        this.userRepository = userRepository;
        this.accountFactory = accountFactory;

    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public User findByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User with email " + email + " not found");
        }
        return user;
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User saveUser(User user) {
        log.info("Saving user with email: {}", user.getEmail());
        if (!existsByEmail(user.getEmail())) {
            accountFactory.createAccountForUser(user);
            log.info("Account created for user with email: {}", user.getEmail());
        }
        log.info("User with email: {} saved successfully with account type: {}", user.getEmail(),
                user.getAccount().getClass().getSimpleName());

        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User user) {

        User oldUser = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));

        oldUser.setName(user.getName());

        return userRepository.save(oldUser);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }
}