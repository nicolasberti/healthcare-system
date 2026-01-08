package com.healthcare.member_service.common.audit.model;

public record ErrorPayload(
        Object[] args,
        String error
) {}
