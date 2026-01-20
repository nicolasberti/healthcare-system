package com.healthcare.member_service.infrastructure.ai;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class RecommendationResponse {

    private String recommendedPlanCode;
    private String reasoning;
    private Double confidenceScore;
    private List<String> alternativePlanCodes;

    public RecommendationResponse() {
    }
}
