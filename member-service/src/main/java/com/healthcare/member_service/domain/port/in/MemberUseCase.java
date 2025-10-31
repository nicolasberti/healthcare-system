package com.healthcare.member_service.domain.port.in;

import com.healthcare.member_service.common.command.CreateMember;
import com.healthcare.member_service.domain.model.member.Member;
import com.healthcare.member_service.domain.model.member.valueobject.MemberId;

import java.util.List;
import java.util.UUID;

public interface MemberUseCase {
    Member createMember(CreateMember createMember);
    Member getMemberById(MemberId id);
    List<Member> getAllMembers();
    Member updateMember(MemberId id, Member member);
    void deleteMember(MemberId id);
}
