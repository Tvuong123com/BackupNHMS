package com.eldercare.modules.careplan_management.careplan_design.service;

import com.eldercare.common.enums.CarePlanGoalStatusEnum;
import com.eldercare.modules.admin.user_management.UserRepository;
import com.eldercare.modules.careplan_management.careplan_design.dto.createCarePlanDTO.*;
import com.eldercare.modules.careplan_management.careplan_design.repository.database_schema.*;
import com.eldercare.modules.careplan_management.careplan_design.repository.jpa.JpaCarePlanRepository;
import com.eldercare.modules.resident_intake.resident.repository.ResidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CarePlanCreationService {
    private final JpaCarePlanRepository plans;
    private final ResidentRepository residents;
    private final UserRepository users;
    public record ResidentOption(Long id, String name) {}
    @Transactional(readOnly = true)
    public List<ResidentOption> residents() {
        return residents.findByStatusIgnoreCaseAndIsDeletedFalse("ACTIVE").stream().filter(r -> !r.isChartLocked())
                .map(r -> new ResidentOption(r.getId(), r.getFirstName() + " " + r.getLastName())).toList();
    }
    @Transactional
    public CreateCarePlanResponseDTO create(CreateCarePlanRequestDTO request, String purpose, String email) {
        if (request == null || request.residentId <= 0 || request.listCareGoal == null || request.listCareGoal.isEmpty() || request.listCareGoal.size() > 10)
            throw bad("Choose a resident and provide 1–10 reviewed care goals");
        if (!Set.of("DRAFT", "NEED_REVIEW").contains(purpose == null ? "" : purpose)) throw bad("Save as DRAFT or submit for review; new plans cannot be activated automatically");
        var resident = residents.findByIdAndIsDeletedFalse((long) request.residentId).orElseThrow(() -> bad("Resident not found"));
        if (resident.isChartLocked() || !"ACTIVE".equalsIgnoreCase(resident.getStatus())) throw bad("Choose an active resident with an unlocked chart");
        var author = users.findByEmailAndIsDeletedFalse(email).filter(u -> "ACTIVE".equalsIgnoreCase(u.getStatus())).orElseThrow(() -> bad("Active staff account not found"));
        CarePlanSchema plan = new CarePlanSchema();
        plan.setResident(resident); plan.setCreatedBy(author); plan.setStatus("NEED_REVIEW".equals(purpose) ? "PENDING_REVIEW" : "DRAFT");
        plan.setCreatedAt(OffsetDateTime.now()); plan.setUpdatedAt(OffsetDateTime.now());
        for (var input : request.listCareGoal) {
            if (input == null || !text(input.name, 255) || !text(input.description, 10000) || input.listCareIntervention == null
                    || input.listCareIntervention.isEmpty() || input.listCareIntervention.size() > 20) throw bad("Each goal needs a title, description and 1–20 interventions");
            var goal = new CareGoalSchema(); goal.setCarePlan(plan); goal.setTitle(input.name.trim()); goal.setDescription(input.description.trim());
            goal.setStatus(CarePlanGoalStatusEnum.IN_PROGRESS);
            for (var instruction : input.listCareIntervention) {
                if (instruction == null || !text(instruction.name, 2000) || instruction.assingedRole == null) throw bad("Review each intervention and its assigned role");
                String role = instruction.assingedRole.trim().toUpperCase(Locale.ROOT);
                if (!Set.of("CNA", "NURSE", "DOCTOR", "PT", "OT").contains(role)) throw bad("Unsupported intervention role");
                var intervention = new CareInterventionSchema(); intervention.setCareGoal(goal); intervention.setTitle(instruction.name.trim()); intervention.setAssignedRole(role);
                goal.getListCareIntervention().add(intervention);
            }
            plan.getListCareGoal().add(goal);
        }
        var saved = plans.saveAndFlush(plan);
        var result = new CreateCarePlanResponseDTO(); result.id = Math.toIntExact(saved.getId()); return result;
    }
    private boolean text(String value, int max) { return value != null && !value.isBlank() && value.length() <= max; }
    private ResponseStatusException bad(String message) { return new ResponseStatusException(HttpStatus.BAD_REQUEST, message); }
}
