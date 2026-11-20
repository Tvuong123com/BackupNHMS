package com.eldercare.modules.ai.website.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaqSearchRequest {
    @NotBlank
    @Size(max = 1000)
    private String question;
    @Size(max = 500)
    private String context;
    @Size(max = 30)
    private List<@Valid FaqItem> availableFaqs;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FaqItem {
        @NotBlank
        @Size(max = 500)
        private String q;
        @NotBlank
        @Size(max = 2000)
        private String a;
    }
}
