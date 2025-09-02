package com.aiplus.backend.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aiplus.backend.users.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

}
