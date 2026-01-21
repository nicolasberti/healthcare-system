package com.healthcare.member_service.infrastructure.ai;

import com.healthcare.member_service.domain.model.member.Member;
import com.healthcare.member_service.domain.model.member.valueobject.MemberId;
import com.healthcare.member_service.domain.port.out.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class RecommendationService {

    @Value("${ia.recommendation.url}")
    private String apiUrl;

    private final MemberRepository memberRepository;
    private final RestTemplate restTemplate;

    private static final List<RecommendationRequest.AvailablePlan> AVAILABLE_PLANS = new ArrayList<>();

    static {
        RecommendationRequest.AvailablePlan basicPlan = new RecommendationRequest.AvailablePlan();
        basicPlan.setPlanCode("PLAN-BASIC-01");
        basicPlan.setName("Basic Health Plan");
        basicPlan.setDescription("Essential coverage with limited benefits and low monthly cost.");
        basicPlan.setTargetAudience("Young adults with low medical usage");
        basicPlan.setCoverages(List.of("HOSPITALIZATION", "OUTPATIENT"));

        RecommendationRequest.AvailablePlan goldPlan = new RecommendationRequest.AvailablePlan();
        goldPlan.setPlanCode("PLAN-GOLD-02");
        goldPlan.setName("Gold Comprehensive Plan");
        goldPlan.setDescription("High coverage plan including chronic disease management and low copayments.");
        goldPlan.setTargetAudience("Members seeking comprehensive coverage and financial protection");
        goldPlan.setCoverages(List.of("HOSPITALIZATION", "OUTPATIENT", "DENTAL", "MENTAL_HEALTH"));

        AVAILABLE_PLANS.add(basicPlan);
        AVAILABLE_PLANS.add(goldPlan);
    }

    public RecommendationService(MemberRepository memberRepository, RestTemplate restTemplate) {
        this.memberRepository = memberRepository;
        this.restTemplate = restTemplate;
    }

    public RecommendationResponse getRecommendationByMemberId(String memberId) {
        Member member = memberRepository.findById(new MemberId(memberId))
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));

        RecommendationRequest request = buildRecommendationRequest(member);

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

    private RecommendationRequest buildRecommendationRequest(Member member) {
        RecommendationRequest request = new RecommendationRequest();

        RecommendationRequest.MemberProfile memberProfile = new RecommendationRequest.MemberProfile();
        memberProfile.setAge(member.getAge().value());

        RecommendationRequest.MedicalHistory medicalHistory = new RecommendationRequest.MedicalHistory();
        medicalHistory.setHasChronicConditions(false);
        medicalHistory.setChronicConditions(new ArrayList<>());
        medicalHistory.setSmoker(false);
        medicalHistory.setPreExistingConditions(false);

        RecommendationRequest.Preferences preferences = new RecommendationRequest.Preferences();
        preferences.setRiskTolerance("LOW");
        preferences.setBudgetLevel("MEDIUM");
        preferences.setPreferredCoverages(List.of("HOSPITALIZATION", "OUTPATIENT", "DENTAL"));

        memberProfile.setMedicalHistory(medicalHistory);
        memberProfile.setPreferences(preferences);

        request.setMemberProfile(memberProfile);
        request.setAvailablePlans(AVAILABLE_PLANS);

        return request;
    }
}
