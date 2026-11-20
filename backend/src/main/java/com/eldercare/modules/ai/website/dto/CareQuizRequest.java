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
public class CareQuizRequest {
    @Size(max = 100)
    private String recipient;
    @Size(max = 100)
    private String primaryNeed;
    @Size(max = 100)
    private String careUrgency;
    @Size(max = 100)
    private String mobility;
    @Size(max = 20)
    private List<@Size(max = 100) String> symptomsOrGoals;
}
