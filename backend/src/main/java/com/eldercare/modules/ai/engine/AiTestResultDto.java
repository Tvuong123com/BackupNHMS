package com.eldercare.modules.ai.engine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiTestResultDto {
    private boolean success;
    private long latencyMs;
    private String provider;
    private String model;
    private String message;
    private java.util.List<String> availableModels;
}
