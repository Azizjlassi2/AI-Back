package com.aiplus.backend.users.model;

import java.util.ArrayList;
import java.util.List;

import com.aiplus.backend.models.model.AiModel;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Represents a client account with specific fields like website, bio, address,
 * company, job title, and phone number. Manages relationships with favorite AI
 * models.
 * 
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class ClientAccount extends Account {

    private String web_site;
    private String bio;
    private String address;
    private String company;
    private String job_title;

    @Pattern(regexp = "^[0-9]{8}$", message = "Phone number must be 8  digits")
    private String phone_number;

    /**
     * Konnect wallet ID for receiving payments.
     */
    private String konnectWalletId;

    /**
     * All favorite models for this Client. owner side is AiModel.clients
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "client_favorite_models", joinColumns = @JoinColumn(name = "client_id"), inverseJoinColumns = @JoinColumn(name = "model_id"))
    private List<AiModel> favoriteModels = new ArrayList<>();

}
