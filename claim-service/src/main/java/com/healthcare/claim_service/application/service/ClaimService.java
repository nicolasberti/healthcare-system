package com.healthcare.claim_service.application.service;

import com.healthcare.claim_service.common.command.CreateClaim;
import com.healthcare.claim_service.common.command.UpdateClaim;
import com.healthcare.claim_service.domain.model.claim.Claim;
import com.healthcare.claim_service.domain.model.claim.exception.ClaimNotFoundException;
import com.healthcare.claim_service.domain.model.claim.valueobject.ClaimId;
import com.healthcare.claim_service.domain.model.claim.valueobject.ClaimStatus;
import com.healthcare.claim_service.domain.model.claim.valueobject.PolicyId;
import com.healthcare.claim_service.domain.port.in.ClaimUseCase;
import com.healthcare.claim_service.domain.port.out.ClaimRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class ClaimService implements ClaimUseCase {
    private final ClaimRepository claimRepository;
    @Override
    public Claim create(CreateClaim createClaim) {
        log.info("Creating claim: {}", createClaim.getPolicyId());
        Claim claim = Claim.builder()
                .id(new ClaimId())
                .policyId(new PolicyId(createClaim.getPolicyId()))
                .amount(createClaim.getAmount())
                .status(ClaimStatus.transformStatus(createClaim.getStatus()))
                .build();
        log.info("Claim: {}", claim.getPolicyId().getValue().toString());
        return claimRepository.save(claim);
    }

    @Override
    public Claim update(ClaimId id, UpdateClaim status) {
        ClaimStatus statusEnum = ClaimStatus.transformStatus(status.getStatus());
        return claimRepository.update(id, statusEnum);
    }

    @Override
    public Claim findById(ClaimId claimId) {
        return claimRepository.findById(claimId).orElseThrow(() -> new ClaimNotFoundException("Claim not found"));
    }
}
