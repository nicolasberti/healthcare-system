package com.healthcare.notification_service.controller;

import com.healthcare.notification_service.model.RecommendationRequest;
import com.healthcare.notification_service.model.RecommendationResponse;
import com.healthcare.notification_service.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @PostMapping
    public ResponseEntity<RecommendationResponse> recommendPlan(
            @RequestBody RecommendationRequest request
    ) {
        RecommendationResponse response =
                recommendationService.classify(request);

        return ResponseEntity.ok(response);
    }
}
