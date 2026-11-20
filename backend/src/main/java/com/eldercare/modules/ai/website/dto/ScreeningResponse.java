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
public class ScreeningResponse {
    private String recommendedService;
    private String suggestedCareLevel;
    private List<String> additionalServices;
    private String estimatedTimeline;
    private String rationale;
    private String nextStep;
    private boolean success;
    private boolean fallback;
}
