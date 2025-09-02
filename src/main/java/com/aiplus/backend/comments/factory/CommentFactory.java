package com.aiplus.backend.comments.factory;

import org.springframework.stereotype.Component;

import com.aiplus.backend.comments.model.ModelComment;
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

}
