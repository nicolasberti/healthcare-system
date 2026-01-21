package com.healthcare.member_service.infrastructure.ai;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RecommendationRequest/Response DTO Tests")
class RecommendationDTOTest {

    private static final String PLAN_BASIC_CODE = "PLAN-BASIC-01";
    private static final String PLAN_GOLD_CODE = "PLAN-GOLD-02";

    @Test
    @DisplayName("Should create RecommendationRequest with all fields")
    void shouldCreateRecommendationRequestWithAllFields() {
        RecommendationRequest request = new RecommendationRequest();

        RecommendationRequest.MemberProfile profile = new RecommendationRequest.MemberProfile();
        profile.setAge(30);

        RecommendationRequest.MedicalHistory medicalHistory = new RecommendationRequest.MedicalHistory();
        medicalHistory.setHasChronicConditions(false);
        medicalHistory.setChronicConditions(Collections.emptyList());
        medicalHistory.setSmoker(false);
        medicalHistory.setPreExistingConditions(false);

        RecommendationRequest.Preferences preferences = new RecommendationRequest.Preferences();
        preferences.setRiskTolerance("LOW");
        preferences.setBudgetLevel("MEDIUM");
        preferences.setPreferredCoverages(Arrays.asList("HOSPITALIZATION", "OUTPATIENT"));

        profile.setMedicalHistory(medicalHistory);
        profile.setPreferences(preferences);

        List<RecommendationRequest.AvailablePlan> plans = createAvailablePlans();

        request.setMemberProfile(profile);
        request.setAvailablePlans(plans);

        assertNotNull(request);
        assertEquals(30, request.getMemberProfile().getAge());
        assertEquals("LOW", request.getMemberProfile().getPreferences().getRiskTolerance());
        assertEquals(2, request.getAvailablePlans().size());
        assertEquals(PLAN_BASIC_CODE, request.getAvailablePlans().get(0).getPlanCode());
    }

    @Test
    @DisplayName("Should create AvailablePlan with correct fields")
    void shouldCreateAvailablePlanWithCorrectFields() {
        RecommendationRequest.AvailablePlan plan = new RecommendationRequest.AvailablePlan();
        plan.setPlanCode(PLAN_BASIC_CODE);
        plan.setName("Basic Health Plan");
        plan.setDescription("Essential coverage");
        plan.setTargetAudience("Young adults");
        plan.setCoverages(Arrays.asList("HOSPITALIZATION", "OUTPATIENT"));

        assertEquals(PLAN_BASIC_CODE, plan.getPlanCode());
        assertEquals("Basic Health Plan", plan.getName());
        assertEquals("Essential coverage", plan.getDescription());
        assertEquals("Young adults", plan.getTargetAudience());
        assertEquals(2, plan.getCoverages().size());
    }

    @Test
    @DisplayName("Should create MedicalHistory with all fields")
    void shouldCreateMedicalHistoryWithAllFields() {
        RecommendationRequest.MedicalHistory history = new RecommendationRequest.MedicalHistory();
        history.setHasChronicConditions(true);
        history.setChronicConditions(Arrays.asList("Diabetes", "Hypertension"));
        history.setSmoker(true);
        history.setPreExistingConditions(true);

        assertTrue(history.isHasChronicConditions());
        assertEquals(2, history.getChronicConditions().size());
        assertTrue(history.getChronicConditions().contains("Diabetes"));
        assertTrue(history.isSmoker());
        assertTrue(history.isPreExistingConditions());
    }

    @Test
    @DisplayName("Should create Preferences with all fields")
    void shouldCreatePreferencesWithAllFields() {
        RecommendationRequest.Preferences preferences = new RecommendationRequest.Preferences();
        preferences.setRiskTolerance("HIGH");
        preferences.setBudgetLevel("HIGH");
        preferences.setPreferredCoverages(Arrays.asList("HOSPITALIZATION", "OUTPATIENT", "DENTAL", "MENTAL_HEALTH"));

        assertEquals("HIGH", preferences.getRiskTolerance());
        assertEquals("HIGH", preferences.getBudgetLevel());
        assertEquals(4, preferences.getPreferredCoverages().size());
    }

    @Test
    @DisplayName("Should create RecommendationResponse with all fields")
    void shouldCreateRecommendationResponseWithAllFields() {
        RecommendationResponse response = new RecommendationResponse();
        response.setRecommendedPlanCode(PLAN_GOLD_CODE);
        response.setReasoning("Recommended based on comprehensive analysis");
        response.setConfidenceScore(0.95);
        response.setAlternativePlanCodes(Arrays.asList(PLAN_BASIC_CODE));

        assertEquals(PLAN_GOLD_CODE, response.getRecommendedPlanCode());
        assertEquals("Recommended based on comprehensive analysis", response.getReasoning());
        assertEquals(0.95, response.getConfidenceScore());
        assertEquals(1, response.getAlternativePlanCodes().size());
        assertEquals(PLAN_BASIC_CODE, response.getAlternativePlanCodes().get(0));
    }

    @Test
    @DisplayName("Should handle empty alternatives list in RecommendationResponse")
    void shouldHandleEmptyAlternativesList() {
        RecommendationResponse response = new RecommendationResponse();
        response.setRecommendedPlanCode(PLAN_BASIC_CODE);
        response.setReasoning("Simple recommendation");
        response.setConfidenceScore(1.0);
        response.setAlternativePlanCodes(Collections.emptyList());

        assertEquals(0, response.getAlternativePlanCodes().size());
        assertTrue(response.getAlternativePlanCodes().isEmpty());
    }

    @Test
    @DisplayName("Should handle null alternative plans in RecommendationResponse")
    void shouldHandleNullAlternativePlans() {
        RecommendationResponse response = new RecommendationResponse();
        response.setRecommendedPlanCode(PLAN_BASIC_CODE);
        response.setReasoning("No alternatives available");
        response.setConfidenceScore(1.0);
        response.setAlternativePlanCodes(null);

        assertNull(response.getAlternativePlanCodes());
    }

    @Test
    @DisplayName("Should create MemberProfile with age and nested objects")
    void shouldCreateMemberProfileWithNestedObjects() {
        RecommendationRequest.MemberProfile profile = new RecommendationRequest.MemberProfile();
        profile.setAge(45);

        RecommendationRequest.MedicalHistory history = new RecommendationRequest.MedicalHistory();
        history.setHasChronicConditions(true);
        history.setChronicConditions(Collections.singletonList("Asthma"));
        history.setSmoker(false);
        history.setPreExistingConditions(true);

        RecommendationRequest.Preferences preferences = new RecommendationRequest.Preferences();
        preferences.setRiskTolerance("MEDIUM");
        preferences.setBudgetLevel("LOW");
        preferences.setPreferredCoverages(Collections.singletonList("HOSPITALIZATION"));

        profile.setMedicalHistory(history);
        profile.setPreferences(preferences);

        assertEquals(45, profile.getAge());
        assertNotNull(profile.getMedicalHistory());
        assertNotNull(profile.getPreferences());
        assertTrue(profile.getMedicalHistory().isHasChronicConditions());
        assertEquals("MEDIUM", profile.getPreferences().getRiskTolerance());
    }

    @Test
    @DisplayName("Should create multiple available plans in list")
    void shouldCreateMultipleAvailablePlans() {
        RecommendationRequest.AvailablePlan plan1 = new RecommendationRequest.AvailablePlan();
        plan1.setPlanCode("PLAN-001");
        plan1.setName("Plan 1");
        plan1.setDescription("Description 1");
        plan1.setTargetAudience("Audience 1");
        plan1.setCoverages(Arrays.asList("Coverage1", "Coverage2"));

        RecommendationRequest.AvailablePlan plan2 = new RecommendationRequest.AvailablePlan();
        plan2.setPlanCode("PLAN-002");
        plan2.setName("Plan 2");
        plan2.setDescription("Description 2");
        plan2.setTargetAudience("Audience 2");
        plan2.setCoverages(Arrays.asList("Coverage3", "Coverage4"));

        RecommendationRequest.AvailablePlan plan3 = new RecommendationRequest.AvailablePlan();
        plan3.setPlanCode("PLAN-003");
        plan3.setName("Plan 3");
        plan3.setDescription("Description 3");
        plan3.setTargetAudience("Audience 3");
        plan3.setCoverages(Arrays.asList("Coverage5"));

        List<RecommendationRequest.AvailablePlan> plans = Arrays.asList(plan1, plan2, plan3);

        assertEquals(3, plans.size());
        assertEquals("PLAN-001", plans.get(0).getPlanCode());
        assertEquals("PLAN-002", plans.get(1).getPlanCode());
        assertEquals("PLAN-003", plans.get(2).getPlanCode());
    }

    @Test
    @DisplayName("Should handle empty lists in medical history chronic conditions")
    void shouldHandleEmptyChronicConditions() {
        RecommendationRequest.MedicalHistory history = new RecommendationRequest.MedicalHistory();
        history.setHasChronicConditions(false);
        history.setChronicConditions(Collections.emptyList());
        history.setSmoker(false);
        history.setPreExistingConditions(false);

        assertFalse(history.isHasChronicConditions());
        assertTrue(history.getChronicConditions().isEmpty());
    }

    private List<RecommendationRequest.AvailablePlan> createAvailablePlans() {
        RecommendationRequest.AvailablePlan basicPlan = new RecommendationRequest.AvailablePlan();
        basicPlan.setPlanCode(PLAN_BASIC_CODE);
        basicPlan.setName("Basic Health Plan");
        basicPlan.setDescription("Essential coverage");
        basicPlan.setTargetAudience("Young adults");
        basicPlan.setCoverages(Arrays.asList("HOSPITALIZATION", "OUTPATIENT"));

        RecommendationRequest.AvailablePlan goldPlan = new RecommendationRequest.AvailablePlan();
        goldPlan.setPlanCode(PLAN_GOLD_CODE);
        goldPlan.setName("Gold Comprehensive Plan");
        goldPlan.setDescription("Comprehensive coverage");
        goldPlan.setTargetAudience("Families");
        goldPlan.setCoverages(Arrays.asList("HOSPITALIZATION", "OUTPATIENT", "DENTAL", "MENTAL_HEALTH"));

        return Arrays.asList(basicPlan, goldPlan);
    }
}
