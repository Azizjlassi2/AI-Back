package com.aiplus.backend.users.service.strategy;

import com.aiplus.backend.users.dto.AccountUpdateRequest;
import com.aiplus.backend.users.model.Account;
import com.aiplus.backend.users.model.User;

/**
 * Defines the contract for account update strategies based on user role.
 */
public interface AccountUpdateStrategy {
    /**
     * Checks if this strategy supports updating the given user's account.
     *
     * @param user the user whose account will be updated
     * @return true if supported, false otherwise
     */
    boolean supports(User user);

    /**
     * Updates the user's account using the provided request.
     *
     * @param user    the user whose account is being updated
     * @param request the update request containing new values
     * @return a DTO representing the updated account
     */
    Account update(User user, AccountUpdateRequest request);
}