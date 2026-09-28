package com.eldercare.modules.ai.medication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicationCheckRequest {
    private String residentName;
    private List<String> currentMedications;
    private String newMedication;
    private List<String> allergies;
    private List<String> medicalConditions;
}
