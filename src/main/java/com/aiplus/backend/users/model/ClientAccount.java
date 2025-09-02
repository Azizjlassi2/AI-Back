package com.aiplus.backend.users.model;

import java.util.ArrayList;
import java.util.List;

import com.aiplus.backend.models.model.AiModel;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class ClientAccount extends Account {

    /**
     * All favorite models for this Client. owner side is AiModel.clients
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "client_favorite_models", joinColumns = @JoinColumn(name = "client_id"), inverseJoinColumns = @JoinColumn(name = "model_id"))
    private List<AiModel> favoriteModels = new ArrayList<>();

}
