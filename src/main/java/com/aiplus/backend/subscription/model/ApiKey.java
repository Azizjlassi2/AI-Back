package com.aiplus.backend.subscription.model;

import java.time.Instant;

import com.aiplus.backend.utils.EncryptionUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // association vers l'abonnement
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false, unique = true)
    private Subscription subscription;

    @Transient
    private String key;

    // stocker uniquement le hash (BCrypt)
    @JsonIgnore
    @Column(name = "key_hash", nullable = false)
    private String keyHash;

    @Builder.Default
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @PrePersist
    private void onCreate() {
        this.keyHash = EncryptionUtil.encrypt(this.key);
    }

    @PostLoad
    private void OnLoad() {
        this.key = EncryptionUtil.decrypt(this.keyHash);
    }

}
