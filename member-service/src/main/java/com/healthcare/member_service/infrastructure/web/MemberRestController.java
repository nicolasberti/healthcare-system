package com.healthcare.member_service.infrastructure.web;

import com.healthcare.member_service.domain.model.member.valueobject.MemberId;
import com.healthcare.member_service.domain.port.in.MemberUseCase;
import com.healthcare.member_service.infrastructure.ai.RecommendationResponse;
import com.healthcare.member_service.infrastructure.ai.RecommendationService;
import com.healthcare.member_service.infrastructure.web.dto.ApiResponse;
import com.healthcare.member_service.common.command.CreateMember;
import com.healthcare.member_service.infrastructure.web.dto.query.MemberResponse;
import com.healthcare.member_service.infrastructure.web.mapper.MemberMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/members")
public class MemberRestController implements MemberController {
    private final MemberUseCase memberUseCase;
    private final RecommendationService recommendationService;

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<ApiResponse<MemberResponse>> getMember(@PathVariable String id) {
        log.info("Controller -> ID: {}", id);
        MemberId memberId = new MemberId(id);
        log.info("Controller -> memberId: {}", memberId.toString());
        MemberResponse memberResponse = MemberMapper.memberToMemberResponse(memberUseCase.getMemberById(memberId));
        return ResponseEntity.ok(ApiResponse.createSuccess(memberResponse, "Member retrieved successfully"));
    }

    @GetMapping
    @Override
    public ResponseEntity<ApiResponse<List<MemberResponse>>> getAllMember() {
        List<MemberResponse> memberResponses = memberUseCase.getAllMembers()
                .stream()
                .map(MemberMapper::memberToMemberResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.createSuccess(memberResponses, "Members retrieved successfully"));
    }

    @PostMapping
    @Override
    public ResponseEntity<ApiResponse<MemberResponse>> createMember(@RequestBody CreateMember member) {
        log.info("Create member recived in controller: {}", member.toString());
        MemberResponse memberResponse = MemberMapper.memberToMemberResponse(memberUseCase.createMember(member));
        return ResponseEntity.ok(ApiResponse.createSuccess(memberResponse, "Member created successfully"));
    }

    @GetMapping("/{memberId}/recommendation")
    @Override
    public ResponseEntity<ApiResponse<RecommendationResponse>> getPlanRecommendation(@PathVariable String memberId) {
        log.info("Getting plan recommendation for memberId: {}", memberId);
        RecommendationResponse recommendation = recommendationService.getRecommendationByMemberId(memberId);
        return ResponseEntity.ok(ApiResponse.createSuccess(recommendation, "Plan recommendation retrieved successfully"));
    }
}
