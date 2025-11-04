package com.healthcare.notification_service.repository;

import com.healthcare.notification_service.model.MemberContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberContactRepository extends JpaRepository<MemberContact, String> {
}
