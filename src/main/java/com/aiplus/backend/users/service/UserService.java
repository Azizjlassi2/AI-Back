package com.aiplus.backend.users.service;

import java.util.List;

import com.aiplus.backend.users.model.User;

public interface UserService {
    /**
     * Retrieves all users in the system.
     *
     * @return a list of all users
     */
    List<User> getAllUsers();

    /**
     * Finds a user by their ID.
     *
     * @param id the ID of the user to find
     * @return the user with the specified ID
     */
    User findById(Long id);

    /**
     * Finds a user by their email address.
     *
     * @param email the email of the user to find
     * @return the user with the specified email
     */
    User findByEmail(String email);

    /**
     * Checks if a user with the given email already exists.
     *
     * @param email the email to check
     * @return true if a user with the email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Saves a new user to the system.
     *
     * @param user the user to be saved
     * @return the saved user
     */
    User saveUser(User user);

    /**
     * Updates an existing user.
     *
     * @param id   the ID of the user to update
     * @param user the user data to update
     * @return the updated user
     */
    User updateUser(Long id, User user);

    /**
     * Deletes a user by ID.
     *
     * @param id the ID of the user to delete
     */
    void deleteUser(Long id);

}
