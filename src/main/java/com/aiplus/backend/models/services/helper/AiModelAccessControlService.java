package com.aiplus.backend.models.services.helper;

import org.springframework.stereotype.Service;

import com.aiplus.backend.models.exceptions.RoleBasedAccessDeniedException;
import com.aiplus.backend.users.model.DeveloperAccount;
import com.aiplus.backend.users.service.AccountService;
import com.aiplus.backend.users.service.UserService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiModelAccessControlService {
    private final UserService userService;

    /**
     * Validates that the given user ID corresponds to a Developer or Admin.
     * 
     * @param developerId ID of the user to validate
     * @throws RoleBasedAccessDeniedException if not Developer or Admin
     */
    public void validateDeveloperOrAdmin(Long developerId) {
        var user = userService.findById(developerId);
        if (!user.isDeveloper() && !user.isAdmin()) {
            throw new RoleBasedAccessDeniedException(user.getRole().toString());
        }
    }

    /**
     * Loads the DeveloperAccount associated with the userId.
     * 
     * @param developerId ID of the user to fetch
     * @return DeveloperAccount entity ready to attach to AiModel
     * @throws RoleBasedAccessDeniedException if user is not Developer/Admin
     * @throws EntityNotFoundException        if the user does not exist or account
     *                                        unavailable
     */
    public DeveloperAccount fetchDeveloperAccount(Long developerId) {
        validateDeveloperOrAdmin(developerId);
        var user = userService.findById(developerId);
        return (DeveloperAccount) user.getAccount();
    }
}
