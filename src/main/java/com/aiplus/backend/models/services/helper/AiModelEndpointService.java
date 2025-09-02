package com.aiplus.backend.models.services.helper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.aiplus.backend.endpoints.dto.EndpointDto;
import com.aiplus.backend.endpoints.mapper.EndpointMapper;
import com.aiplus.backend.endpoints.model.Endpoint;
import com.aiplus.backend.endpoints.repository.EndpointRepository;
import com.aiplus.backend.models.model.AiModel;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiModelEndpointService {

    private final EndpointMapper endpointMapper;
    private final EndpointRepository endpointRepository;

    /**
     * Maps endpoint DTO list into detached entities, sets model reference.
     *
     * @param dtoList endpoints from create/update DTO
     * @param model   target AiModel
     * @return list of attached Endpoint entities
     */
    public List<Endpoint> attachEndpoints(List<EndpointDto> dtoList, AiModel model) {
        List<Endpoint> endpoints;
        try {

            endpoints = new ArrayList<>();

            // Map and set back-reference

            dtoList.forEach(dto -> {

                Endpoint endpoint = endpointMapper.toEntity(dto);
                endpoint.setModel(model);
                endpoints.add(endpointRepository.save(endpoint));
            });
            return endpoints;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;

        }
    }
}
