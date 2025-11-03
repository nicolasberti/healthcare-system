package com.healthcare.claim_service.domain.model.claim;

import com.healthcare.claim_service.domain.model.claim.valueobject.ClaimId;
import com.healthcare.claim_service.domain.model.claim.valueobject.ClaimStatus;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Claim {
    private ClaimId id;
    private String memberId;
    private double amount;
    private ClaimStatus status;
}
