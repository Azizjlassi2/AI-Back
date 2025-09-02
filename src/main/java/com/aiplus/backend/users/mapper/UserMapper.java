package com.aiplus.backend.users.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import com.aiplus.backend.users.dto.AccountDto;
import com.aiplus.backend.users.dto.UserCreationDto;
import com.aiplus.backend.users.dto.UserDetailDto;
import com.aiplus.backend.users.dto.UsersDto;
import com.aiplus.backend.users.model.Account;
import com.aiplus.backend.users.model.Role;
import com.aiplus.backend.users.model.User;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    @Autowired
    protected AccountMapper accountMapper;

    @Mapping(source = "email", target = "email")
    @Mapping(source = "name", target = "username")
    @Mapping(source = "role", target = "role", qualifiedByName = "roleToString")
    public abstract UsersDto toDto(User user);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "name", target = "username")
    @Mapping(source = "role", target = "role", qualifiedByName = "roleToString")
    @Mapping(source = "account", target = "account", qualifiedByName = "accountToDto")
    public abstract UserDetailDto toDetailDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(source = "username", target = "name")
    @Mapping(source = "role", target = "role", qualifiedByName = "stringToRole")
    public abstract User toEntity(UserCreationDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(source = "username", target = "name")
    @Mapping(source = "role", target = "role", qualifiedByName = "stringToRole")
    public abstract User toEntity(UserDetailDto dto);

    @Named("roleToString")
    public String roleToString(Role role) {
        return role != null ? role.name() : null;
    }

    @Named("stringToRole")
    public Role stringToRole(String role) {
        return role != null ? Role.valueOf(role) : null;
    }

    @Named("accountToDto")
    public AccountDto accountToDto(Account account) {
        return account != null ? accountMapper.toAccountDto(account) : null;
    }

    @Mapping(target = "account", ignore = true)
    @Mapping(source = "username", target = "name")
    @Mapping(source = "role", target = "role", qualifiedByName = "stringToRole")
    public abstract User toEntity(UsersDto dto);
}