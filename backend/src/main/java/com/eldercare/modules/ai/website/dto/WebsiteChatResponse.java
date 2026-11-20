package com.eldercare.modules.ai.website.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebsiteChatResponse {
    private String reply;
    private boolean success;
    private boolean fallback;
}
