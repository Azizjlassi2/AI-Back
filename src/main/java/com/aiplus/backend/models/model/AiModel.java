package com.aiplus.backend.models.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.aiplus.backend.comments.model.ModelComment;
import com.aiplus.backend.endpoints.model.Endpoint;
import com.aiplus.backend.subscriptions.model.SubscriptionPlan;
import com.aiplus.backend.users.model.ClientAccount;
import com.aiplus.backend.users.model.DeveloperAccount;
import com.aiplus.backend.utils.EncryptionUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@EqualsAndHashCode(exclude = { "endpoints", "subscriptionPlans", "comments", "tasks" })
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AiModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(columnDefinition = "TEXT", length = 2048)
    private String description;

    @Transient
    private String image;

    @Column(name = "image")
    private String docker_image;

    @Enumerated(EnumType.STRING)
    private Visibility visibility;

    private String framework;
    private String architecture;
    private String trainingDataset;

    @Embedded
    private ModelStats stats;

    @Embedded
    private PerformanceMetrics performance;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.EAGER)
    @JoinTable(name = "model_tasks", joinColumns = @JoinColumn(name = "model_id"), inverseJoinColumns = @JoinColumn(name = "task_id"))
    @JsonManagedReference
    private List<Task> tasks = new ArrayList<>();

    /**
     * ❌ Removed CascadeType.ALL ✅ A model belongs to a developer, but deleting a
     * model should NOT delete the developer
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "developer_account_id")
    private DeveloperAccount developerAccount;

    /**
     * ✅ Favorite models should be deleted with the client
     */
    @ManyToMany(mappedBy = "favoriteModels", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ClientAccount> clients = new ArrayList<>();

    /**
     * ✅ Endpoints should be deleted with the model
     */
    @OneToMany(mappedBy = "model", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference

    private List<Endpoint> endpoints = new ArrayList<>();

    /**
     * ✅ Subscription plans should be deleted with the model
     */
    @OneToMany(mappedBy = "model", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<SubscriptionPlan> subscriptionPlans = new ArrayList<>();

    /**
     * ✅ Comments should also be deleted with the model (optional but recommended)
     */
    @OneToMany(mappedBy = "model", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ModelComment> comments = new ArrayList<>();

    @CreatedDate
    private LocalDate createdAt;

    @LastModifiedDate
    private LocalDate updatedAt;

    @PrePersist
    @PreUpdate
    @SuppressWarnings("unused")
    private void encryptDockerImage() {
        if (docker_image != null) {
            this.docker_image = "ENC:" + EncryptionUtil.encrypt(image);
        }
    }

    @PostLoad
    @SuppressWarnings("unused")
    private void decryptDockerImage() {
        if (docker_image != null && docker_image.startsWith("ENC:")) {
            this.image = EncryptionUtil.decrypt(docker_image.substring(4));
        } else {
            this.image = docker_image;
        }
    }
}
