package com.healthcare.member_service.common.audit.model;

public record AuditPayload(
        Object[] args,
        Object result
) {}
