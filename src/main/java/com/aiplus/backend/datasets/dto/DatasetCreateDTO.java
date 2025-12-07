package com.aiplus.backend.datasets.dto;

import java.util.List;

import com.aiplus.backend.datasets.model.DatasetFormat;
import com.aiplus.backend.datasets.model.LicenseType;
import com.aiplus.backend.datasets.model.Visibility;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

/**
 * DTO for creating a new dataset
 */
@Data
@Builder
public class DatasetCreateDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @Builder.Default
    @NotNull
    private Visibility visibility = Visibility.PUBLIC;

    @Builder.Default
    @NotNull
    private DatasetFormat format = DatasetFormat.CSV;

    @Builder.Default
    private Integer sampleCount = 0;

    @Builder.Default
    @NotNull
    private LicenseType license = LicenseType.CC_BY;

    private String customLicenseUrl;

    @Builder.Default
    private List<@Valid TagDto> tags = List.of();
    @Builder.Default
    private List<@Valid SampleDto> samples = List.of();

    private PurchasePlanDto purchasePlan;

}
