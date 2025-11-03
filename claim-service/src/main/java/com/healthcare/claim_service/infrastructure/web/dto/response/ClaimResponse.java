package com.healthcare.claim_service.infrastructure.web.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ClaimResponse {
    private String id;
    private String memberId;
    private double amount;
    private String status;
}
