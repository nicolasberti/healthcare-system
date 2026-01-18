package com.healthcare.notification_service.model;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class RecommendationRequest {

    private MemberProfile memberProfile;
    private List<AvailablePlan> availablePlans;

    public RecommendationRequest() {}

    /* =========================
       INTERNAL CLASSES
       ========================= */

    @Getter
    @Setter
    @ToString
    public static class MemberProfile {
        private int age;
        private MedicalHistory medicalHistory;
        private Preferences preferences;

        public MemberProfile() {}
    }

    @Getter
    @Setter
    @ToString
    public static class MedicalHistory {
        private boolean hasChronicConditions;
        private List<String> chronicConditions;
        private boolean smoker;
        private boolean preExistingConditions;

        public MedicalHistory() {}
    }

    @Getter
    @Setter
    @ToString
    public static class Preferences {
        private String riskTolerance;   // LOW, MEDIUM, HIGH
        private String budgetLevel;     // LOW, MEDIUM, HIGH
        private List<String> preferredCoverages;

        public Preferences() {}
    }

    @Getter
    @Setter
    @ToString
    public static class AvailablePlan {
        private String planCode;
        private String name;
        private String description;
        private String targetAudience;
        private List<String> coverages;

        public AvailablePlan() {}
    }

}

