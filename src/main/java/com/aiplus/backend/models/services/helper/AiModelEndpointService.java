package com.aiplus.backend.models.services.helper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.aiplus.backend.endpoints.dto.EndpointDto;
import com.aiplus.backend.endpoints.mapper.EndpointMapper;
import com.aiplus.backend.endpoints.model.Endpoint;
import com.aiplus.backend.endpoints.repository.EndpointRepository;
import com.aiplus.backend.models.exceptions.EndpointNotFoundException;
import com.aiplus.backend.models.model.AiModel;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiModelEndpointService {

    private final EndpointMapper endpointMapper;
    private final EndpointRepository endpointRepository;

    /**
     * Attaches/updates endpoints from DTO list. - If DTO has ID, loads existing
     * endpoint, updates fields manually, and reattaches. - If no ID, creates a new
     * endpoint using the mapper.
     *
     * @param dtoList endpoints from create/update DTO
     * @param model   target AiModel
     * @return list of (updated or new) Endpoint entities
     * @throws EndpointNotFoundException if endpointDto.id is present but not found
     *                                   in DB
     */
    public List<Endpoint> attachEndpoints(List<EndpointDto> dtoList, AiModel model) {
        List<Endpoint> endpoints = new ArrayList<>();
        if (dtoList != null && !dtoList.isEmpty()) {
            for (EndpointDto dto : dtoList) {
                Endpoint endpoint;
                if (dto.getId() != null) {
                    // Update existing
                    endpoint = endpointRepository.findById(dto.getId())
                            .orElseThrow(() -> new EndpointNotFoundException(dto.getId()));
                    // Manual field updates
                    endpoint.setMethod(dto.getMethod());
                    endpoint.setPath(dto.getPath());
                    endpoint.setDescription(dto.getDescription());
                    endpoint.setRequestBody(dto.getRequestBody());
                    endpoint.setSuccessResponse(dto.getSuccessResponse());
                    endpoint.setErrorResponse(dto.getErrorResponse());
                } else {
                    // Create new
                    endpoint = endpointMapper.toEntity(dto);
                }
                endpoint.setModel(model);
                endpoints.add(endpointRepository.save(endpoint));
            }
        }
        return endpoints;
    }
}