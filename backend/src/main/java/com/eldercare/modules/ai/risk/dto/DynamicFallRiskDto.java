package com.eldercare.modules.ai.risk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicFallRiskDto {
    private Long residentId;
    private String residentName;
    private String roomNumber;
    private int dfrsScore; // 0 - 100
    private String riskLevel; // "HIGH" | "MODERATE" | "LOW"
    private int incidentSubscore; // Max 35
    private int medicationSubscore; // Max 30
    private int adlSubscore; // Max 25
    private int vitalSubscore; // Max 10
    private int recentFallsCount;
    private int activeFridCount;
    private List<String> contributingFactors;
    private List<FridMatchDto> activeFridMedications;
    private List<FallInterventionDto> recommendedInterventions;
    private String clinicalDisclaimer;
    private OffsetDateTime assessedAt;
    private String methodVersion;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FridMatchDto {
        private String medicationName;
        private String category;
        private int riskWeight;
        private String clinicalWarning;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FallInterventionDto {
        private String role; // CNA | NURSE | DOCTOR | PT
        private String priority; // IMMEDIATE | HIGH | ROUTINE
        private String action;
        private String rationale;
    }
}
