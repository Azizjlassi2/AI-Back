package com.aiplus.backend.users.dto;

/**
 * Base class for account update requests. Enables polymorphic deserialization.
 */

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Base class for account update requests. Enables polymorphic deserialization.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @JsonSubTypes.Type(value = DeveloperAccountUpdateRequest.class, name = "developer"),
        @JsonSubTypes.Type(value = ClientAccountUpdateRequest.class, name = "client"),
        @JsonSubTypes.Type(value = AdminAccountUpdateRequest.class, name = "admin") })
public abstract class AccountUpdateRequest {

}