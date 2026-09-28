package com.eldercare.modules.ai.elopement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElopementRiskResponse {
    private String riskLevel; // "LOW", "MODERATE", "HIGH", "CRITICAL"
    private Double riskScore; // 0.0 - 10.0
    private Boolean triggerDoorLockAlert;
    private String behavioralAnalysis;
    private List<String> immediateSafeguards;
}
