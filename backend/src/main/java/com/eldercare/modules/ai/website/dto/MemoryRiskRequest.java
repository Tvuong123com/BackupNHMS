package com.eldercare.modules.ai.website.dto;

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
public class MemoryRiskRequest {
    @Size(max = 20)
    private List<@Size(max = 100) String> observedSigns;
    @Size(max = 100)
    private String duration;
    @Size(max = 1000)
    private String additionalObservations;
}
