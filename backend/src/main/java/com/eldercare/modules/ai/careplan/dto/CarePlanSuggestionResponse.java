package com.eldercare.modules.ai.careplan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarePlanSuggestionResponse {
    private String residentSummary;
    private List<SuggestedGoalDTO> suggestedGoals;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SuggestedGoalDTO {
        private String goalName;
        private String description;
        private String priority; // HIGH, MEDIUM, LOW
        private String rationale;
        private List<SuggestedInterventionDTO> interventions;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SuggestedInterventionDTO {
        private String name;
        private String assignedRole; // CNA, NURSE, PT, OT, DOCTOR
    }
}
