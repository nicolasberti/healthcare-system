package com.healthcare.security_service.repository;

import com.healthcare.security_service.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    // Acá tendría que llegar la contraseña cifrada
    Optional<Member> findByEmailAndPassword(String email, String password);
}
