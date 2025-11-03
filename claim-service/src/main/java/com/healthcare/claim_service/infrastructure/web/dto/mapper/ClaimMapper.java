package com.healthcare.claim_service.infrastructure.web.dto.mapper;

import com.healthcare.claim_service.domain.model.claim.Claim;
import com.healthcare.claim_service.infrastructure.web.dto.response.ClaimResponse;

public class ClaimMapper {
    public static ClaimResponse claimToClaimResponse(Claim claim) {
        return ClaimResponse.builder()
                .id(claim.getId().getValue().toString())
                .memberId(claim.getMemberId())
                .amount(claim.getAmount())
                .status(claim.getStatus().toString())
                .build();
    }
}
