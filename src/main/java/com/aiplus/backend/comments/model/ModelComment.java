package com.aiplus.backend.comments.model;

import com.aiplus.backend.models.model.AiModel;
import com.aiplus.backend.users.model.User;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ModelComment extends Comment {

    @ManyToOne
    @JoinColumn(name = "model_id")
    @JsonBackReference
    private AiModel model;

    public ModelComment(User user, AiModel model, String content) {
        super(user, content);
        this.model = model;
    }

}
