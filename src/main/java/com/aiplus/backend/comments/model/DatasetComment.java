package com.aiplus.backend.comments.model;

import com.aiplus.backend.datasets.model.Dataset; // Assuming Dataset entity package
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
public class DatasetComment extends Comment {

    @ManyToOne
    @JoinColumn(name = "dataset_id")
    @JsonBackReference
    private Dataset dataset;

    public DatasetComment(User user, Dataset dataset, String content) {
        super(user, content);
        this.dataset = dataset;
    }
}