package com.healthcare.member_service.infrastructure.web;

import com.healthcare.member_service.infrastructure.web.dto.ApiResponse;
import com.healthcare.member_service.common.command.CreateMember;
import com.healthcare.member_service.infrastructure.web.dto.query.MemberResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface MemberController {
    ResponseEntity<ApiResponse<MemberResponse>> getMember(String id);
    ResponseEntity<ApiResponse<List<MemberResponse>>> getAllMember();
    ResponseEntity<ApiResponse<MemberResponse>> createMember(CreateMember member);
}
