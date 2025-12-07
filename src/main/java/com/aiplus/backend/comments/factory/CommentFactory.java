package com.aiplus.backend.comments.factory;

import org.springframework.stereotype.Component;

import com.aiplus.backend.comments.model.DatasetComment;
import com.aiplus.backend.comments.model.ModelComment;
import com.aiplus.backend.datasets.model.Dataset;
import com.aiplus.backend.models.model.AiModel;
import com.aiplus.backend.users.model.User;

/**
 * Factory class for creating comment entities.
 * 
 */
@Component
public class CommentFactory {

    /**
     * Creates a new ModelComment instance.
     */
    public ModelComment createModelComment(User user, AiModel model, String content) {
        return new ModelComment(user, model, content);
    }

    /**
     * Creates a new DatasetComment instance.
     */
    public DatasetComment createDatasetComment(User user, Dataset dataset, String content) {
        return new DatasetComment(user, dataset, content);
    }

}
