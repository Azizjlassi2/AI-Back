package com.aiplus.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class DatasetConfig {

    @Value("${app.upload.dir}")
    private String uploadDir;

}
