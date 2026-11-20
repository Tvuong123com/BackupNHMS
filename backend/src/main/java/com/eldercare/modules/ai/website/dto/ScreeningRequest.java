package com.eldercare.modules.ai.website.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class ScreeningRequest {
    @Min(1)
    @Max(120)
    private Integer age;
    @Size(max = 50)
    private String gender;
    @Size(max = 80)
    private String mobilityStatus;      // "INDEPENDENT", "CANE_WALKER", "WHEELCHAIR", "BEDBOUND"
    @Size(max = 20)
    private List<@Size(max = 80) String> medicalConditions; // "ALZHEIMERS", "DIABETES", "HEART_DISEASE", "STROKE", etc.
    @Size(max = 20)
    private List<@Size(max = 80) String> dailyNeedsHelp;   // "BATHING", "DRESSING", "EATING", "TOILETING", "MEDICATION"
    @Size(max = 1000)
    private String additionalNotes;
}
