package com.eldercare.modules.ai.summary.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummaryResponse {
    private String title;
    private String narrativeSummary;
    private List<String> keyHighlights;
    private List<String> handoverActionItems;
    private String generatedAt;
}
