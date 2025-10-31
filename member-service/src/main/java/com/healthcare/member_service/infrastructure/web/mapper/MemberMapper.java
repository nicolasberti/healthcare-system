package com.healthcare.member_service.infrastructure.web.mapper;

import com.healthcare.member_service.domain.model.member.Member;
import com.healthcare.member_service.domain.model.member.valueobject.MemberId;
import com.healthcare.member_service.common.command.CreateMember;
import com.healthcare.member_service.infrastructure.web.dto.query.MemberResponse;

public class MemberMapper {
    public static MemberResponse memberToMemberResponse(Member member){
        return new MemberResponse(member.getName(), member.getEmail().value(), member.getAge().value());
    }
}
