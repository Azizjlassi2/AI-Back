package com.aiplus.backend.models.services.impl;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aiplus.backend.docker.exceptions.DockerImageNotFoundException;
import com.aiplus.backend.docker.service.DockerImageVerifier;
import com.aiplus.backend.models.dto.AiModelCreateDto;
import com.aiplus.backend.models.dto.AiModelDto;
import com.aiplus.backend.models.dto.AiModelSummaryDto;
import com.aiplus.backend.models.exceptions.AiModelNameAlreadyExistsException;
import com.aiplus.backend.models.exceptions.AiModelNotFoundException;
import com.aiplus.backend.models.exceptions.ModelAccessDeniedException;
import com.aiplus.backend.models.mapper.AiModelMapper;
import com.aiplus.backend.models.mapper.ModelStatsMapper;
import com.aiplus.backend.models.mapper.PerformanceMetricsMapper;
import com.aiplus.backend.models.model.AiModel;
import com.aiplus.backend.models.model.ModelStats;
import com.aiplus.backend.models.model.Visibility;
import com.aiplus.backend.models.repository.AiModelRepository;
import com.aiplus.backend.models.security.AiModelSecurityService;
import com.aiplus.backend.models.services.AiModelService;
import com.aiplus.backend.models.services.helper.AiModelAccessControlService;
import com.aiplus.backend.models.services.helper.AiModelDeveloperService;
import com.aiplus.backend.models.services.helper.AiModelEndpointService;
import com.aiplus.backend.models.services.helper.AiModelSubscriptionPlanService;
import com.aiplus.backend.models.services.helper.AiModelTaskService;
import com.aiplus.backend.users.model.DeveloperAccount;
import com.aiplus.backend.users.model.User;
import com.aiplus.backend.users.service.AccountService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AiModelServiceImpl implements AiModelService {
    private static final Logger logger = Logger.getLogger(AiModelServiceImpl.class.getName());

    private final AiModelRepository modelRepo;
    private final AiModelMapper modelMapper;
    private final AiModelAccessControlService aiModelAccessControlService;
    private final AiModelEndpointService aiModelEndpointService;
    private final AiModelSubscriptionPlanService aiModelSubscriptionPlanService;
    private final AiModelTaskService aiModelTaskService;
    private final AiModelSecurityService aiModelSecurityService;

    private final DockerImageVerifier dockerImageVerifier;

    private final ModelStatsMapper modelStatsMapper;
    private final PerformanceMetricsMapper performanceMetricsMapper;

    private final AiModelDeveloperService aiModelDeveloperService;

    private final AccountService accountService;

    /**
     * Fetches an AI model by its ID.
     *
     * @param id_model the ID of the model to fetch
     * @return the fetched AI model
     */
    @Override
    public AiModelDto getModelById(User developer, Long id_model) {
        AiModel model = modelRepo.findById(id_model).orElseThrow(() -> new AiModelNotFoundException(id_model));
        try {
            boolean isPublic = model.getVisibility() == Visibility.PUBLIC;
            /**
             * Check if the developer is the owner of the model If the model is not public,
             * verify ownership
             * 
             */
            if (!isPublic && !aiModelSecurityService.isOwner(model.getId(), developer.getEmail())) {
                throw new ModelAccessDeniedException();
            }
        } catch (java.lang.NullPointerException e) {
        }

        return modelMapper.toDto(model);
    }

    @Cacheable("models_all")
    @Override
    public Page<AiModelSummaryDto> getAllModels(Pageable pageable) {
        return modelRepo.findByVisibility(Visibility.PUBLIC, pageable).map(modelMapper::toSummaryDto);
    }

    /**
     * Creates a new AI model.
     */
    @Override
    public AiModelDto createModel(User user, AiModelCreateDto dto) {

        if (modelRepo.existsByName(dto.getName())) {
            throw new AiModelNameAlreadyExistsException(dto.getName());
        }
        DeveloperAccount developerAccount = (DeveloperAccount) accountService.findByUser(user);
        String dockerPat = developerAccount.getDockerPat();
        String dockerUsername = developerAccount.getDockerUsername();

        // checks if the docker image exists
        if (!dockerImageVerifier.existsImage(dockerUsername, dockerPat, dto.getImage())) {
            throw new DockerImageNotFoundException(dto.getImage());
        }
        logger.log(Level.INFO, "Docker image {0} exists for user {1}", new Object[] { dto.getImage(), dockerUsername });

        logger.log(Level.INFO, "Creating AI model: {0}", dto.getName());
        AiModel model = modelMapper.toEntity(dto);

        logger.log(Level.INFO, "Attaching Developer");
        model.setDeveloperAccount(aiModelAccessControlService.fetchDeveloperAccount(user.getAccount().getId()));

        logger.log(Level.INFO, "Attaching Tasks");
        if (dto.getTasks() != null) {
            model.setTasks(aiModelTaskService.resolveTasks(dto.getTasks()));
        }

        // Attach endpoints
        logger.log(Level.INFO, "Attaching Endpoints : {0}", dto.getEndpoints());
        if (dto.getEndpoints() != null) {
            aiModelEndpointService.attachEndpoints(dto.getEndpoints(), model);
        }

        // Attach subscription plans
        logger.log(Level.INFO, "Attaching Subscription Plans : " + dto.getSubscriptionPlans());
        if (dto.getSubscriptionPlans() != null) {

            aiModelSubscriptionPlanService.attachSubscriptionPlans(dto.getSubscriptionPlans(), model);

        }

        ModelStats stats = new ModelStats();
        stats.setDiscussions(0);
        stats.setStars(0);
        stats.setUsed(0);
        model.setStats(stats);

        logger.log(Level.INFO, "Saving AI Model ...");

        AiModel saved = modelRepo.save(model);
        logger.log(Level.INFO, "Model " + saved.getName() + " Saved Successfully !");

        return modelMapper.toDto(saved);
    }

    @Override
    public List<AiModelSummaryDto> getModelsByDeveloperId(Long developerId) {
        logger.log(Level.INFO, "Fetching AI models for developer ID: {0}", developerId);

        return modelMapper.toSummaryDtoList(modelRepo.findByDeveloperAccountId(developerId));
    }

    @Transactional(rollbackFor = Exception.class)
    @CachePut(value = "models_all")
    @Override
    public AiModelDto updateModel(Long id, AiModelDto dto) {
        logger.log(Level.INFO, "Updating AI model with ID: {0}", id);

        AiModel existingModel = modelRepo.findById(id).orElseThrow(() -> new AiModelNotFoundException(id));

        existingModel.setName(dto.getName());
        existingModel.setDescription(dto.getDescription());
        existingModel.setFramework(dto.getFramework());
        existingModel.setArchitecture(dto.getArchitecture());
        existingModel.setTrainingDataset(dto.getTrainingDataset());
        existingModel.setStats(modelStatsMapper.toEntity(dto.getStats()));
        existingModel.setPerformance(performanceMetricsMapper.toEntity(dto.getPerformance()));
        existingModel.setVisibility(dto.getVisibility());

        // Update tasks
        logger.log(Level.INFO, "Attaching Tasks");
        if (dto.getTasks() != null && !dto.getTasks().isEmpty()) {
            existingModel.getTasks().clear();
            existingModel.getTasks().addAll(aiModelTaskService.resolveTasks(dto.getTasks()));
        }

        // Attach endpoints
        logger.log(Level.INFO, "Attaching Endpoints");
        if (dto.getEndpoints() != null && !dto.getEndpoints().isEmpty()) {
            existingModel.getEndpoints().clear();
            existingModel.getEndpoints()
                    .addAll(aiModelEndpointService.attachEndpoints(dto.getEndpoints(), existingModel));
        }

        // Attach subscription plans

        logger.log(Level.INFO, "Attaching Subscription Plans");
        if (dto.getSubscriptionPlans() != null && !dto.getSubscriptionPlans().isEmpty()) {
            existingModel.getSubscriptionPlans().clear();
            existingModel.getSubscriptionPlans().addAll(
                    aiModelSubscriptionPlanService.attachSubscriptionPlans(dto.getSubscriptionPlans(), existingModel));
        }

        AiModelDto updatedModel = modelMapper.toDto(modelRepo.save(existingModel));
        logger.log(Level.INFO, "AI model with ID {0} updated successfully", id);

        return updatedModel;

    }

    @CacheEvict(value = "models_all")
    @Override
    @Transactional
    public void deleteModel(Long id) {
        logger.log(Level.INFO, "Deleting AI model with ID: {0}", id);

        AiModel model = modelRepo.findById(id).orElseThrow(() -> new AiModelNotFoundException(id));

        // Just unlink ManyToMany (Hibernate takes care of join table cleanup)
        if (model.getTasks() != null) {
            model.getTasks().clear();
        }
        modelRepo.delete(model);

        logger.log(Level.INFO, "AI model with ID {0} deleted successfully", id);
    }

    @Override
    public List<AiModelSummaryDto> getModelsByName(String name) {
        logger.log(Level.INFO, "Searching AI models by name: {0}", name);
        if (name == null || name.isEmpty()) {
            logger.log(Level.WARNING, "No name provided for model search");
            return Collections.emptyList();
        }
        return modelMapper.toSummaryDtoList(modelRepo.findByNameContainingIgnoreCase(name));
    }

    @Override
    public List<AiModelDto> getDeveloperModels(User user) {

        return aiModelDeveloperService.getDevelopersModels(user);
    }

}