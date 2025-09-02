package com.aiplus.backend.users.service.strategy;

import org.springframework.stereotype.Component;

import com.aiplus.backend.users.dto.AccountUpdateRequest;
import com.aiplus.backend.users.dto.ClientAccountUpdateRequest;
import com.aiplus.backend.users.model.ClientAccount;
import com.aiplus.backend.users.model.User;
import com.aiplus.backend.users.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

/**
 * Strategy to update Client accounts.
 */
@Component
@RequiredArgsConstructor
public class ClientAccountUpdateStrategy implements AccountUpdateStrategy {

    private final AccountRepository accountRepository;

    /**
     * Checks if the strategy supports the given user.
     *
     * @param user the user to check
     * @return true if the user is a client, false otherwise
     */
    @Override
    public boolean supports(User user) {
        return user.isClient();
    }

    /**
     * Updates the Client account with the provided request data.
     *
     * @param user    the authenticated user
     * @param request the update request containing new values
     * @return the updated Client account
     */
    @Override
    public ClientAccount update(User user, AccountUpdateRequest request) {
        throw new UnsupportedOperationException("Client account updates are not fully implemented yet.");
        /*
         * ClientAccountUpdateRequest req = (ClientAccountUpdateRequest) request;
         * ClientAccount account = (ClientAccount) user.getAccount();
         * // update fields
         * accountRepository.save(account);
         * return account;
         */
    }
}
