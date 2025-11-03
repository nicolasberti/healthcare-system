package com.healthcare.claim_service.application.service;

import com.healthcare.claim_service.common.command.CreateClaim;
import com.healthcare.claim_service.common.command.UpdateClaim;
import com.healthcare.claim_service.domain.aggregate.ClaimAggregate;
import com.healthcare.claim_service.domain.model.claim.Claim;
import com.healthcare.claim_service.domain.model.claim.exception.ClaimNotFoundException;
import com.healthcare.claim_service.domain.model.claim.valueobject.ClaimId;
import com.healthcare.claim_service.domain.model.claim.valueobject.ClaimStatus;
import com.healthcare.claim_service.domain.model.claim.valueobject.PolicyId;
import com.healthcare.claim_service.domain.port.in.ClaimUseCase;
import com.healthcare.claim_service.domain.port.out.ClaimRepository;
import com.healthcare.claim_service.domain.port.out.DomainEventPublisher;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class ClaimService implements ClaimUseCase {
    private final ClaimRepository claimRepository;
    private final DomainEventPublisher domainEventPublisher;

    @Override
    public Claim create(CreateClaim createClaim) {
        ClaimAggregate claimAggregate = ClaimAggregate.create(createClaim.getMemberId(), createClaim.getAmount(), ClaimStatus.transformStatus(createClaim.getStatus()));

        // TODO: Agregar outbox pattern
        claimAggregate.getEvents().forEach(event ->
                domainEventPublisher.publish("Claim", claimAggregate.getClaim().getId(), event)
        );

        return claimRepository.save(claimAggregate.getClaim());
    }

    @Override
    public Claim update(ClaimId id, UpdateClaim status) {
        Claim claim = claimRepository.findById(id).orElseThrow(() -> new ClaimNotFoundException("Claim not found"));
        ClaimAggregate claimUpdated = ClaimAggregate.update(claim, ClaimStatus.transformStatus(status.getStatus()));

        // TODO: Agregar outbox pattern
        claimUpdated.getEvents().forEach(event ->
                domainEventPublisher.publish("Claim", claimUpdated.getClaim().getId(), event)
        );

        return claimRepository.save(claimUpdated.getClaim());
    }

    @Override
    public Claim findById(ClaimId claimId) {
        return claimRepository.findById(claimId).orElseThrow(() -> new ClaimNotFoundException("Claim not found"));
    }
}
