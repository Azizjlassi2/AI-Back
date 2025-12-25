package com.aiplus.backend.subscription.model;

/**
 * Enumeration of possible subscription statuses.
 * 
 * PENDING: Subscription is pending activation. INACTIVE: Subscription is
 * inactive. ACTIVE: Subscription is active. CANCELLED: Subscription has been
 * cancelled. EXPIRED: Subscription has expired.
 */
public enum SubscriptionStatus {
    PENDING, INACTIVE, ACTIVE, CANCELLED, EXPIRED
}
