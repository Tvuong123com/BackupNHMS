package com.eldercare.modules.ai.website.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaqSearchResponse {
    private String answer;
    private boolean success;
    private String matchedFaqQuestion;
    private boolean fallback;
}
