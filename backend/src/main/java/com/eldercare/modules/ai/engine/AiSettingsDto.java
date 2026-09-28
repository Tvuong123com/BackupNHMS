package com.eldercare.modules.ai.engine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiSettingsDto {
    private String provider; // "GOOGLE_GEMINI" or "LOCAL_OLLAMA"
    private String geminiApiKey;
    private String geminiModel; // e.g. "gemini-2.0-flash", "gemini-1.5-flash"
    private String ollamaBaseUrl; // e.g. "http://localhost:11434"
    private String ollamaModel; // e.g. "qwen3.5:2b-q4_K_M"
    private Double temperature;
    private Integer maxTokens;
    private String systemRules;
    private Map<String, Boolean> features; // feature toggles
}
