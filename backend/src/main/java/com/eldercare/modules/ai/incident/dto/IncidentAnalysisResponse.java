package com.eldercare.modules.ai.incident.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncidentAnalysisResponse {
    private String suggestedSeverity;       // "LOW", "MEDIUM", "HIGH", "CRITICAL", "EMERGENCY"
    private String suggestedIncidentType;   // "FALL", "MEDICATION_ERROR", "ELOPEMENT", "BEHAVIORAL", "MEDICAL_EMERGENCY", "INJURY", "OTHER"
    private String rationale;               // Lý do phân loại
    private List<String> recommendedActions;// Các hành động y tế / hành chính khuyến nghị
    private Double confidence;              // Độ tin cậy (0.0 - 1.0)
}
