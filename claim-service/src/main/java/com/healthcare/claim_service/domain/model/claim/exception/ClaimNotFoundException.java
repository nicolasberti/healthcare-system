package com.healthcare.claim_service.domain.model.claim.exception;

public class ClaimNotFoundException extends RuntimeException {
    public ClaimNotFoundException(String message) {
        super(message);
    }
}
