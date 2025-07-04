package com.aiplus.backend.users.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.aiplus.backend.users.dto.UserDto;
import com.aiplus.backend.users.model.Role;
import com.aiplus.backend.users.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "role", target = "role")
    UserDto toDto(User user);

    @Mapping(source = "role", target = "role")
    User toEntity(UserDto dto);

    // enum ↔ String helpers
    default String roleToString(Role role) {
        return role == null ? null : role.name();
    }

    default Role stringToRole(String role) {
        return role == null ? null : Role.valueOf(role);
    }
}