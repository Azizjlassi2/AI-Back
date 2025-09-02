package com.aiplus.backend.users.model;

import java.util.ArrayList;
import java.util.List;

import com.aiplus.backend.models.model.AiModel;
import com.aiplus.backend.utils.EncryptionUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class DeveloperAccount extends Account {

    private String web_site;
    private String bio;
    private String address;
    private String linkedin;
    private String github;

    @Pattern(regexp = "^[0-9]{8,15}$", message = "Phone number must be between 8 and 15 digits")
    private String phone_number;

    @Column(name = "docker_username")
    private String docker_username;
    @Lob
    @Column(name = "docker_pat", length = 512)
    @JsonIgnore // hide encrypted value from any JSON serialization
    private String encryptedDockerPat;

    @Transient
    private String dockerPat; // runtime only

    // write-only: set plain PAT -> store encrypted immediately
    public void setDockerPat(String plainPat) {
        if (plainPat == null || plainPat.isEmpty()) {
            this.dockerPat = null;
            this.encryptedDockerPat = null;
            return;
        }
        this.dockerPat = plainPat;
        this.encryptedDockerPat = "ENC:" + EncryptionUtil.encrypt(plainPat);
    }

    // lazy decrypt: always returns plain PAT if encrypted value exists
    public String getDockerPat() {
        if (this.dockerPat == null && this.encryptedDockerPat != null) {
            if (this.encryptedDockerPat.startsWith("ENC:")) {
                this.dockerPat = EncryptionUtil.decrypt(this.encryptedDockerPat.substring(4));
            } else {
                this.dockerPat = this.encryptedDockerPat; // fallback
            }
        }
        return this.dockerPat;
    }

    /**
     * All AI models created by this developer. owner side is
     * AiModel.developerAccount
     */
    @OneToMany(mappedBy = "developerAccount", fetch = FetchType.EAGER)
    private List<AiModel> models = new ArrayList<>();

    public void addModel(AiModel model) {
        model.setDeveloperAccount(this);
        models.add(model);
    }

    public void removeModel(AiModel model) {
        model.setDeveloperAccount(null);
        models.remove(model);
    }

    public String setDockerUsername(String dockerUsername) {
        return this.docker_username = dockerUsername;
    }

    public String getDockerUsername() {
        return docker_username;
    }

}
