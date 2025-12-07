package com.aiplus.backend.datasets.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.aiplus.backend.comments.mapper.DatasetCommentMapper; // Reuse from previous implementation
import com.aiplus.backend.datasets.dto.response.DatasetDTO;
import com.aiplus.backend.datasets.model.Dataset;
import com.aiplus.backend.users.mapper.UserSummaryMapper;

@Mapper(componentModel = "spring", uses = { DatasetCommentMapper.class, TagMapper.class, PurchasePlanMapper.class,
        UserSummaryMapper.class })
public interface DatasetMapper {

    @Mapping(source = "developerAccount.user", target = "developer")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    DatasetDTO toDto(Dataset dataset);

    default Page<DatasetDTO> toDtoPage(Page<Dataset> page) {
        if (page.isEmpty()) {
            return Page.empty(page.getPageable());
        }
        return new PageImpl<>(page.getContent().stream().map(this::toDto).toList(), page.getPageable(),
                page.getTotalElements());
    }
}