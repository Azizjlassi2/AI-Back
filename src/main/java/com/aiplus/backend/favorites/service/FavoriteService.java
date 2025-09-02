package com.aiplus.backend.favorites.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.aiplus.backend.auth.exceptions.UserNotFoundException;
import com.aiplus.backend.models.exceptions.AiModelNotFoundException;
import com.aiplus.backend.models.model.AiModel;
import com.aiplus.backend.models.repository.AiModelRepository;
import com.aiplus.backend.users.model.ClientAccount;
import com.aiplus.backend.users.model.User;
import com.aiplus.backend.users.repository.AccountRepository;
import com.aiplus.backend.users.service.AccountService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class FavoriteService {
    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final AiModelRepository modelRepository;

    /**
     * Adds a model to the user's favorites.
     * 
     * @param modelId the ID of the model to add
     * @param userId  the ID of the user
     * @return the updated list of favorite models
     */
    @SuppressWarnings("null")
    public List<AiModel> addModelToFavorites(Long modelId, User user) {

        ClientAccount userAccount = (ClientAccount) accountService.findByUser(user);

        AiModel model = modelRepository.findById(modelId)
                .orElseThrow(() -> new AiModelNotFoundException("Model with id " + modelId + " not found"));
        if (userAccount != null && model != null) {
            userAccount.getFavoriteModels().add(model);
        }
        return accountRepository.save(userAccount).getFavoriteModels();

    }

    // remove model from favorites
    public List<AiModel> removeModelFromFavorites(Long modelId, User user) {
        ClientAccount userAccount = (ClientAccount) accountService.findByUser(user);

        AiModel model = modelRepository.findById(modelId)
                .orElseThrow(() -> new AiModelNotFoundException("Model with id " + modelId + " not found"));
        if (userAccount != null && model != null) {
            userAccount.getFavoriteModels().remove(model);
            accountRepository.save(userAccount);
        }
        return accountRepository.save(userAccount).getFavoriteModels();
    }

    // get all favorite models for a user
    public List<AiModel> getFavoriteModelsForUser(Long userId) {
        ClientAccount userAccount = (ClientAccount) accountRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));
        return userAccount.getFavoriteModels();
    }
}
