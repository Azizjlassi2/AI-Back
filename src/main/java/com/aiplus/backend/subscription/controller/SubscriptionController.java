package com.aiplus.backend.subscription.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aiplus.backend.payment.dto.KonnectPaymentInitResponse;
import com.aiplus.backend.subscription.dto.SubscriptionCreateDTO;
import com.aiplus.backend.subscription.dto.SubscriptionDTO;
import com.aiplus.backend.subscription.dto.SubscriptionDetailsDTO;
import com.aiplus.backend.subscription.model.Subscription;
import com.aiplus.backend.subscription.service.SubscriptionService;
import com.aiplus.backend.users.model.User;
import com.aiplus.backend.utils.responses.ApiResponse;
import com.aiplus.backend.utils.responses.ResponseUtil;

import lombok.RequiredArgsConstructor;
import java.util.List;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/subscribe")
    public ResponseEntity<ApiResponse<KonnectPaymentInitResponse>> createSubscription(
            @RequestBody SubscriptionCreateDTO dto) {
        KonnectPaymentInitResponse response = subscriptionService.createSubscription(dto);
        return ResponseEntity.ok(ResponseUtil.success("Subscription created", response));
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<Subscription>> activateSubscription(@PathVariable Long id) {
        Subscription subscription = subscriptionService.activateSubscription(id);
        return ResponseEntity.ok(ResponseUtil.success("Subscription activated", subscription));
    }

    @GetMapping("/{payment_ref}")
    public ResponseEntity<ApiResponse<SubscriptionDetailsDTO>> getSubscriptionByPaymentRef(
            @PathVariable String payment_ref) {
        return ResponseEntity.ok(ResponseUtil.success("Subscription retrieved",
                subscriptionService.getSubscriptionByPaymentRef(payment_ref)));
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<SubscriptionDTO>>> getSubscriptionsByClient(
            @AuthenticationPrincipal User user) {
        return ResponseEntity
                .ok(ResponseUtil.success("Subscription retrieved", subscriptionService.getSubscriptionsByClient(user)));
    }

}
