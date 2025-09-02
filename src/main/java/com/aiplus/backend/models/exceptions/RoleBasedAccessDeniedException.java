package com.aiplus.backend.models.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class RoleBasedAccessDeniedException extends RuntimeException {

    public RoleBasedAccessDeniedException(String role) {
        super("User does not have the necessary role . Current role : " + role);
    }

}
