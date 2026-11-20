package com.eldercare.modules.ai.risk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResidentChartContextDto {
    private Long residentId;
    private String residentName;
    private String roomNumber;
    private Integer age;
    private String gender;
    private String careLevel;
    private Integer adlScore;
    private Integer cognitiveScore;
    private List<ChartMedicationDto> activeMedications;
    private List<ChartIncidentDto> recentIncidents;
    private List<String> diagnoses;
    private List<String> documentedBehaviors;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChartMedicationDto {
        private Long id;
        private String medicationName;
        private String dosage;
        private String route;
        private String frequency;
        private String status;
        private boolean isFrid;
        private String fridCategory;
        private String clinicalWarning;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChartIncidentDto {
        private Long id;
        private String incidentType;
        private String severity;
        private String occurredAt;
        private String description;
        private String location;
    }
}
