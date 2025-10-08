package com.aiplus.backend.users.dto;

import java.util.List;

import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.ReadOnlyProperty;

import com.aiplus.backend.models.dto.AiModelSummaryDto;

import lombok.Data;

@Data
public class ClientAccountDto extends AccountDto {

    private String web_site;
    private String bio;
    private String address;
    private String company;
    private String job_title;

    @Length(min = 8, max = 8)
    private Integer phone_number;

    /**
     * List of favorite AI models for the client.
     */
    @ReadOnlyProperty
    private List<AiModelSummaryDto> favoriteModels;

}
