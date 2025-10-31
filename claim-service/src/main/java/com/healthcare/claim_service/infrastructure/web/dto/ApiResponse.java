package com.healthcare.claim_service.infrastructure.web.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record ApiResponse<T>(
        boolean success,
        T data,
        String message,
        Instant timestamp
) {
    public static <T> ApiResponse<T> createSuccess(T data, String message) {
        return new ApiResponse<>(true, data, message, Instant.now());
    }

    public static ApiResponse<Object> createError(String message) {
        return new ApiResponse<>(false, null, message, Instant.now());
    }
}
