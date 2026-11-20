package com.eldercare.modules.ai.website.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebsiteChatRequest {
    @NotBlank
    @Size(max = 2000)
    private String message;
    @Size(max = 120)
    private String currentPage; // e.g. "/services/memory-care", "/admissions"
}
