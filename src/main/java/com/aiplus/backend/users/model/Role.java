package com.aiplus.backend.users.model;

public enum Role {
    CLIENT,
    ADMIN,
    DEVELOPER;

    public String getAuthority() {
        return "ROLE_" + this.name();
    }

}
