package com.healthcare.claim_service.infrastructure.persistence.mapper;

import com.healthcare.claim_service.common.command.CreateClaim;
import com.healthcare.claim_service.domain.model.claim.Claim;
import com.healthcare.claim_service.domain.model.claim.valueobject.ClaimId;
import com.healthcare.claim_service.domain.model.claim.valueobject.ClaimStatus;
import com.healthcare.claim_service.domain.model.claim.valueobject.PolicyId;
import com.healthcare.claim_service.infrastructure.persistence.model.ClaimEntity;

public class ClaimMapper {
    public static ClaimEntity claimToClaimEntity(Claim claim) {
        return ClaimEntity.builder()
                .id(claim.getId().getValue().toString())
                .memberId(claim.getMemberId())
                .amount(claim.getAmount())
                .status(claim.getStatus().toString())
                .build();
    }

    public static Claim claimEntityToClaim(ClaimEntity claimEntity) {
        return Claim.builder()
                .id(new ClaimId(claimEntity.getId()))
                .memberId(claimEntity.getMemberId())
                .amount(claimEntity.getAmount())
                .status(ClaimStatus.transformStatus(claimEntity.getStatus()))
                .build();
    }
}
