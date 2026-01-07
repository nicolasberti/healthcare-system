package com.healthcare.security_service.model;

public record LoginRequest(
        String email,
        String password
) {}
