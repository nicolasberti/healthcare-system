package com.healthcare.member_service.domain.model.member;

import com.healthcare.member_service.domain.model.member.valueobject.Age;
import com.healthcare.member_service.domain.model.member.valueobject.Email;
import com.healthcare.member_service.domain.model.member.valueobject.MemberId;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
public class Member {
    private MemberId id;
    private String name;
    private Email email;
    private Age age;

    public static Member createMember(MemberId id, String name, Email email, Age age) {
        return new Member(id, name, email, age);
    }

}
