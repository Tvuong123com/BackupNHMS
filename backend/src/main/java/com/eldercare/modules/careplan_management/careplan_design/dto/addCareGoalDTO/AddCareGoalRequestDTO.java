package com.eldercare.modules.careplan_management.careplan_design.dto.addCareGoalDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddCareGoalRequestDTO {
    public String careGoalName;
    public String careGoalDescription;
    public List<InterventionDraft> interventions;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InterventionDraft {
        public String title;
        public String assignedRole;
    }
}
