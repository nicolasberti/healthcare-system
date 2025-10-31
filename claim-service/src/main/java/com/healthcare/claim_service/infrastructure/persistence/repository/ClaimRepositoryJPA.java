package com.healthcare.claim_service.infrastructure.persistence.repository;

import com.healthcare.claim_service.infrastructure.persistence.model.ClaimEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimRepositoryJPA extends JpaRepository<ClaimEntity, String> {
}