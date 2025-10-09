package com.aiplus.backend.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aiplus.backend.users.model.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

}
