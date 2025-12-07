package com.aiplus.backend.models.services.impl;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aiplus.backend.docker.exceptions.DockerImageNotFoundException;
import com.aiplus.backend.docker.service.DockerImageVerifier;
import com.aiplus.backend.endpoints.dto.EndpointDto;
import com.aiplus.backend.endpoints.model.Endpoint;
import com.aiplus.backend.models.dto.AiModelCreateDto;
import com.aiplus.backend.models.dto.AiModelDto;
import com.aiplus.backend.models.dto.AiModelSummaryDto;
import com.aiplus.backend.models.dto.TaskDto;
import com.aiplus.backend.models.exceptions.AiModelNameAlreadyExistsException;
import com.aiplus.backend.models.exceptions.AiModelNotFoundException;
import com.aiplus.backend.models.exceptions.ModelAccessDeniedException;
import com.aiplus.backend.models.mapper.AiModelMapper;
import com.aiplus.backend.models.mapper.ModelStatsMapper;
import com.aiplus.backend.models.mapper.PerformanceMetricsMapper;
import com.aiplus.backend.models.model.AiModel;
import com.aiplus.backend.models.model.ModelStats;
import com.aiplus.backend.models.model.Task;
import com.aiplus.backend.models.model.Visibility;
import com.aiplus.backend.models.repository.AiModelRepository;
import com.aiplus.backend.models.security.AiModelSecurityService;
import com.aiplus.backend.models.services.AiModelService;
import com.aiplus.backend.models.services.helper.AiModelAccessControlService;
import com.aiplus.backend.models.services.helper.AiModelDeveloperService;
import com.aiplus.backend.models.services.helper.AiModelEndpointService;
import com.aiplus.backend.models.services.helper.AiModelSubscriptionPlanService;
import com.aiplus.backend.models.services.helper.AiModelTaskService;
import com.aiplus.backend.subscriptionPlans.dto.SubscriptionPlanDto;
import com.aiplus.backend.subscriptionPlans.model.SubscriptionPlan;
import com.aiplus.backend.users.model.DeveloperAccount;
import com.aiplus.backend.users.model.User;
import com.aiplus.backend.users.service.AccountService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of the AiModelService interface for managing AI models.
 */
@Slf4j
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
     * Fetches all AI models with pagination.
     *
     * @param pageable pagination information
     * @return a page of AI model summaries
     */
    @Cacheable("models_all")
    @Override
    public Page<AiModelSummaryDto> getAllModels(Pageable pageable) {
        System.out.println(modelRepo.findByVisibility(Visibility.PUBLIC, pageable));
        return modelRepo.findByVisibility(Visibility.PUBLIC, pageable).map(modelMapper::toSummaryDto);
    }

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

        AiModel model = modelMapper.toEntity(dto);

        model.setDeveloperAccount(aiModelAccessControlService.fetchDeveloperAccount(user.getAccount().getId()));

        if (dto.getTasks() != null) {
            model.setTasks(aiModelTaskService.resolveTasks(dto.getTasks()));
        }

        // Attach endpoints
        if (dto.getEndpoints() != null) {
            aiModelEndpointService.attachEndpoints(dto.getEndpoints(), model);
        }

        // Attach subscription plans
        if (dto.getSubscriptionPlans() != null) {

            aiModelSubscriptionPlanService.attachSubscriptionPlans(dto.getSubscriptionPlans(), model);

        }

        ModelStats stats = new ModelStats();
        stats.setDiscussions(0);
        stats.setStars(0);
        stats.setUsed(0);
        model.setStats(stats);

        AiModel saved = modelRepo.save(model);
        logger.log(Level.INFO, "Model " + saved.getName() + " Saved Successfully !");

        return modelMapper.toDto(saved);
    }

    @Override
    public List<AiModelSummaryDto> getModelsByDeveloperId(Long developerId) {
        logger.log(Level.INFO, "Fetching AI models for developer ID: {0}", developerId);

        return modelMapper.toSummaryDtoList(modelRepo.findByDeveloperAccountId(developerId));
    }

    /**
     * Updates an existing AI model.
     */
    @Transactional
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
        logger.info("Attaching Tasks");
        if (dto.getTasks() != null && !dto.getTasks().isEmpty()) {
            // Synchronize: Remove tasks not in DTO, add/update those in DTO
            Set<Long> newTaskIds = dto.getTasks().stream().filter(d -> d.getId() != null).map(TaskDto::getId)
                    .collect(Collectors.toSet());

            // Detach unreferenced tasks (no deletion)
            existingModel.getTasks().removeIf(task -> !newTaskIds.contains(task.getId()));

            // Resolve (update/create) and add new ones
            List<Task> updatedTasks = aiModelTaskService.resolveTasks(dto.getTasks());
            updatedTasks.stream().filter(task -> task.getId() == null || !newTaskIds.contains(task.getId())) // New or
                                                                                                             // mismatched
                    .forEach(existingModel.getTasks()::add);
        }

        // Attach endpoints
        logger.info("Attaching Endpoints");
        if (dto.getEndpoints() != null && !dto.getEndpoints().isEmpty()) {
            // Synchronize: Remove endpoints not in DTO, add/update those in DTO
            Set<Long> newEndpointIds = dto.getEndpoints().stream().filter(d -> d.getId() != null)
                    .map(EndpointDto::getId).collect(Collectors.toSet());

            // Detach unreferenced endpoints (no deletion)
            existingModel.getEndpoints().removeIf(endpoint -> !newEndpointIds.contains(endpoint.getId()));

            // Attach (update/create) and add new ones
            List<Endpoint> updatedEndpoints = aiModelEndpointService.attachEndpoints(dto.getEndpoints(), existingModel);
            updatedEndpoints.stream()
                    .filter(endpoint -> endpoint.getId() == null || !newEndpointIds.contains(endpoint.getId())) // New
                                                                                                                // or
                                                                                                                // mismatched
                    .forEach(existingModel.getEndpoints()::add);
        }
        // Attach subscription plans
        logger.log(Level.INFO, "Attaching Subscription Plans");

        if (dto.getSubscriptionPlans() != null && !dto.getSubscriptionPlans().isEmpty()) {
            // Synchronize: Remove plans not in DTO, add/update those in DTO
            Set<Long> newPlanIds = dto.getSubscriptionPlans().stream().filter(d -> d.getId() != null)
                    .map(SubscriptionPlanDto::getId).collect(Collectors.toSet());

            // Remove detached plans (but do NOT delete if referenced; just detach from
            // model)
            existingModel.getSubscriptionPlans().removeIf(plan -> !newPlanIds.contains(plan.getId()));

            // Add/update the new/updated ones
            List<SubscriptionPlan> updatedPlans = aiModelSubscriptionPlanService
                    .attachSubscriptionPlans(dto.getSubscriptionPlans(), existingModel);
            // Add only new ones (avoid duplicates for updates)
            updatedPlans.stream().filter(plan -> plan.getId() == null || !newPlanIds.contains(plan.getId())) // New or
                                                                                                             // mismatched
                    .forEach(e -> existingModel.getSubscriptionPlans().add(e));
        }

        AiModelDto updatedModel = modelMapper.toDto(modelRepo.save(existingModel));
        logger.log(Level.INFO, "AI model with ID {0} updated successfully", id);

        return updatedModel;

    }

    /**
     * Deletes an AI model by its ID.
     */
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

    /**
     * Searches for AI models by name.
     */
    @Override
    public List<AiModelSummaryDto> getModelsByName(String name) {
        logger.log(Level.INFO, "Searching AI models by name: {0}", name);
        if (name == null || name.isEmpty()) {
            logger.log(Level.WARNING, "No name provided for model search");
            return Collections.emptyList();
        }
        return modelMapper.toSummaryDtoList(modelRepo.findByNameContainingIgnoreCase(name));
    }

    /**
     * Fetches AI models developed by a specific user.
     */
    @Override
    public List<AiModelDto> getDeveloperModels(User user) {

        return aiModelDeveloperService.getDevelopersModels(user);
    }

}