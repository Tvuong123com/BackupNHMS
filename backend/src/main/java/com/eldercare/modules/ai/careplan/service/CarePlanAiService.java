package com.eldercare.modules.ai.careplan.service;

import com.eldercare.modules.ai.careplan.dto.CarePlanSuggestionRequest;
import com.eldercare.modules.ai.careplan.dto.CarePlanSuggestionResponse;
import com.eldercare.modules.ai.engine.AiExecutionService;
import com.eldercare.modules.resident_intake.resident.repository.ResidentRepository;
import com.eldercare.modules.resident_intake.assessment.repository.AssessmentRepository;
import com.eldercare.modules.resident_intake.resident.repository.ClinicalRecordRepository;
import com.eldercare.modules.risk_incident.incident_tracking.repository.IncidentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDate;
import java.time.Period;

@Service
@Slf4j
@RequiredArgsConstructor
public class CarePlanAiService {

    private final AiExecutionService aiExecutionService;
    private final ObjectMapper objectMapper;
    private final ResidentRepository residentRepository;
    private final AssessmentRepository assessmentRepository;
    private final IncidentRepository incidentRepository;
    private final ClinicalRecordRepository clinicalRecordRepository;

    private static final String CARE_PLAN_SYSTEM_PROMPT = """
            You are a Clinical Nurse Specialist & Care Plan Coordinator in an elderly nursing care facility.
            Based on the resident's assessment data, ADL score, medical diagnoses, and recent incident history, generate personalized, evidence-based Care Goals and corresponding Interventions in English.
            Rely strictly on provided facts; do not invent unverified diagnoses or medication dosages.
            The output is a draft for clinical staff to review, customize, and approve.
            Suggest 3 to 6 comprehensive goals, assigning roles strictly from: CNA, NURSE, PT, OT, DOCTOR.
            
            Return ONLY a valid JSON object matching this schema (no outside conversational prose):
            {
              "residentSummary": "Brief clinical synthesis of the resident's physical, cognitive, and risk status",
              "suggestedGoals": [
                {
                  "goalName": "Concise Goal Title (e.g. Fall Risk Reduction & Bed Safety Protocol)",
                  "description": "Specific measurable clinical objective to be achieved",
                  "priority": "HIGH | MEDIUM | LOW",
                  "rationale": "Clinical rationale based on resident chart data and risk indicators",
                  "interventions": [
                    {
                      "name": "Specific actionable nursing intervention",
                      "assignedRole": "CNA | NURSE | PT | OT | DOCTOR"
                    }
                  ]
                }
              ]
            }
            """;

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public CarePlanSuggestionResponse suggestCarePlan(CarePlanSuggestionRequest request) {
        if (request.getResidentId() == null) {
            throw new IllegalArgumentException("A resident ID is required for clinical suggestions");
        }
        var resident = residentRepository.findByIdAndIsDeletedFalse(request.getResidentId())
                .orElseThrow(() -> new IllegalArgumentException("Resident not found"));
        try {
            request.setResidentName(resident.getFirstName() + " " + resident.getLastName());
            request.setGender(resident.getGender());
            request.setAge(resident.getDateOfBirth() == null ? null
                    : Period.between(resident.getDateOfBirth(), LocalDate.now()).getYears());
            assessmentRepository.findByResidentIdAndIsCurrentTrue(request.getResidentId())
                    .ifPresent(a -> {
                        request.setAdlScore(a.getAdlTotalScore());
                        if (a.getConfirmedCareLevel() != null) {
                            request.setCareLevel(a.getConfirmedCareLevel().getLevelName());
                        }
                    });
            request.setRecentIncidents(incidentRepository.findTop5ByResidentIdOrderByOccurredAtDesc(request.getResidentId())
                    .stream().map(i -> i.getIncidentType() + ": " + i.getDescription()).toList());
            request.setDiagnoses(clinicalRecordRepository
                    .findByResidentIdAndRecordTypeAndIsDeletedFalse(request.getResidentId(), "DIAGNOSIS")
                    .stream().map(d -> d.getDescription()).toList());
            StringBuilder prompt = new StringBuilder();
            prompt.append("Hồ sơ cư dân: ").append(request.getResidentName() != null ? request.getResidentName() : "Cư dân")
                    .append(", ").append(request.getAge() != null ? request.getAge() : "chưa rõ tuổi").append(" tuổi")
                    .append(", Giới tính: ").append(request.getGender() != null ? request.getGender() : "Chưa rõ").append("\n");

            if (request.getCareLevel() != null) {
                prompt.append("Cấp độ chăm sóc (Care Level): ").append(request.getCareLevel()).append("\n");
            }
            if (request.getAdlScore() != null) {
                prompt.append("Điểm ADL được ghi nhận trong hồ sơ: ").append(request.getAdlScore()).append("\n");
            }
            if (request.getDiagnoses() != null && !request.getDiagnoses().isEmpty()) {
                prompt.append("Chẩn đoán bệnh lý nền: ").append(String.join(", ", request.getDiagnoses())).append("\n");
            }
            if (request.getRecentIncidents() != null && !request.getRecentIncidents().isEmpty()) {
                prompt.append("Lịch sử sự cố gần đây: ").append(String.join("; ", request.getRecentIncidents())).append("\n");
            }
            if (request.getExistingGoals() != null && !request.getExistingGoals().isEmpty()) {
                prompt.append("Các mục tiêu đã có: ").append(String.join(", ", request.getExistingGoals())).append("\n");
            }

            String raw = aiExecutionService.execute(CARE_PLAN_SYSTEM_PROMPT, prompt.toString());

            return parseCarePlanResponse(raw, request);
        } catch (Exception e) {
            log.warn("Không tạo được gợi ý kế hoạch chăm sóc AI: {}", e.getClass().getSimpleName());
            return createFallbackCarePlan(request);
        }
    }

    private CarePlanSuggestionResponse parseCarePlanResponse(String raw, CarePlanSuggestionRequest request) {
        try {
            String json = raw.trim();
            if (json.contains("{") && json.contains("}")) {
                json = json.substring(json.indexOf('{'), json.lastIndexOf('}') + 1).trim();
            }

            CarePlanSuggestionResponse parsed = objectMapper.readValue(json, CarePlanSuggestionResponse.class);
            if (parsed == null || parsed.getSuggestedGoals() == null
                    || parsed.getSuggestedGoals().stream().anyMatch(goal -> goal == null
                            || goal.getGoalName() == null || goal.getGoalName().isBlank()
                            || goal.getDescription() == null || goal.getDescription().isBlank()
                            || goal.getGoalName().length() > 255 || goal.getDescription().length() > 10000
                            || goal.getInterventions() == null || goal.getInterventions().isEmpty()
                            || goal.getInterventions().stream().anyMatch(item -> item == null || item.getName() == null
                                || item.getName().isBlank() || item.getName().length() > 2000 || item.getAssignedRole() == null
                                || !java.util.Set.of("CNA", "NURSE", "PT", "OT", "DOCTOR").contains(item.getAssignedRole())))) {
                return createFallbackCarePlan(request);
            }
            if (parsed.getSuggestedGoals().size() > 10) parsed.setSuggestedGoals(parsed.getSuggestedGoals().subList(0, 10));
            parsed.setGenerationMethod("AI");
            return parsed;
        } catch (Exception e) {
            log.warn("Không thể parse JSON Care Plan từ AI: {}", e.getMessage());
            return createFallbackCarePlan(request);
        }
    }

    private CarePlanSuggestionResponse createFallbackCarePlan(CarePlanSuggestionRequest request) {
        String name = request.getResidentName() != null ? request.getResidentName() : "Resident";
        List<CarePlanSuggestionResponse.SuggestedGoalDTO> fallbackGoals = new java.util.ArrayList<>();

        // Goal 1: Fall Safety
        fallbackGoals.add(CarePlanSuggestionResponse.SuggestedGoalDTO.builder()
                .goalName("Fall Risk Reduction & Bedside Safety Protocol")
                .description("Maintain resident safety in bedroom and bathroom areas with zero unassisted fall occurrences.")
                .priority("HIGH")
                .rationale("High-priority safety protocol based on geriatric fall prevention guidelines (CDC STEADI).")
                .interventions(List.of(
                        CarePlanSuggestionResponse.SuggestedInterventionDTO.builder()
                                .name("Ensure bed is locked in lowest position with bedside floor safety mat placed.")
                                .assignedRole("CNA").build(),
                        CarePlanSuggestionResponse.SuggestedInterventionDTO.builder()
                                .name("Activate bed sensor alarm during sleep hours and test nurse call bell proximity.")
                                .assignedRole("NURSE").build(),
                        CarePlanSuggestionResponse.SuggestedInterventionDTO.builder()
                                .name("Physical therapy gait and balance assessment with assistive walker verification.")
                                .assignedRole("PT").build()
                ))
                .build());

        // Goal 2: ADL Independence & Toileting
        fallbackGoals.add(CarePlanSuggestionResponse.SuggestedGoalDTO.builder()
                .goalName("ADL Functional Independence & Scheduled Toileting")
                .description("Promote functional mobility and personal hygiene independence while mitigating incontinence risks.")
                .priority("MEDIUM")
                .rationale("Regular toileting prevents hurried unassisted transfers and promotes resident dignity.")
                .interventions(List.of(
                        CarePlanSuggestionResponse.SuggestedInterventionDTO.builder()
                                .name("Implement scheduled 2-hour daytime toileting and prompt assistance before bed.")
                                .assignedRole("CNA").build(),
                        CarePlanSuggestionResponse.SuggestedInterventionDTO.builder()
                                .name("Assist with morning/evening hygiene routines using adaptive personal care equipment.")
                                .assignedRole("CNA").build(),
                        CarePlanSuggestionResponse.SuggestedInterventionDTO.builder()
                                .name("Occupational therapy evaluation for adaptive dining and dressing aids.")
                                .assignedRole("OT").build()
                ))
                .build());

        // Goal 3: Medication & Clinical Surveillance
        fallbackGoals.add(CarePlanSuggestionResponse.SuggestedGoalDTO.builder()
                .goalName("Medication Adherence & Hydration Surveillance")
                .description("Ensure optimal hydration and therapeutic medication levels without adverse pharmacological events.")
                .priority("HIGH")
                .rationale("Elderly residents are vulnerable to dehydration, orthostatic hypotension, and polypharmacy interactions.")
                .interventions(List.of(
                        CarePlanSuggestionResponse.SuggestedInterventionDTO.builder()
                                .name("Promote fluid intake toward 1,500 mL daily target and log hydration observations.")
                                .assignedRole("CNA").build(),
                        CarePlanSuggestionResponse.SuggestedInterventionDTO.builder()
                                .name("Review medications for anticholinergic burden (ACB) and monitor blood pressure.")
                                .assignedRole("NURSE").build(),
                        CarePlanSuggestionResponse.SuggestedInterventionDTO.builder()
                                .name("Physician medication regimen review and routine laboratory surveillance.")
                                .assignedRole("DOCTOR").build()
                ))
                .build());

        // Goal 4 (if wandering / memory issues)
        boolean hasMemoryRisk = (request.getCareLevel() != null && request.getCareLevel().toLowerCase().contains("memory"))
                || (request.getRecentIncidents() != null && request.getRecentIncidents().stream().anyMatch(i -> i.toUpperCase().contains("ELOPEMENT") || i.toUpperCase().contains("WANDER")));
        if (hasMemoryRisk) {
            fallbackGoals.add(CarePlanSuggestionResponse.SuggestedGoalDTO.builder()
                    .goalName("Elopement Risk Mitigation & Cognitive Engagement")
                    .description("Maintain resident security within safe perimeter while providing stimulating, comforting activities.")
                    .priority("HIGH")
                    .rationale("Memory care safety protocol to mitigate wandering toward unmonitored exits.")
                    .interventions(List.of(
                            CarePlanSuggestionResponse.SuggestedInterventionDTO.builder()
                                    .name("Conduct 30-minute visual safety checks, especially during 16:00-20:00 sunsetting hours.")
                                    .assignedRole("CNA").build(),
                            CarePlanSuggestionResponse.SuggestedInterventionDTO.builder()
                                    .name("Engage in structured calming sensory activities (music, tactile crafts, reminiscence).")
                                    .assignedRole("OT").build()
                    ))
                    .build());
        }

        return CarePlanSuggestionResponse.builder()
                .residentSummary("Clinical care profile for " + name + " (Age " + (request.getAge() != null ? request.getAge() : "N/A") + ", " + (request.getCareLevel() != null ? request.getCareLevel() : "Standard Care") + "). Goals compiled from evidence-based geriatric protocols.")
                .suggestedGoals(fallbackGoals)
                .generationMethod("CLINICAL_PROTOCOL_DRAFT")
                .build();
    }
}
