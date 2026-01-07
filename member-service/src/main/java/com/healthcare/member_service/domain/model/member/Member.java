package com.healthcare.member_service.domain.model.member;

import com.healthcare.member_service.domain.model.member.valueobject.Age;
import com.healthcare.member_service.domain.model.member.valueobject.Email;
import com.healthcare.member_service.domain.model.member.valueobject.MemberId;
import com.healthcare.member_service.domain.model.member.valueobject.Phone;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
public class Member {
    private MemberId id;
    private String name;
    private String password;
    private Email email;
    private Age age;
    private Phone phone;
}
