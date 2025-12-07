package com.aiplus.backend.subscription.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.aiplus.backend.deployments.model.DeployedInstance;
import com.aiplus.backend.deployments.service.DeploymentService;
import com.aiplus.backend.payment.dto.PaymentInitResponse;
import com.aiplus.backend.payment.exception.PaymentInitializationException;
import com.aiplus.backend.payment.model.PaymentStatus;
import com.aiplus.backend.payment.service.KonnectPaymentService;
import com.aiplus.backend.payment.service.PaymeePaymentService;
import com.aiplus.backend.subscription.dto.SubscriptionCreateDTO;
import com.aiplus.backend.subscription.dto.SubscriptionDTO;
import com.aiplus.backend.subscription.dto.SubscriptionDetailsDTO;
import com.aiplus.backend.subscription.mapper.SubscriptionMapper;
import com.aiplus.backend.subscription.model.Subscription;
import com.aiplus.backend.subscription.model.SubscriptionStatus;
import com.aiplus.backend.subscription.repository.SubscriptionRepository;
import com.aiplus.backend.subscriptionPlans.exceptions.SubscriptionPlanNotFoundException;
import com.aiplus.backend.subscriptionPlans.model.SubscriptionPlan;
import com.aiplus.backend.subscriptionPlans.repository.SubscriptionPlanRepository;
import com.aiplus.backend.users.exceptions.AccountNotFoundException;
import com.aiplus.backend.users.exceptions.ClientAccountNotFoundException;
import com.aiplus.backend.users.model.Account;
import com.aiplus.backend.users.model.ClientAccount;
import com.aiplus.backend.users.model.User;
import com.aiplus.backend.users.repository.AccountRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service class for managing subscriptions. This class handles business logic
 * related to subscriptions, including creation, activation, and retrieval of
 * subscription details.
 * 
 * <p>
 * Key responsibilities:
 * <ul>
 * <li>Validate client accounts and subscription plans</li>
 * <li>Create and initialize subscription records with payment initiation</li>
 * <li>Activate subscriptions upon successful payment</li>
 * <li>Retrieve subscription details by payment reference</li>
 * </ul>
 * </p>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final ApiKeyService apiKeyService;
    private final AccountRepository accountRepository;
    private final KonnectPaymentService konnectPaymentService;
    private final PaymeePaymentService paymeePaymentService;
    private final SubscriptionMapper subscriptionMapper;

    private final DeploymentService deploymentService;

    public List<SubscriptionDTO> getSubscriptionsByClient(User user) {

        Account account = accountRepository.findById(user.getAccount().getId())
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + user.getAccount().getId()));

        if (!(account instanceof ClientAccount)) {
            throw new IllegalArgumentException("Account is not a ClientAccount");
        }

        ClientAccount clientAccount = (ClientAccount) account;

        List<Subscription> subscriptions = subscriptionRepository.findByClient(clientAccount);

        // Map entities to DTOs
        return subscriptions.stream().map(subscriptionMapper::toDto).toList();
    }

    /**
     * Creates a new subscription for a client to a specific subscription plan.
     * 
     * <p>
     * This method performs the following steps:
     * <ol>
     * <li>Validates that the provided clientId corresponds to a valid
     * ClientAccount</li>
     * <li>Retrieves and validates the subscription plan</li>
     * <li>Initializes a new Subscription in PENDING status</li>
     * <li>Persists the subscription entity</li>
     * <li>Initiates payment processing for the subscription</li>
     * <li>Associates the payment with the subscription</li>
     * </ol>
     * </p>
     *
     * @param dto the DTO containing clientId, planId, recurring flag, and customer
     *            contact information
     * @return the Konnect payment initialization response containing payment URL
     *         and reference
     * @throws ClientAccountNotFoundException    if the provided clientId does not
     *                                           correspond to a valid ClientAccount
     * @throws SubscriptionPlanNotFoundException if the provided planId does not
     *                                           exist in the system
     * @throws PaymentInitializationException    if payment gateway initialization
     *                                           fails
     * @throws IllegalArgumentException          if required DTO fields are null or
     *                                           invalid
     */
    @Transactional
    public PaymentInitResponse createSubscription(SubscriptionCreateDTO dto) {

        log.info("Received SubscriptionCreateDTO: {}", dto);

        validateSubscriptionCreateDTO(dto);

        log.info("Initiating subscription creation - Model: {}, Plan: {}, ClientId: {}", dto.getModelName(),
                dto.getPlanName(), dto.getClientId());

        try {
            ClientAccount client = findAndValidateClientAccount(dto.getClientId());
            SubscriptionPlan plan = subscriptionPlanRepository.findById(dto.getPlanId()).orElseThrow(
                    () -> new SubscriptionPlanNotFoundException("Subscription plan not found: " + dto.getPlanName()));

            Subscription subscription = Subscription.builder().client(client).plan(plan).startDate(LocalDate.now())
                    .status(SubscriptionStatus.PENDING).build();

            subscription = subscriptionRepository.save(subscription);

            apiKeyService.createForSubscription(subscription);

            return switch (dto.getPaymentMethod()) {
            case KONNECT -> konnectPaymentService.initiatePaymentForSubscription(subscription, dto);
            case PAYMEE -> paymeePaymentService.initiatePaymentForSubscription(subscription, dto);
            default -> throw new IllegalArgumentException("Unsupported payment gateway: " + dto.getPaymentMethod());
            };

        } catch (ClientAccountNotFoundException | SubscriptionPlanNotFoundException e) {
            // Log validation errors and rethrow
            log.error("Validation error during subscription creation: {}", e.getMessage());
            throw e;
        } catch (PaymentInitializationException e) {
            // Log payment errors
            log.error("Payment initialization failed for subscription creation: {}", e.getMessage());
            // Subscription is already persisted; payment failed. Consider retry strategy or
            // cleanup.
            throw e;
        } catch (IllegalArgumentException e) {
            // Unexpected errors
            log.error("Unexpected error during subscription creation", e);
            throw new RuntimeException("Subscription creation failed: " + e.getMessage(), e);
        }
    }

    /**
     * Activate subscription after payment confirmation
     */
    public Subscription activateSubscription(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId).orElse(null);
        if (subscription != null && subscription.getPayment() != null
                && subscription.getPayment().getStatus() == PaymentStatus.COMPLETED) {
            subscription.setStatus(SubscriptionStatus.ACTIVE);
            subscription = subscriptionRepository.save(subscription);

            // Déployez après activation
            DeployedInstance instance = deploymentService.deployModelForSubscription(subscription);

            // Associez les endpoints du modèle à l'instance (e.g., via baseUrl + path)
            // Client peut consommer via baseUrl + endpoint.path avec API key
        }
        return subscription;
    }

    /**
     * Get subscription details by payment reference ID
     */
    public SubscriptionDetailsDTO getSubscriptionByPaymentRef(String id) {
        Optional<Subscription> subscription = subscriptionRepository.findByPaymentGatewayTransactionId(id);
        if (subscription.isPresent()) {
            log.info("Subscription Found :" + subscription);
            return subscriptionMapper.toDetailsDto(subscription.get());
        }
        log.info("No Subscription Found !");
        return null;
    }

    /**
     * Validates the provided SubscriptionCreateDTO for required fields.
     *
     * @param dto the DTO to validate
     * @throws IllegalArgumentException if any required field is null
     */
    private void validateSubscriptionCreateDTO(SubscriptionCreateDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("SubscriptionCreateDTO cannot be null");
        }
        if (dto.getClientId() == null) {
            throw new IllegalArgumentException("Client ID cannot be null");
        }
        if (dto.getPlanId() == null) {
            throw new IllegalArgumentException("Plan ID cannot be null");
        }
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        log.debug("SubscriptionCreateDTO validation passed");
    }

    /**
     * Finds and validates a ClientAccount by ID, ensuring it is a valid
     * ClientAccount type.
     *
     * @param clientId the client account ID
     * @return the validated ClientAccount
     * @throws ClientAccountNotFoundException if the account does not exist or is
     *                                        not a ClientAccount
     */
    private ClientAccount findAndValidateClientAccount(Long clientId) {
        return accountRepository.findById(clientId).filter(ClientAccount.class::isInstance)
                .map(ClientAccount.class::cast).orElseThrow(() -> {
                    log.warn("Client account not found or is not ClientAccount type - ClientId: {}", clientId);
                    return new ClientAccountNotFoundException(
                            "Invalid client ID or account is not a ClientAccount: " + clientId);
                });
    }

}
