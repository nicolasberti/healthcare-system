package com.healthcare.member_service.domain.model.member.exception;

public class MemberAlreadyExistsException extends RuntimeException {
    public MemberAlreadyExistsException(String message) {
        super(message);
    }
}
