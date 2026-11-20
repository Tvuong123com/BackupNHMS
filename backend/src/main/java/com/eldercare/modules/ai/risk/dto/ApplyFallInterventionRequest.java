package com.eldercare.modules.ai.risk.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyFallInterventionRequest {

    @NotNull(message = "Resident ID is required")
    private Long residentId;

    @NotBlank(message = "Action description is required")
    private String action;

    private String role; // CNA, NURSE, PT, DOCTOR

    private String priority; // IMMEDIATE, HIGH, MEDIUM

    private String rationale;

    private Long assignedCnaId;

    private OffsetDateTime scheduledTime;
}
