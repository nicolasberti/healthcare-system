package com.healthcare.claim_service.domain.aggregate;

import com.healthcare.claim_service.domain.event.claim.ClaimCreated;
import com.healthcare.claim_service.domain.event.claim.ClaimDomainEvent;
import com.healthcare.claim_service.domain.event.claim.ClaimUpdated;
import com.healthcare.claim_service.domain.model.claim.Claim;
import com.healthcare.claim_service.domain.model.claim.valueobject.ClaimId;
import com.healthcare.claim_service.domain.model.claim.valueobject.ClaimStatus;
import com.healthcare.claim_service.domain.model.claim.valueobject.PolicyId;
import lombok.Getter;

import java.util.List;

@Getter
public class ClaimAggregate {
    private Claim claim;
    private List<ClaimDomainEvent> events;

    private ClaimAggregate(Claim claim, List<ClaimDomainEvent> events) {
        this.claim = claim;
        this.events = events;
    }

    public static ClaimAggregate create(String memberId, double amount, ClaimStatus status) {
        Claim claim = Claim.builder()
                .id(new ClaimId())
                .memberId(memberId)
                .amount(amount)
                .status(status)
                .build();
        ClaimCreated event = new ClaimCreated(claim.getId().getValue().toString(), claim.getMemberId(), claim.getAmount(), claim.getStatus());
        return new ClaimAggregate(claim, List.of(event));
    }

    public static ClaimAggregate update(Claim claim, ClaimStatus status) {
        claim.setStatus(status);
        ClaimUpdated event = new ClaimUpdated(claim.getId().getValue().toString(), claim.getMemberId(), claim.getAmount(), claim.getStatus());
        return new ClaimAggregate(claim, List.of(event));
    }
}
