package com.aiplus.backend.users.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.aiplus.backend.users.dto.UserSummaryDto;
import com.aiplus.backend.users.model.Role;
import com.aiplus.backend.users.model.User;

/**
 * Created to solve circular‑bean problem
 * before :
 * AiModelMapper → (uses) UserMapper
 * UserMapper → (uses) AccountMapper
 * AccountMapper → (uses) AiModelMapper
 * 
 * now:
 * AiModelMapper → UserSummaryMapper
 * UserMapper → AccountMapper → AiModelMapper
 * 
 * 
 */
@Mapper(componentModel = "spring")
public abstract class UserSummaryMapper {

    @Mapping(source = "email", target = "email")
    @Mapping(source = "name", target = "username")
    @Mapping(source = "role", target = "role", qualifiedByName = "roleToString")
    public abstract UserSummaryDto toDto(User user);

    @Named("roleToString")
    public String roleToString(Role role) {
        return role != null ? role.name() : null;
    }

    @Named("stringToRole")
    public Role stringToRole(String role) {
        return role != null ? Role.valueOf(role) : null;
    }

}
