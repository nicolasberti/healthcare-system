package com.healthcare.member_service.domain.model.member.valueobject;

import lombok.Getter;

import java.util.UUID;

@Getter
public class MemberId {
    private final UUID value;
    public MemberId(String value){
        this.value = UUID.fromString(value);
    }
    public MemberId(){
        this.value = UUID.randomUUID();
    }
}
