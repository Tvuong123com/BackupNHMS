package com.eldercare.modules.ai.website.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemoryRiskResponse {
    private String estimatedCategory;
    private String clinicalPerspective;
    private List<String> homeStrategies;
    private String facilitySupportPlan;
    private String recommendedNextStep;
    private boolean success;
    private boolean fallback;
}
