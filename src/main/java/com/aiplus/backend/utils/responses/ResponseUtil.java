package com.aiplus.backend.utils.responses;

import java.util.Map;

public class ResponseUtil {

    public static <T> ApiResponse<T> success(String message, T data, Map<String, Object> metadata) {
        return new ApiResponse<>(true, message, data, metadata);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return success(message, data, null);
    }
}
