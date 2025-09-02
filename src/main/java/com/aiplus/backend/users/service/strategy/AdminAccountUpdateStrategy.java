
package com.aiplus.backend.users.service.strategy;

import org.springframework.stereotype.Component;

import com.aiplus.backend.users.dto.AccountUpdateRequest;
import com.aiplus.backend.users.model.AdminAccount;
import com.aiplus.backend.users.model.User;
import com.aiplus.backend.users.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

/**
 * Strategy to update Admin accounts.
 */
@Component
@RequiredArgsConstructor
public class AdminAccountUpdateStrategy implements AccountUpdateStrategy {

    private final AccountRepository accountRepository;

    /**
     * Checks if the strategy supports the given user.
     *
     * @param user the user to check
     * @return true if the user is an admin, false otherwise
     */
    @Override
    public boolean supports(User user) {
        return user.isAdmin();
    }

    /**
     * Updates the Admin account with the provided request data.
     *
     * @param user    the authenticated user
     * @param request the update request containing new values
     * @return the updated Admin account
     */
    @Override
    public AdminAccount update(User user, AccountUpdateRequest request) {
        AdminAccount account = (AdminAccount) user.getAccount();
        // apply admin-specific updates if any
        accountRepository.save(account);
        return account;
    }
}
