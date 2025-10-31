package com.healthcare.claim_service.domain.model.claim.valueobject;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ClaimId {
    private UUID value;
    public ClaimId(String value) {
        this.value = UUID.fromString(value);
    }
    public ClaimId() {
        this.value = UUID.randomUUID();
    }
}
