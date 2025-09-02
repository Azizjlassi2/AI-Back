package com.aiplus.backend.users.service;

import com.aiplus.backend.users.dto.AccountUpdateRequest;
import com.aiplus.backend.users.model.Account;
import com.aiplus.backend.users.model.User;

public interface AccountService {

    /**
     * Finds the account associated with the given user.
     *
     * @param user the user whose account is to be found
     * @return the user's account
     */
    Account findByUser(User user);

    /**
     * Creates a new account for the given user.
     *
     * @param user the user for whom the account is to be created
     * @return the newly created account
     */
    Account createAccount(User user);

    /**
     * Updates the user's account with the provided request data.
     *
     * @param user    the authenticated user
     * @param account the update request containing new values
     * @return the updated account
     */
    Account updateAccount(User user, AccountUpdateRequest account);

    /**
     * Updates the Docker credentials for the user's account.
     *
     * @param user           the authenticated user
     * @param dockerUsername the new Docker username
     * @param dockerPat      the new Docker Personal Access Token
     * @return the updated account with new Docker credentials
     */
    Account updateDockerCredentials(User user, String dockerUsername, String dockerPat);

}
