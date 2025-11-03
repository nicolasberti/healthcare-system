package com.healthcare.member_service.infrastructure.persistence.mapper;

import com.healthcare.member_service.domain.model.member.Member;
import com.healthcare.member_service.domain.model.member.valueobject.Age;
import com.healthcare.member_service.domain.model.member.valueobject.Email;
import com.healthcare.member_service.domain.model.member.valueobject.MemberId;
import com.healthcare.member_service.domain.model.member.valueobject.Phone;
import com.healthcare.member_service.infrastructure.persistence.model.MemberEntity;

import java.util.UUID;

public class MemberMapper {

    public static Member memberEntityToMember(MemberEntity memberEntity){
        return Member.builder()
                .id(new MemberId(memberEntity.getId()))
                .name(memberEntity.getName())
                .email(new Email(memberEntity.getEmail()))
                .age(new Age(memberEntity.getAge()))
                .phone(new Phone(memberEntity.getPhone()))
                .build();
    }

    public static MemberEntity memberToMemberEntity(Member member){
        return MemberEntity.builder()
                .id(member.getId().getValue().toString())
                .name(member.getName())
                .email(member.getEmail().value())
                .age(member.getAge().value())
                .phone(member.getPhone().value())
                .build();
    }
}
