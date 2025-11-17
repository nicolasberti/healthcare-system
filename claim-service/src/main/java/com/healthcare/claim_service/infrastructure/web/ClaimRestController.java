package com.healthcare.claim_service.infrastructure.web;

import com.healthcare.claim_service.common.command.CreateClaim;
import com.healthcare.claim_service.common.command.UpdateClaim;
import com.healthcare.claim_service.domain.model.claim.Claim;
import com.healthcare.claim_service.domain.model.claim.valueobject.ClaimId;
import com.healthcare.claim_service.domain.port.in.ClaimUseCase;
import com.healthcare.claim_service.infrastructure.web.dto.ApiResponse;
import com.healthcare.claim_service.infrastructure.web.dto.mapper.ClaimMapper;
import com.healthcare.claim_service.infrastructure.web.dto.response.ClaimResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v2/claims") //v2 x poner algo
public class ClaimRestController {
    private final ClaimUseCase claimUseCase;

    /*
    POST /api/claims - Create claim
    GET /api/claims/{id} - Get claim
    PUT /api/claims/{id}/status - Update status
   */
    @PostMapping
    public ApiResponse<ClaimResponse> createClaim(@RequestBody CreateClaim createClaim) {
        log.info("createClaim: memberId={}, amount={}, status={}", createClaim.getMemberId(), createClaim.getAmount(), createClaim.getStatus());
        Claim claim = claimUseCase.create(createClaim);
        ClaimResponse response = ClaimMapper.claimToClaimResponse(claim);
        return ApiResponse.createSuccess(response, "Claim created successfully");
    }

    @GetMapping("/{id}")
    public ApiResponse<ClaimResponse> getClaim(@PathVariable String id) {
        Claim claim = claimUseCase.findById(new ClaimId(id));
        ClaimResponse response = ClaimMapper.claimToClaimResponse(claim);
        return ApiResponse.createSuccess(response, "Claim retrieved successfully");
    }

    @PutMapping("/{id}")
    public ApiResponse<ClaimResponse> updateClaim(@PathVariable String id, @RequestBody UpdateClaim updateClaim) {
        Claim claim = claimUseCase.update(new ClaimId(id), updateClaim);
        ClaimResponse response = ClaimMapper.claimToClaimResponse(claim);
        return ApiResponse.createSuccess(response, "Claim updated successfully");
    }
}
