package com.healthcare.claim_service.domain.model.claim.valueobject;

import com.healthcare.claim_service.domain.model.claim.exception.ClaimNotFoundException;

public enum ClaimStatus {
    PENDING,
    APPROVED,
    REJECTED;

    // TODO: Refactor -> no cumple Open/Closed principle.
    public static ClaimStatus transformStatus(String status) {
        switch (status.toUpperCase()) {
            case "PENDING":
                return PENDING;
            case "APPROVED":
                return APPROVED;
            case "REJECTED":
                return REJECTED;
            default:
                throw new ClaimNotFoundException("Invalid claim status: " + status);
        }
    }
}
