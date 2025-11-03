package com.healthcare.claim_service.domain.event.claim;

import com.healthcare.claim_service.domain.model.claim.valueobject.ClaimStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ClaimUpdated implements ClaimDomainEvent {
    private String id;
    private String memberId;
    private double amount;
    private ClaimStatus status;
}
