package com.healthcare.notification_service.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberCreatedEvent {
    private String name;
    private String email;
    private int age;
}
