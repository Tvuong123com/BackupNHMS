package com.eldercare.modules.ai.medication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicationCheckResponse {
    private String riskLevel; // "SAFE", "MILD", "MODERATE", "SEVERE", "CONTRAINDICATED"
    private Boolean isSafeToAdminister;
    private String summaryWarning;
    private List<InteractionDetailDTO> interactions;
    private List<String> clinicalPrecautions;

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
}
