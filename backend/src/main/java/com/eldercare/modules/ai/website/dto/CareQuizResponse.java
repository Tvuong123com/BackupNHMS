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
public class CareQuizResponse {
    private String recommendedService;
    private String serviceUrl;
    private String badge;
    private String headline;
    private String summary;
    private List<String> keyBenefits;
    private List<String> questionsForTeam;
    private String recommendedAction;
    private boolean success;
    private boolean fallback;
}
