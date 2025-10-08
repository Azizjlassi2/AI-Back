package com.aiplus.backend.users.service.strategy;

import org.springframework.stereotype.Component;

import com.aiplus.backend.users.dto.AccountUpdateRequest;
import com.aiplus.backend.users.dto.DeveloperAccountUpdateRequest;
import com.aiplus.backend.users.model.DeveloperAccount;
import com.aiplus.backend.users.model.User;
import com.aiplus.backend.users.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

/**
 * Strategy to update Developer accounts.
 */
@Component
@RequiredArgsConstructor
public class DeveloperAccountUpdateStrategy implements AccountUpdateStrategy {

    private final AccountRepository accountRepository;

    /**
     * Checks if the strategy supports the given user.
     *
     * @param user the user to check
     * @return true if the user is a developer, false otherwise
     */
    @Override
    public boolean supports(User user) {
        return user.isDeveloper();
    }

    /**
     * Updates the Developer account with the provided request data.
     *
     * @param user    the authenticated user
     * @param request the update request containing new values
     * @return the updated Developer account
     */
    @Override

    public DeveloperAccount update(User user, AccountUpdateRequest request) {

        DeveloperAccountUpdateRequest req = (DeveloperAccountUpdateRequest) request;
        DeveloperAccount account = (DeveloperAccount) user.getAccount();

        // Personal Information
        account.setWeb_site(req.getWeb_site());
        account.setAddress(req.getAddress());
        account.setBio(req.getBio());
        account.setGithub(req.getGithub());
        account.setLinkedin(req.getLinkedin());
        account.setPhone_number(req.getPhone_number());
        account.setKonnectWalletId(req.getKonnect_wallet_id());

        // Docker Credentials
        account.setDockerUsername(req.getDockerUsername());
        account.setDockerPat(req.getDockerPat());

        // Other Information

        accountRepository.save(account);
        return account;
    }
}