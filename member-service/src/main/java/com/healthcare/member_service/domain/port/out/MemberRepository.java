package com.healthcare.member_service.domain.port.out;

import com.healthcare.member_service.domain.model.member.Member;
import com.healthcare.member_service.domain.model.member.valueobject.MemberId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MemberRepository {
    Member save(Member member);
    Optional<Member> findById(MemberId id);
    List<Member> findAll();
    Member update(MemberId id, Member member);
    void delete(MemberId id);
}
