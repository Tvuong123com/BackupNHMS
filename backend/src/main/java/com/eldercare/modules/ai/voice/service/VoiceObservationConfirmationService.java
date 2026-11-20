package com.eldercare.modules.ai.voice.service;

import com.eldercare.modules.ai.voice.dto.VoiceCareParseResponse;
import com.eldercare.modules.ai.voice.entity.AiVoiceObservationEntity;
import com.eldercare.modules.ai.voice.repository.AiVoiceObservationRepository;
import com.eldercare.modules.admin.user_management.UserRepository;
import com.eldercare.modules.clinical.vitals_sign.VitalSignEntity;
import com.eldercare.modules.clinical.vitals_sign.VitalSignRepository;
import com.eldercare.modules.careplan_management.cna_daily_tasks.enums.TaskStatus;
import com.eldercare.modules.careplan_management.cna_daily_tasks.repository.CareTaskRepository;
import com.eldercare.modules.resident_intake.resident.repository.ResidentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.Set;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class VoiceObservationConfirmationService {
    private static final Set<String> VOICE_COMPLETABLE_TASK_TYPES = Set.of(
            "BATHING", "DRESSING", "GROOMING", "TOILETING", "FEEDING",
            "MEAL_ASSISTANCE", "HYDRATION", "MOBILITY", "TRANSFER",
            "REPOSITIONING", "ACTIVITY");
    private final AiVoiceObservationRepository observations;
    private final ResidentRepository residents;
    private final CareTaskRepository tasks;
    private final UserRepository users;
    private final VitalSignRepository vitalSigns;
    private final ObjectMapper objectMapper;

    public record Confirmation(Long residentId, Long taskId, String transcript, VoiceCareParseResponse parsed) {}
    public record ConfirmationResult(Long observationId, Long completedTaskId, Long vitalSignId) {}

    @Transactional
    public ConfirmationResult confirm(Confirmation request, String staffEmail) {
        if (request.residentId() == null) throw new IllegalArgumentException("Resident not found");
        var resident = residents.findByIdAndIsDeletedFalse(request.residentId())
                .orElseThrow(() -> new IllegalArgumentException("Resident not found"));
        if (resident.isChartLocked()) throw new IllegalStateException("Resident chart is locked");
        if (request.transcript() == null || request.transcript().isBlank() || request.parsed() == null) {
            throw new IllegalArgumentException("Reviewed transcript and extracted observations are required");
        }
        if (request.parsed().getDietaryIntakePercentage() != null
                && (request.parsed().getDietaryIntakePercentage() < 0
                    || request.parsed().getDietaryIntakePercentage() > 100)) {
            throw new IllegalArgumentException("Dietary intake must be 0–100 percent");
        }
        var parsed = request.parsed();
        boolean hasVitals = parsed.getBloodPressureSystolic() != null
                || parsed.getBloodPressureDiastolic() != null || parsed.getHeartRateBpm() != null
                || parsed.getBodyTemperatureCelsius() != null;
        Long recordedBy = null;
        if (hasVitals) {
            validateRange(parsed.getBloodPressureSystolic(), 1, 300, "Systolic blood pressure");
            validateRange(parsed.getBloodPressureDiastolic(), 1, 250, "Diastolic blood pressure");
            validateRange(parsed.getHeartRateBpm(), 1, 300, "Heart rate");
            if (parsed.getBodyTemperatureCelsius() != null
                    && (!Double.isFinite(parsed.getBodyTemperatureCelsius())
                        || parsed.getBodyTemperatureCelsius() < 25
                        || parsed.getBodyTemperatureCelsius() > 45)) {
                throw new IllegalArgumentException("Temperature needs manual chart review");
            }
            recordedBy = users.findByEmailAndIsDeletedFalse(staffEmail)
                    .orElseThrow(() -> new IllegalArgumentException("Staff account not found"))
                    .getId();
        }
        if (request.taskId() != null) {
            var task = tasks.findById(request.taskId())
                    .orElseThrow(() -> new IllegalArgumentException("Task not found"));
            Long taskResidentId = task.getCareIntervention().getCareGoal().getCarePlan().getResident().getId();
            if (!request.residentId().equals(taskResidentId)) {
                throw new IllegalArgumentException("Task belongs to another resident");
            }
            if (task.getStatus() != TaskStatus.PENDING) {
                throw new IllegalStateException("Only pending tasks may be completed");
            }
            String taskType = task.getTaskType() == null ? "" : task.getTaskType()
                    .trim().toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]+", "_");
            if (!VOICE_COMPLETABLE_TASK_TYPES.contains(taskType)) {
                throw new IllegalArgumentException("This task type needs its dedicated confirmation workflow");
            }
            task.setStatus(TaskStatus.COMPLETED);
            task.setCompletedAt(OffsetDateTime.now());
            tasks.save(task);
        }
        AiVoiceObservationEntity row = new AiVoiceObservationEntity();
        row.setResidentId(request.residentId());
        row.setTaskId(request.taskId());
        row.setConfirmedBy(staffEmail);
        row.setTranscript(request.transcript());
        try { row.setParsedJson(objectMapper.writeValueAsString(request.parsed())); }
        catch (Exception e) { throw new IllegalArgumentException("Could not serialize observation", e); }
        row = observations.save(row);
        Long vitalSignId = null;
        if (hasVitals) {
            VitalSignEntity vital = new VitalSignEntity();
            vital.setResidentId(request.residentId());
            vital.setRecordedBy(recordedBy);
            vital.setBloodPressureSystolic(toShort(parsed.getBloodPressureSystolic()));
            vital.setBloodPressureDiastolic(toShort(parsed.getBloodPressureDiastolic()));
            vital.setHeartRateBpm(toShort(parsed.getHeartRateBpm()));
            if (parsed.getBodyTemperatureCelsius() != null) {
                vital.setTemperatureFahrenheit(BigDecimal.valueOf(parsed.getBodyTemperatureCelsius())
                        .multiply(BigDecimal.valueOf(1.8)).add(BigDecimal.valueOf(32))
                        .setScale(1, RoundingMode.HALF_UP));
            }
            vital.setNotes("Confirmed voice observation #" + row.getId());
            vitalSignId = vitalSigns.save(vital).getId();
        }
        return new ConfirmationResult(row.getId(), request.taskId(), vitalSignId);
    }

    private static void validateRange(Integer value, int min, int max, String field) {
        if (value != null && (value < min || value > max)) {
            throw new IllegalArgumentException(field + " needs manual chart review");
        }
    }

    private static Short toShort(Integer value) {
        return value == null ? null : value.shortValue();
    }
}
