package com.aiplus.backend.datasets.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.aiplus.backend.config.DatasetConfig;
import com.aiplus.backend.datasets.dto.DatasetCreateDTO;
import com.aiplus.backend.datasets.dto.DatasetUpdateDTO;
import com.aiplus.backend.datasets.dto.response.DatasetDTO;
import com.aiplus.backend.datasets.mapper.DatasetMapper;
import com.aiplus.backend.datasets.model.Dataset;
import com.aiplus.backend.datasets.model.DatasetFile;
import com.aiplus.backend.datasets.model.PurchasePlan;
import com.aiplus.backend.datasets.model.Sample;
import com.aiplus.backend.datasets.model.Tag;
import com.aiplus.backend.datasets.model.Visibility;
import com.aiplus.backend.datasets.repository.DatasetFileRepository;
import com.aiplus.backend.datasets.repository.DatasetRepository;
import com.aiplus.backend.datasets.repository.PurchasePlanRepository;
import com.aiplus.backend.datasets.repository.SampleRepository;
import com.aiplus.backend.datasets.repository.TagRepository;
import com.aiplus.backend.users.model.DeveloperAccount;
import com.aiplus.backend.users.model.User;
import com.aiplus.backend.users.service.AccountService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service class for managing datasets
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class DatasetService {

    private final DatasetRepository datasetRepository;
    private final TagRepository tagRepository;
    private final SampleRepository sampleRepository;
    private final PurchasePlanRepository purchasePlanRepository;
    private final DatasetFileRepository datasetFileRepository;
    private final AccountService accountService;
    private final DatasetConfig datasetConfig;
    private final DatasetMapper datasetMapper;

    public List<Dataset> getAllDatasets() {
        log.info("Fetching all datasets");
        return datasetRepository.findAll();
    }

    public Dataset createDataset(DatasetCreateDTO request, List<MultipartFile> files, User user) {

        log.info("Creating dataset with name: {}", request.getName());
        DeveloperAccount developerAccount = (DeveloperAccount) accountService.findByUser(user);
        log.info("Developer account ID: {}", developerAccount.getId());

        Dataset d = new Dataset();
        d.setName(request.getName());
        d.setDescription(request.getDescription());
        d.setVisibility(request.getVisibility());
        d.setFormat(request.getFormat());
        d.setSampleCount(request.getSampleCount());
        d.setLicense(request.getLicense());
        d.setDeveloperAccount(developerAccount);

        log.info("Dataset object built: {}", d);

        Dataset dataset = datasetRepository.save(d);

        log.info("Dataset object built: {}", dataset);
        // Save tags
        log.info("Saving tags");
        List<Tag> tags = request.getTags().stream().map(dto -> {
            Tag tag = new Tag();
            tag.setName(dto.getName());
            tag.setDataset(dataset);
            return tag;
        }).collect(Collectors.toList());
        dataset.setTags(tags);
        log.info("Tags saved: {}", tags.size());

        // Save samples
        log.info("Saving samples");
        List<Sample> samples = request.getSamples().stream().map(dto -> {
            Sample sample = new Sample();
            sample.setContent(dto.getContent());
            sample.setLabel(dto.getLabel());
            sample.setMetadata(dto.getMetadata());
            sample.setUrl(dto.getUrl());
            sample.setMimeType(dto.getMimeType());
            sample.setDataset(dataset);
            return sample;
        }).collect(Collectors.toList());
        dataset.setSamples(samples);
        log.info("Samples saved: {}", samples.size());

        log.info("Saving purchase plan if present");
        // Save purchase plan if present
        if (request.getPurchasePlan() != null) {
            PurchasePlan plan = new PurchasePlan();
            plan.setName(request.getPurchasePlan().getName());
            plan.setDescription(request.getPurchasePlan().getDescription());
            plan.setPrice(request.getPurchasePlan().getPrice());
            plan.setCurrency(request.getPurchasePlan().getCurrency());
            plan.setBillingPeriod(request.getPurchasePlan().getBillingPeriod());
            plan.setFeatures(request.getPurchasePlan().getFeatures());
            plan.setDataset(dataset);
            dataset.setPurchasePlan(plan);
        }
        log.info("Purchase plan processed");

        log.info("Saving dataset");
        Dataset savedDataset = datasetRepository.save(dataset);
        log.info("Dataset saved with ID: {}", savedDataset.getId());

        log.info("Handling file uploads ");
        // Handle file uploads
        if (files != null && !files.isEmpty()) {
            uploadFiles(savedDataset, files);
            updateSize(savedDataset); // Recalculate size
        }
        log.info("Dataset creation process completed");

        return savedDataset;
    }

    private void uploadFiles(Dataset dataset, List<MultipartFile> files) {
        log.info("Uploading files for dataset ID: {}", dataset.getId());
        try {
            String dirPath = Files
                    .createDirectories(Paths.get(datasetConfig.getUploadDir(), dataset.getId().toString())).toString();
            log.info("Directory created at: {}", dirPath);
            long totalSize = 0;
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                    Path filePath = Paths.get(datasetConfig.getUploadDir(), dataset.getId().toString(), fileName);
                    Files.copy(file.getInputStream(), filePath);
                    log.info("File copied to: {}", filePath.toString());
                    DatasetFile dsFile = new DatasetFile();
                    dsFile.setOriginalName(file.getOriginalFilename());
                    dsFile.setFilePath(filePath.toString());
                    dsFile.setSize(file.getSize());
                    dsFile.setDataset(dataset);
                    log.info("Saving dataset file: {}", dsFile.getOriginalName());
                    datasetFileRepository.save(dsFile);
                    log.info("Dataset file saved: {}", dsFile.getOriginalName());
                    totalSize += file.getSize();
                }
            }
            log.info("Files uploaded: {}", files.size());
            dataset.setSize(formatSize(totalSize)); // e.g., "1.2 GB"
        } catch (IOException e) {
            log.error("File upload failed", e);
            throw new RuntimeException("File upload failed", e);
        } catch (NullPointerException e) {
            log.error("File upload failed due to null pointer", e);
            throw new RuntimeException("File upload failed due to null pointer", e);
        }
    }

    /**
     * 
     */
    private String formatSize(long bytes) {
        log.info("Formatting size for bytes: {}", bytes);
        double sizeInMB = bytes / (1024.0 * 1024);
        log.info("Dataset Size = {}", sizeInMB);
        return String.format("%.2f MB", sizeInMB); // Extend for GB/TB
    }

    /**
     * 
     */
    private void updateSize(Dataset dataset) {
        log.info("Updating size for dataset ID: {}", dataset.getId());
        long totalSize = dataset.getFiles().stream().mapToLong(DatasetFile::getSize).sum();
        dataset.setSize(formatSize(totalSize));
        datasetRepository.save(dataset);
        log.info("Size updated for dataset ID: {}", dataset.getId());
    }

    public Dataset getDataset(Long id) {

        return datasetRepository.findById(id).orElse(null);
    }

    /**
     * Update a dataset by its ID
     */
    public Dataset updateDataset(Long id, DatasetUpdateDTO dataset) {

        return null;
    }

    /**
     * Delete a dataset by its ID
     */
    public void deleteDataset(Long id) {
        datasetRepository.deleteById(id);

    }

    /**
     * Get datasets for a specific developer
     */
    public List<Dataset> getDeveloperDatasets(User user) {
        List<Dataset> datasets = datasetRepository.findByDeveloperAccountId(user.getAccount().getId());
        log.info("Found {} datasets for developer ID: {}", datasets.size(), user.getId());
        return datasets;
    }

    public Page<DatasetDTO> getAllDatasets(Pageable pageable) {

        Page<Dataset> datasets = datasetRepository.findByVisibility(Visibility.PUBLIC, pageable);
        return datasetMapper.toDtoPage(datasets);

    }

}