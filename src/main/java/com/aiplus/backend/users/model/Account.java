package com.aiplus.backend.users.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;

/**
 * Represents an abstract account entity that is part of a single table
 * inheritance strategy.
 * This entity is mapped to a database table, with different account types
 * distinguished
 */
@Entity
@Data
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user associated with this account. This is a one-to-one relationship,
     * with a foreign key column named "user_id" in the account table.
     */
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

}
