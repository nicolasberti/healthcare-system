package com.healthcare.claim_service.domain.port.in;

import com.healthcare.claim_service.common.command.CreateClaim;
import com.healthcare.claim_service.common.command.UpdateClaim;
import com.healthcare.claim_service.domain.model.claim.Claim;
import com.healthcare.claim_service.domain.model.claim.valueobject.ClaimId;
import com.healthcare.claim_service.domain.model.claim.valueobject.ClaimStatus;

public interface ClaimUseCase {
    Claim create(CreateClaim createClaim);
    Claim update(ClaimId id, UpdateClaim status);
    Claim findById(ClaimId id);
}
