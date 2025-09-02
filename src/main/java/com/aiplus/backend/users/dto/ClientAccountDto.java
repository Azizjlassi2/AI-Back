package com.aiplus.backend.users.dto;

import java.util.List;

import org.springframework.data.annotation.ReadOnlyProperty;

import com.aiplus.backend.models.dto.AiModelSummaryDto;

import lombok.Data;

@Data
public class ClientAccountDto extends AccountDto {

    /**
     * List of favorite AI models for the client.
     */
    @ReadOnlyProperty
    private List<AiModelSummaryDto> favoriteModels;

}
