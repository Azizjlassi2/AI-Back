package com.aiplus.backend.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aiplus.backend.users.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Find a user by their email.
     */
    User findByEmail(String email);

    /**
     * Find a user by their name.
     */
    User findByName(String name);

    /**
     * Check if a user exists by their email.
     */

    boolean existsByEmail(String email);

    /**
     * Check if a user exists by their name.
     */
    boolean existsByName(String name);

}
