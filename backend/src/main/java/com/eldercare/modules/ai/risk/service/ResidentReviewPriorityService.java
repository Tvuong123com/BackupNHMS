package com.eldercare.modules.ai.risk.service;

import com.eldercare.modules.risk_incident.incident_tracking.entity.IncidentEntity;
import com.eldercare.modules.risk_incident.incident_tracking.repository.IncidentRepository;
import com.eldercare.modules.resident_intake.assessment.repository.AssessmentRepository;
import com.eldercare.modules.ai.risk.repository.FallScreeningRepository;
import com.eldercare.modules.resident_intake.resident.repository.ResidentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** A transparent review queue based on documented incidents, not a clinical prediction. */
@Service
public class ResidentReviewPriorityService {
    private final IncidentRepository incidentRepository;
    private final AssessmentRepository assessmentRepository;
    private final FallScreeningRepository fallScreeningRepository;
    private final ResidentRepository residentRepository;
    private final DynamicFallRiskService dynamicFallRiskService;

    public ResidentReviewPriorityService(IncidentRepository incidentRepository,
                                         AssessmentRepository assessmentRepository,
                                         FallScreeningRepository fallScreeningRepository,
                                         ResidentRepository residentRepository) {
        this(incidentRepository, assessmentRepository, fallScreeningRepository, residentRepository, null);
    }

    @Autowired
    public ResidentReviewPriorityService(IncidentRepository incidentRepository,
                                         AssessmentRepository assessmentRepository,
                                         FallScreeningRepository fallScreeningRepository,
                                         ResidentRepository residentRepository,
                                         DynamicFallRiskService dynamicFallRiskService) {
        this.incidentRepository = incidentRepository;
        this.assessmentRepository = assessmentRepository;
        this.fallScreeningRepository = fallScreeningRepository;
        this.residentRepository = residentRepository;
        this.dynamicFallRiskService = dynamicFallRiskService;
    }

    public record ReviewPriority(Long residentId, String residentName, int incidentCount,
                                 int recentIncidentCount, Integer currentAdlScore,
                                 OffsetDateTime assessedAt, String assessmentStatus,
                                 Integer morseScore, OffsetDateTime morseAssessedAt,
                                 int priorityScore, String priority, List<String> factors,
                                 String recommendedAction, OffsetDateTime generatedAt,
                                 String methodVersion, List<Long> sourceIncidentIds,
                                 Long sourceAssessmentId, Long sourceScreeningId,
                                 List<String> dataGaps,
                                 Integer dfrsScore, String fallRiskLevel,
                                 List<String> fallInterventions,
                                 Integer incidentSubscore, Integer medicationSubscore,
                                 Integer adlSubscore, Integer vitalSubscore,
                                 List<String> activeFridNames) {
        public ReviewPriority(Long residentId, String residentName, int incidentCount,
                               int recentIncidentCount, Integer currentAdlScore,
                               OffsetDateTime assessedAt, String assessmentStatus,
                               Integer morseScore, OffsetDateTime morseAssessedAt,
                               int priorityScore, String priority, List<String> factors,
                               String recommendedAction, OffsetDateTime generatedAt,
                               String methodVersion, List<Long> sourceIncidentIds,
                               Long sourceAssessmentId, Long sourceScreeningId,
                               List<String> dataGaps) {
            this(residentId, residentName, incidentCount, recentIncidentCount, currentAdlScore,
                 assessedAt, assessmentStatus, morseScore, morseAssessedAt,
                 priorityScore, priority, factors, recommendedAction, generatedAt,
                 methodVersion, sourceIncidentIds, sourceAssessmentId, sourceScreeningId,
                 dataGaps, null, null, List.of(), null, null, null, null, List.of());
        }
    }

    @Transactional(readOnly = true)
    public List<ReviewPriority> recentPriorities() {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime recentSince = now.minusDays(30);
        OffsetDateTime historySince = now.minusDays(90);
        Map<Long, List<IncidentEntity>> byResident = new LinkedHashMap<>();
        for (IncidentEntity incident : incidentRepository.findRecentWithResident(historySince)) {
            if (incident.getResident() == null || incident.getResident().isDeleted()) continue;
            byResident.computeIfAbsent(incident.getResident().getId(), id -> new ArrayList<>()).add(incident);
        }
        return residentRepository.findByStatusIgnoreCaseAndIsDeletedFalse("ACTIVE").stream().map(resident -> {
            List<IncidentEntity> incidents = byResident.getOrDefault(resident.getId(), List.of());
            List<IncidentEntity> recent = incidents.stream().filter(i -> i.getOccurredAt() != null
                    && !i.getOccurredAt().isBefore(recentSince)).toList();
            List<IncidentEntity> earlier = incidents.stream().filter(i -> i.getOccurredAt() != null
                    && i.getOccurredAt().isBefore(recentSince)).toList();
            long falls = recent.stream().filter(i -> i.getIncidentType() != null
                    && "FALL".equals(i.getIncidentType().name())).count();
            long medication = recent.stream().filter(i -> i.getIncidentType() != null
                    && "MEDICATION_ERROR".equals(i.getIncidentType().name())).count();
            long earlierFalls = earlier.stream().filter(i -> i.getIncidentType() != null
                    && "FALL".equals(i.getIncidentType().name())).count();
            int score = Math.min(10, (int) (falls * 3 + medication * 3
                    + (recent.size() - falls - medication) + earlierFalls
                    + (earlier.size() - earlierFalls > 0 ? 1 : 0)));
            List<String> factors = new ArrayList<>();
            if (falls > 0) factors.add(falls + " documented fall incident(s) in 30 days");
            if (medication > 0) factors.add(medication + " documented medication incident(s) in 30 days");
            long other = recent.size() - falls - medication;
            if (other > 0) factors.add(other + " other documented incident(s) in 30 days");
            if (!earlier.isEmpty()) factors.add(earlier.size() + " older incident(s) from days 31–90, including "
                    + earlierFalls + " fall(s)");
            var assessment = assessmentRepository.findByResidentIdAndIsCurrentTrue(resident.getId())
                    .orElseGet(() -> assessmentRepository
                            .findFirstByResidentIdOrderByCreatedAtDesc(resident.getId()).orElse(null));
            Integer adlScore = assessment == null ? null : assessment.getAdlTotalScore();
            OffsetDateTime assessedAt = assessment == null ? null : assessment.getCreatedAt();
            boolean confirmed = assessment != null && "CONFIRMED".equalsIgnoreCase(assessment.getStatus());
            String assessmentStatus = assessment == null ? null : confirmed ? "CONFIRMED" : "UNVERIFIED";
            if (adlScore != null) factors.add((confirmed ? "Confirmed" : "Unverified") + " ADL total: " + adlScore
                    + " (chart context only; scoring scale requires clinical review)");
            var morse = fallScreeningRepository.findFirstByResidentIdOrderByIdDesc(resident.getId()).orElse(null);
            Integer morseScore = morse == null ? null : morse.getTotalScore();
            OffsetDateTime morseAssessedAt = morse == null ? null : morse.getAssessedAt();
            if (morseScore != null) factors.add("Staff-recorded Morse score: " + morseScore
                    + " (raw screening score; use approved local cutoffs)");
            else factors.add("No staff-recorded Morse screening in this chart");
            if (assessment == null) factors.add("No assessment found in this chart");
            List<String> dataGaps = new ArrayList<>();
            if (morse == null) dataGaps.add("MORSE_NOT_RECORDED");
            if (assessment == null) dataGaps.add("ASSESSMENT_NOT_RECORDED");
            else if (!confirmed) dataGaps.add("ASSESSMENT_UNVERIFIED");

            // Evaluate Dynamic Fall Risk Score (DFRS) if service is injected
            Integer finalDfrsScore = null;
            String fallLevel = null;
            List<String> fallInterventionDescriptions = List.of();
            Integer incidentSub = null;
            Integer medSub = null;
            Integer adlSub = null;
            Integer vitalSub = null;
            List<String> fridNames = List.of();
            String methodVersion = "incident-review-v1";
            int combinedScore = score;
            String priorityLevel = score >= 6 ? "HIGH" : "REVIEW";
            String action = incidents.isEmpty()
                    ? "Review the current assessment and screening under facility policy. No incident-based risk estimate is available."
                    : "Review the incident record and current care plan with the responsible clinician.";

            if (dynamicFallRiskService != null) {
                try {
                    var dfrs = dynamicFallRiskService.calculateFallRisk(resident.getId());
                    finalDfrsScore = dfrs.getDfrsScore();
                    fallLevel = dfrs.getRiskLevel();
                    incidentSub = dfrs.getIncidentSubscore();
                    medSub = dfrs.getMedicationSubscore();
                    adlSub = dfrs.getAdlSubscore();
                    vitalSub = dfrs.getVitalSubscore();
                    if (dfrs.getActiveFridMedications() != null) {
                        fridNames = dfrs.getActiveFridMedications().stream()
                                .map(m -> m.getMedicationName())
                                .toList();
                    }
                    combinedScore = Math.max(score, Math.min(10, finalDfrsScore / 10));
                    priorityLevel = (combinedScore >= 6 || "HIGH".equals(fallLevel)) ? "HIGH" : "REVIEW";
                    methodVersion = "dynamic-risk-v2";
                    fallInterventionDescriptions = dfrs.getRecommendedInterventions().stream()
                            .map(item -> "[" + item.getRole() + "] " + item.getAction())
                            .toList();
                    if ("HIGH".equals(fallLevel)) {
                        action = "High dynamic fall risk (DFRS " + finalDfrsScore + "/100). Implement bed sensor and 2-hour toileting schedule.";
                    }
                } catch (Exception ignored) {}
            }

            return new ReviewPriority(resident.getId(), resident.getFirstName() + " " + resident.getLastName(),
                    incidents.size(), recent.size(), adlScore, assessedAt, assessmentStatus,
                    morseScore, morseAssessedAt,
                    combinedScore, priorityLevel, factors,
                    action,
                    now, methodVersion, incidents.stream().map(IncidentEntity::getId)
                            .filter(id -> id != null).toList(),
                    assessment == null ? null : assessment.getId(),
                    morse == null ? null : morse.getId(), dataGaps,
                    finalDfrsScore, fallLevel, fallInterventionDescriptions,
                    incidentSub, medSub, adlSub, vitalSub, fridNames);
        }).sorted(Comparator.comparingInt(ReviewPriority::priorityScore).reversed())
                .toList();
    }
}
