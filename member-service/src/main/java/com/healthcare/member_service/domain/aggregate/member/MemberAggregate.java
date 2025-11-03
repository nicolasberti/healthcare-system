package com.healthcare.member_service.domain.aggregate.member;

import com.healthcare.member_service.domain.event.member.MemberCreated;
import com.healthcare.member_service.domain.event.member.MemberDomainEvent;
import com.healthcare.member_service.domain.model.member.Member;
import com.healthcare.member_service.domain.model.member.valueobject.Age;
import com.healthcare.member_service.domain.model.member.valueobject.Email;
import com.healthcare.member_service.domain.model.member.valueobject.MemberId;
import com.healthcare.member_service.domain.model.member.valueobject.Phone;

import java.util.Collections;
import java.util.List;

public class MemberAggregate {
    private final Member member;
    private final List<MemberDomainEvent> domainEvents;

    private MemberAggregate(Member member, List<MemberDomainEvent> domainEvents) {
        this.member = member;
        this.domainEvents = domainEvents;
    }

    public static MemberAggregate createMember(MemberId id, String name, Email email, Age age, Phone phone) {
        Member member = new Member(id, name, email, age, phone);

        MemberCreated event = new MemberCreated(id.getValue().toString(), name, email.value(), age.value(), phone.value());
        return new MemberAggregate(member, Collections.singletonList(event));
    }

    public Member getMember() {
        return member;
    }

    public List<MemberDomainEvent> getDomainEvents() {
        return domainEvents;
    }

}
