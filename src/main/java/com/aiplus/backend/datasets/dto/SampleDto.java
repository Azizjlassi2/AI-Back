package com.aiplus.backend.datasets.dto;

import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SampleDto {
    @NotBlank
    private String content;

    private String label;

    private Map<String, String> metadata;

    private String url;
    private String mimeType;

}
