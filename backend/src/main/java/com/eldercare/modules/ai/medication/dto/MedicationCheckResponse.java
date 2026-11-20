package com.eldercare.modules.ai.medication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicationCheckResponse {
    private String riskLevel; // SAFE, CAUTION_MONITOR, REVIEW_REQUIRED, HIGH_RISK_PIM, CONTRAINDICATED
    private Boolean isSafeToAdminister;
    private String summaryWarning;
    private List<InteractionDetailDTO> interactions;
    private List<String> clinicalPrecautions;
    private List<String> chartMedications;
    private List<String> manuallyReportedMedications;
    private List<String> chartAllergies;
    private List<String> chartConditions;
    private List<String> recordChecks;
    private OffsetDateTime generatedAt;
    private String methodVersion;
    private String interactionReferenceStatus;
    private List<Long> sourceOrderIds;
    private List<Long> sourceAllergyRecordIds;
    private List<Long> sourceDiagnosisRecordIds;
    private List<String> dataGaps;

    // --- Geriatric CDSS Additions (AGS Beers Criteria® 2023, STOPP/START v3, ACB Score) ---
    private Integer totalAcbScore;
    private String acbRiskTier; // MINIMAL, MODERATE, HIGH
    private String acbClinicalImpact;
    private List<AcbScoreDetailDTO> acbContributingMedications;
    private List<BeersAlertDTO> beersCriteriaAlerts;
    private List<StoppAlertDTO> stoppAlerts;
    private List<DeprescribingPlanDTO> deprescribingGuidance;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InteractionDetailDTO {
        private String interactingSubstance;
        private String severity;
        private String mechanism;
        private String recommendation;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AcbScoreDetailDTO {
        private String medicationName;
        private Integer score;
        private String therapeuticClass;
        private String alternativeOption;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BeersAlertDTO {
        private String medicationName;
        private String category;
        private String severity; // HIGH, MODERATE, CAUTION
        private String rationale;
        private String recommendation;
        private String saferAlternative;
        private String evidenceLevel;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StoppAlertDTO {
        private String ruleCode;
        private String primaryMedication;
        private String secondaryMedication;
        private String interactionType;
        private String severity;
        private String clinicalExplanation;
        private String actionRecommendation;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeprescribingPlanDTO {
        private String targetMedication;
        private String indicationReview;
        private String proposedAction; // TAPER_DISCONTINUE, SWITCH_SAFER_AGENT, DOSE_REDUCTION
        private String alternativeMedication;
        private String clinicalMonitoringNotes;
    }
}
