package com.healthcare.member_service.infrastructure.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class RecommendationService {

    @Value("${ia.recommendation.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public RecommendationService() {
        this.restTemplate = new RestTemplate();
    }

    /*
     * TODO: En este metodo se podría recibir otro "request", por ejemplo el
     * memberId y de ahì armar el RecommendationRequest con los datos del miembro
     * obtenidos de la DB
     */
    public RecommendationResponse getRecommendation(RecommendationRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<RecommendationRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<RecommendationResponse> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                entity,
                RecommendationResponse.class);

        return response.getBody();
    }
}
