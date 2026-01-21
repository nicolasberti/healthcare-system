package com.healthcare.member_service.infrastructure.ai;

import com.healthcare.member_service.domain.model.member.Member;
import com.healthcare.member_service.domain.model.member.valueobject.Age;
import com.healthcare.member_service.domain.model.member.valueobject.Email;
import com.healthcare.member_service.domain.model.member.valueobject.MemberId;
import com.healthcare.member_service.domain.model.member.valueobject.Phone;
import com.healthcare.member_service.domain.port.out.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RecommendationService Tests")
class RecommendationServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RecommendationService recommendationService;

    private static final String TEST_MEMBER_ID = "123e4567-e89b-12d3-a456-426614174000";
    private static final String API_URL = "http://ia-service:8080/api/ia/recommendation";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(recommendationService, "apiUrl", API_URL);
    }

    @Test
    @DisplayName("Should return recommendation when member exists and IA service responds successfully")
    void getRecommendationByMemberId_ShouldReturnRecommendation_WhenMemberExistsAndIAResponds() {
        Member member = createTestMember();
        RecommendationResponse expectedResponse = createExpectedResponse();

        when(memberRepository.findById(any(MemberId.class))).thenReturn(Optional.of(member));
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(RecommendationResponse.class)
        )).thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        RecommendationResponse actualResponse = recommendationService.getRecommendationByMemberId(TEST_MEMBER_ID);

        assertNotNull(actualResponse);
        assertEquals("PLAN-GOLD-02", actualResponse.getRecommendedPlanCode());
        assertEquals("Recommended plan based on member profile", actualResponse.getReasoning());
        assertEquals(0.95, actualResponse.getConfidenceScore());
        assertTrue(actualResponse.getAlternativePlanCodes().contains("PLAN-BASIC-01"));

        verify(memberRepository, times(1)).findById(any(MemberId.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException when member is not found")
    void getRecommendationByMemberId_ShouldThrowException_WhenMemberNotFound() {
        when(memberRepository.findById(any(MemberId.class))).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> recommendationService.getRecommendationByMemberId(TEST_MEMBER_ID)
        );

        assertEquals("Member not found with id: " + TEST_MEMBER_ID, exception.getMessage());

        verify(memberRepository, times(1)).findById(any(MemberId.class));
        verifyNoInteractions(restTemplate);
    }

    @Test
    @DisplayName("Should build correct recommendation request with member age")
    void getRecommendationByMemberId_ShouldBuildCorrectRequest() {
        Member member = createTestMember();
        RecommendationResponse expectedResponse = createExpectedResponse();

        ArgumentCaptor<HttpEntity<?>> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        when(memberRepository.findById(any(MemberId.class))).thenReturn(Optional.of(member));
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                entityCaptor.capture(),
                eq(RecommendationResponse.class)
        )).thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        recommendationService.getRecommendationByMemberId(TEST_MEMBER_ID);

        HttpEntity<?> capturedEntity = entityCaptor.getValue();
        RecommendationRequest request = (RecommendationRequest) capturedEntity.getBody();

        assertNotNull(request);
        assertEquals(30, request.getMemberProfile().getAge());
        assertFalse(request.getMemberProfile().getMedicalHistory().isHasChronicConditions());
        assertFalse(request.getMemberProfile().getMedicalHistory().isSmoker());
        assertEquals("LOW", request.getMemberProfile().getPreferences().getRiskTolerance());
        assertEquals("MEDIUM", request.getMemberProfile().getPreferences().getBudgetLevel());
    }

    @Test
    @DisplayName("Should include available plans in request")
    void getRecommendationByMemberId_ShouldIncludeAvailablePlans() {
        Member member = createTestMember();
        RecommendationResponse expectedResponse = createExpectedResponse();

        ArgumentCaptor<HttpEntity<?>> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        when(memberRepository.findById(any(MemberId.class))).thenReturn(Optional.of(member));
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                entityCaptor.capture(),
                eq(RecommendationResponse.class)
        )).thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        recommendationService.getRecommendationByMemberId(TEST_MEMBER_ID);

        HttpEntity<?> capturedEntity = entityCaptor.getValue();
        RecommendationRequest request = (RecommendationRequest) capturedEntity.getBody();

        assertNotNull(request);
        assertNotNull(request.getAvailablePlans());
        assertEquals(2, request.getAvailablePlans().size());
        assertEquals("PLAN-BASIC-01", request.getAvailablePlans().get(0).getPlanCode());
        assertEquals("PLAN-GOLD-02", request.getAvailablePlans().get(1).getPlanCode());
    }

    @Test
    @DisplayName("Should handle different member ages correctly")
    void getRecommendationByMemberId_ShouldHandleDifferentAges() {
        Member youngMember = Member.builder()
                .id(new MemberId(TEST_MEMBER_ID))
                .name("Young Member")
                .email(new Email("young@example.com"))
                .age(new Age(25))
                .phone(new Phone("555-0102"))
                .build();

        RecommendationResponse expectedResponse = createExpectedResponse();

        ArgumentCaptor<HttpEntity<?>> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        when(memberRepository.findById(any(MemberId.class))).thenReturn(Optional.of(youngMember));
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                entityCaptor.capture(),
                eq(RecommendationResponse.class)
        )).thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        recommendationService.getRecommendationByMemberId(TEST_MEMBER_ID);

        HttpEntity<?> capturedEntity = entityCaptor.getValue();
        RecommendationRequest request = (RecommendationRequest) capturedEntity.getBody();

        assertEquals(25, request.getMemberProfile().getAge());
    }

    @Test
    @DisplayName("Should return null when IA service returns null body")
    void getRecommendationByMemberId_ShouldReturnNull_WhenIAReturnsNullBody() {
        Member member = createTestMember();

        when(memberRepository.findById(any(MemberId.class))).thenReturn(Optional.of(member));
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(RecommendationResponse.class)
        )).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        RecommendationResponse actualResponse = recommendationService.getRecommendationByMemberId(TEST_MEMBER_ID);

        assertNull(actualResponse);

        verify(memberRepository, times(1)).findById(any(MemberId.class));
    }

    private Member createTestMember() {
        return Member.builder()
                .id(new MemberId(TEST_MEMBER_ID))
                .name("Test Member")
                .email(new Email("test@example.com"))
                .age(new Age(30))
                .phone(new Phone("555-0101"))
                .build();
    }

    private RecommendationResponse createExpectedResponse() {
        RecommendationResponse response = new RecommendationResponse();
        response.setRecommendedPlanCode("PLAN-GOLD-02");
        response.setReasoning("Recommended plan based on member profile");
        response.setConfidenceScore(0.95);
        response.setAlternativePlanCodes(Arrays.asList("PLAN-BASIC-01"));
        return response;
    }
}
