package com.healthcare.member_service.domain.event.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MemberCreated implements MemberDomainEvent {
    private final String name;
    private final String email;
    private final int age;
}
