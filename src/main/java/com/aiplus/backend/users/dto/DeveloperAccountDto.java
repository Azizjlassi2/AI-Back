package com.aiplus.backend.users.dto;

import java.util.List;

import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.ReadOnlyProperty;

import com.aiplus.backend.models.dto.AiModelSummaryDto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class DeveloperAccountDto extends AccountDto {

    private String web_site;
    private String bio;
    @Length(min = 8, max = 8)
    private Integer phone_number;
    private String address;
    private String linkedin;
    private String github;
    private String docker_username;
    private String docker_pat;

    @ReadOnlyProperty
    private List<AiModelSummaryDto> models;

}
