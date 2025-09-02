package com.aiplus.backend.users.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.aiplus.backend.users.exceptions.UserNotFoundException;
import com.aiplus.backend.users.factory.AccountFactory;
import com.aiplus.backend.users.model.Account;
import com.aiplus.backend.users.model.User;
import com.aiplus.backend.users.repository.UserRepository;
import com.aiplus.backend.users.service.UserService;

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

        if (!existsByEmail(user.getEmail())) {
            Account account = accountFactory.createAccountForUser(user);
            user.setAccount(account);
        }
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