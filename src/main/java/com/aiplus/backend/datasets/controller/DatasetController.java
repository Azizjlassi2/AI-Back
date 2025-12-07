package com.aiplus.backend.datasets.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aiplus.backend.datasets.dto.response.DatasetDTO;
import com.aiplus.backend.datasets.service.DatasetService;
import com.aiplus.backend.utils.responses.ApiResponse;
import com.aiplus.backend.utils.responses.ResponseUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/datasets")
public class DatasetController {

    private final DatasetService datasetService;

    /**
     * Fetches all datasets.
     *
     * @return ResponseEntity containing a list of all datasets.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<DatasetDTO>>> getAll(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String dir) {
        Sort.Direction direction = "desc".equalsIgnoreCase(dir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));

        return ResponseEntity.ok(ResponseUtil.success("Datasets fetched", datasetService.getAllDatasets(pageable)));
    }

}
