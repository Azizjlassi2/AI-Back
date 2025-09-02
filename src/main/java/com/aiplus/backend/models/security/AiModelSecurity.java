package com.aiplus.backend.models.security;

import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Component;

/**
 * Security component for verifying access permissions related to AI models.
 * <p>
 * This class provides methods used in Spring Security SpEL expressions to
 * determine whether the currently authenticated user is authorized to perform
 * certain actions (e.g., update or delete) on a specific AI model.
 * </p>
 *
 * <p>
 * Example usage in a controller:
 * 
 * <pre>
 * &#64;PreAuthorize("hasRole('DEVELOPER') and @aiModelSecurity.isOwner(#id, authentication.name)")
 * public ResponseEntity&lt;...&gt; update(@PathVariable Long id, ...) {
 *     ...
 * }
 * </pre>
 * </p>
 *
 */
@Component("aiModelSecurity")
public class AiModelSecurity {

    private final AiModelSecurityService modelSecurityService;

    public AiModelSecurity(AiModelSecurityService modelSecurityService) {
        this.modelSecurityService = modelSecurityService;
    }

    /**
     * Checks whether the given developer (by email) is the owner of the AI model
     * with the specified ID.
     *
     * @param modelId        the ID of the AI model
     * @param developerEmail the email of the currently authenticated developer
     * @return {@code true} if the developer owns the model,
     *         {@code AuthorizationDeniedException()} otherwise
     */
    public boolean isOwner(Long modelId, String developerEmail) {
        if (modelSecurityService.isOwner(modelId, developerEmail)) {
            return true;
        } else {
            throw new AuthorizationDeniedException("Access Denied : You Are Not The Owner !");
        }

    }
}