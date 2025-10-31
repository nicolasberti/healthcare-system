package com.healthcare.claim_service.infrastructure.persistence;

import com.healthcare.claim_service.common.command.CreateClaim;
import com.healthcare.claim_service.domain.model.claim.Claim;
import com.healthcare.claim_service.domain.model.claim.exception.ClaimNotFoundException;
import com.healthcare.claim_service.domain.model.claim.valueobject.ClaimId;
import com.healthcare.claim_service.domain.model.claim.valueobject.ClaimStatus;
import com.healthcare.claim_service.domain.port.out.ClaimRepository;
import com.healthcare.claim_service.infrastructure.persistence.mapper.ClaimMapper;
import com.healthcare.claim_service.infrastructure.persistence.model.ClaimEntity;
import com.healthcare.claim_service.infrastructure.persistence.repository.ClaimRepositoryJPA;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
@Slf4j
public class ClaimRepositoryAdapter implements ClaimRepository {
    private final ClaimRepositoryJPA claimRepositoryJPA;

    @Override
    public Claim save(Claim claim) {
        ClaimEntity claimEntity = ClaimMapper.claimToClaimEntity(claim);
        log.info("ClaimEntity repository: {}", claimEntity.toString());
        claimRepositoryJPA.save(claimEntity);
        return claim;
    }

    @Override
    public Optional<Claim> findById(ClaimId id) {
        Optional<ClaimEntity> claimEntity = claimRepositoryJPA.findById(id.getValue().toString());
        return claimEntity.map(ClaimMapper::claimEntityToClaim);
    }

    @Override
    public Claim update(ClaimId id, ClaimStatus status) {
        Optional<ClaimEntity> claimEntity = claimRepositoryJPA.findById(id.getValue().toString());
        if (claimEntity.isEmpty())
            throw new ClaimNotFoundException("Claim not found");
        claimEntity.get().setStatus(status.toString());
        ClaimEntity claimUpdated = claimRepositoryJPA.save(claimEntity.get());
        return ClaimMapper.claimEntityToClaim(claimUpdated);
    }
}
