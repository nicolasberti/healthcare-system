package com.healthcare.member_service.infrastructure.persistence.repository;

import com.healthcare.member_service.infrastructure.persistence.model.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface MemberRepositoryJPA extends JpaRepository<MemberEntity, String> {
}