package com.eldercare.modules.ai.risk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyFallInterventionResponse {

    private boolean success;
    private String message;
    private Long carePlanId;
    private Long careGoalId;
    private Long careInterventionId;
    private Long careTaskId;
    private String assignedRole;
    private String action;
    private String taskStatus;
    private OffsetDateTime scheduledTime;
}
