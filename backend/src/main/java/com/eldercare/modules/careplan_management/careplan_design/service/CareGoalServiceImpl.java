package com.eldercare.modules.careplan_management.careplan_design.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.eldercare.modules.careplan_management.careplan_design.dto.addCareGoalDTO.AddCareGoalRequestDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.addCareGoalDTO.AddCareGoalResponseDTO;
import com.eldercare.modules.careplan_management.careplan_design.entity.CareGoalEntity;
import com.eldercare.modules.careplan_management.careplan_design.entity.CareInterventionEntity;
import com.eldercare.modules.careplan_management.careplan_design.entity.CarePlanEntity;

import jakarta.transaction.Transactional;

@Service
public class CareGoalServiceImpl implements ICareGoalService {

    private final ICarePlanRepository carePlanRepositoy;
    private final ICareGoalRepository careGoalRepository;

    public CareGoalServiceImpl(ICarePlanRepository carePlanRepositoy, ICareGoalRepository careGoalRepository) {
        this.carePlanRepositoy = carePlanRepositoy;
        this.careGoalRepository = careGoalRepository;
    }

    @Override
    @Transactional()
    public AddCareGoalResponseDTO addCareGoal(AddCareGoalRequestDTO requestDTO, int carePlanId) {
        // find care plan
        CarePlanEntity carePlanEntity = this.carePlanRepositoy.findById(carePlanId);

        CareGoalEntity careGoalEntity = new CareGoalEntity();
        // careGoalEntity.setListCareIntervention();
        careGoalEntity.setDescription(requestDTO.careGoalDescription);
        careGoalEntity.setName(requestDTO.careGoalName);
        careGoalEntity.initState();// set it to in progress
        if (requestDTO.interventions != null) {
            for (var intervention : requestDTO.interventions) {
                if (intervention.title == null || intervention.title.isBlank()) continue;
                careGoalEntity.addIntervention(new CareInterventionEntity(
                        0, intervention.title,
                        intervention.assignedRole == null || intervention.assignedRole.isBlank()
                                ? "NURSE" : intervention.assignedRole));
            }
        }

        CareGoalEntity careGoalEntityAfterInserted = this.careGoalRepository.saveOne(careGoalEntity,
                carePlanEntity);

        AddCareGoalResponseDTO resposneDto = new AddCareGoalResponseDTO(carePlanEntity.getId(),
                careGoalEntityAfterInserted.getId());
        return resposneDto;
    }

    @Override
    @Transactional
    public List<AddCareGoalResponseDTO> addCareGoals(List<AddCareGoalRequestDTO> requests, int carePlanId) {
        if (requests == null || requests.isEmpty() || requests.size() > 10
                || requests.stream().anyMatch(r -> r == null || r.careGoalName == null
                        || r.careGoalName.isBlank() || r.careGoalDescription == null)) {
            throw new IllegalArgumentException("Provide 1–10 complete care goal drafts");
        }
        return requests.stream().map(request -> addCareGoal(request, carePlanId)).toList();
    }

}
