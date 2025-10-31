package com.healthcare.claim_service.domain.model.claim.valueobject;

import lombok.Getter;

import java.util.UUID;

@Getter
public class PolicyId {
    private UUID value;
    public PolicyId(String value) {
        this.value = UUID.fromString(value);
    }
    public PolicyId() {
        this.value = UUID.randomUUID();
    }
}
