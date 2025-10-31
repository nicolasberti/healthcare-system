package com.healthcare.claim_service.domain.port.out;

import com.healthcare.claim_service.common.command.CreateClaim;
import com.healthcare.claim_service.domain.model.claim.Claim;
import com.healthcare.claim_service.domain.model.claim.valueobject.ClaimId;
import com.healthcare.claim_service.domain.model.claim.valueobject.ClaimStatus;

import java.util.Optional;

public interface ClaimRepository {
    Claim save(Claim claim);
    Optional<Claim> findById(ClaimId id);
    Claim update(ClaimId id, ClaimStatus status);
}
