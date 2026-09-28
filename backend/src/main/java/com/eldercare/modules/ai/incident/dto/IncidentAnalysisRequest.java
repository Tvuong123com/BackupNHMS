package com.eldercare.modules.ai.incident.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncidentAnalysisRequest {
    private String description;
    private String residentInfo;
    private String location;
}
