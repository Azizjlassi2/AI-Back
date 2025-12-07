package com.aiplus.backend.datasets.dto.response;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.annotation.ReadOnlyProperty;

import com.aiplus.backend.comments.dto.DatasetCommentDto;
import com.aiplus.backend.datasets.dto.PurchasePlanDto;
import com.aiplus.backend.datasets.dto.TagDto;
import com.aiplus.backend.datasets.model.DatasetFormat;
import com.aiplus.backend.datasets.model.LicenseType;

import com.aiplus.backend.datasets.model.Visibility;
import com.aiplus.backend.users.dto.UserSummaryDto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DatasetDTO {

    @NotEmpty
    @NotNull
    private Long id;

    @NotEmpty
    @NotNull
    private String name;

    @NotEmpty
    @NotNull
    private String description;

    private Visibility visibility;

    private DatasetFormat format;

    private String size;

    private Integer sampleCount = 0;

    private LicenseType license;

    private String customLicenseUrl;

    private List<TagDto> tags;

    private PurchasePlanDto purchasePlan;

    private List<DatasetCommentDto> comments;

    @NotEmpty
    @NotNull
    private UserSummaryDto developer;

    @ReadOnlyProperty
    private LocalDate createdAt;

    @ReadOnlyProperty
    private LocalDate updatedAt;

}
