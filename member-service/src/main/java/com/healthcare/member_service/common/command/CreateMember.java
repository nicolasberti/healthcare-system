package com.healthcare.member_service.common.command;

import lombok.*;


@Getter
@AllArgsConstructor
@Builder
@ToString
public class CreateMember {
    private final String name;
    private final String password;
    private final String email;
    private final int age;
    private final String phone;
}