package com.aiplus.backend.users.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.aiplus.backend.models.mapper.AiModelMapper;
import com.aiplus.backend.users.dto.AccountDto;
import com.aiplus.backend.users.dto.AdminAccountDto;
import com.aiplus.backend.users.dto.ClientAccountDto;
import com.aiplus.backend.users.dto.DeveloperAccountDto;
import com.aiplus.backend.users.model.Account;
import com.aiplus.backend.users.model.AdminAccount;
import com.aiplus.backend.users.model.ClientAccount;
import com.aiplus.backend.users.model.DeveloperAccount;

@Mapper(componentModel = "spring", uses = { AiModelMapper.class })
public abstract class AccountMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(source = "models", target = "models")
    public abstract DeveloperAccountDto toDto(DeveloperAccount account);

    @Mapping(target = "id", source = "id")
    public abstract AdminAccountDto toDto(AdminAccount account);

    @Mapping(target = "id", source = "id")
    public abstract ClientAccountDto toDto(ClientAccount account);

    public AccountDto toAccountDto(Account account) {
        if (account instanceof DeveloperAccount developerAccount) {
            return toDto(developerAccount);
        } else if (account instanceof AdminAccount adminAccount) {
            return toDto(adminAccount);
        } else if (account instanceof ClientAccount clientAccount) {
            return toDto(clientAccount);
        } else {
            return null;
        }
    }
}