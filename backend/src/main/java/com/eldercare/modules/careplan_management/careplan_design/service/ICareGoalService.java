package com.eldercare.modules.careplan_management.careplan_design.service;

import com.eldercare.modules.careplan_management.careplan_design.dto.addCareGoalDTO.AddCareGoalRequestDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.addCareGoalDTO.AddCareGoalResponseDTO;
import java.util.List;

public interface ICareGoalService {
    AddCareGoalResponseDTO addCareGoal(AddCareGoalRequestDTO requestDTO, int carePlanId);
    List<AddCareGoalResponseDTO> addCareGoals(List<AddCareGoalRequestDTO> requests, int carePlanId);
}
