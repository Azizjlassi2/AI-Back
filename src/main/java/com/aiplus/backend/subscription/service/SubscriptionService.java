package com.aiplus.backend.subscription.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.aiplus.backend.payment.model.PaymentStatus;
import com.aiplus.backend.payment.repository.PaymentRepository;
import com.aiplus.backend.payment.service.PaymentService;
import com.aiplus.backend.subscription.dto.SubscriptionCreateDTO;
import com.aiplus.backend.subscription.model.Subscription;
import com.aiplus.backend.subscription.model.SubscriptionStatus;
import com.aiplus.backend.subscription.repository.SubscriptionRepository;
import com.aiplus.backend.subscriptionPlans.model.SubscriptionPlan;
import com.aiplus.backend.subscriptionPlans.repository.SubscriptionPlanRepository;
import com.aiplus.backend.users.model.User;
import com.aiplus.backend.users.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service class for managing subscriptions. This class will handle business
 * logic related to subscriptions, such as creating, updating, and retrieving
 * subscription details.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;

    /**
     * Creates a new subscription for a client to a specific subscription plan.
     * <p>
     * This method performs the following steps:
     * <ul>
     * <li>Validates that the provided clientId corresponds to a ClientAccount.</li>
     * <li>Retrieves the subscription plan and associated AI model and
     * developer.</li>
     * <li>Initializes a new Subscription in PENDING status and saves it.</li>
     * <li>Initiates a payment for the subscription (stubbed for extension).</li>
     * <li>Links the payment to the subscription and updates the subscription
     * record.</li>
     * </ul>
     *
     * @param dto the DTO containing clientId, planId, recurring flag, and webhook
     *            URL
     * @return the created Subscription entity
     * @throws IllegalArgumentException if clientId is invalid or not a
     *                                  ClientAccount
     */
    public String createSubscription(SubscriptionCreateDTO dto) {
        log.info("Creating subscription for clientId: {}, planId: {} ,modelName: {} , paymentMethod: {} , planName: {}",
                dto.getClientId(), dto.getPlanId(), dto.getModelName(), dto.getPaymentMethod(), dto.getPlanName());

        User client = userRepository.findById(dto.getClientId())
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));

        if (!client.isClient()) {
            throw new IllegalArgumentException("User must be a client");
        }

        // Validate plan
        SubscriptionPlan plan = subscriptionPlanRepository.findById(dto.getPlanId())
                .orElseThrow(() -> new IllegalArgumentException("Plan not found"));

        // Link developer from plan.model
        var model = plan.getModel();
        var developer = model.getDeveloperAccount();

        Subscription subscription = Subscription.builder().client(client).plan(plan).developer(developer)
                .startDate(LocalDate.now()) // will be updated on activation
                .status(SubscriptionStatus.PENDING).recurring(false).build();
        subscriptionRepository.save(subscription);

        log.info("Subscription Initialized !");

        // Initiate payment. PaymentService returns the payUrl (frontend redirect)
        String payUrl = paymentService.initiatePaymentForSubscription(subscription);

        log.info("Payment initiated, payUrl: {}", payUrl);
        // In a controller you will return { subscriptionId, payUrl } to the frontend.
        return payUrl;

    }

    public Subscription updateSubscription(Subscription subscription) {
        return subscriptionRepository.save(subscription);
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
        }
        return subscription;
    }

    public Optional<Subscription> getSubscriptionById(Long id) {
        return subscriptionRepository.findById(id);
    }

}
