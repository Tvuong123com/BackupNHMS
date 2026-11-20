package com.eldercare.modules.ai.summary.service;

import com.eldercare.modules.ai.summary.dto.SummaryRequest;
import com.eldercare.modules.ai.summary.dto.SummaryResponse;
import com.eldercare.modules.ai.summary.dto.HandoverTypes.*;
import com.eldercare.modules.resident_intake.resident.repository.ResidentRepository;
import com.eldercare.modules.ai.summary.entity.AiSummaryDraftEntity;
import com.eldercare.modules.ai.summary.repository.AiSummaryDraftRepository;
import com.eldercare.modules.careplan_management.cna_daily_tasks.repository.CareTaskRepository;
import com.eldercare.modules.risk_incident.incident_tracking.repository.IncidentRepository;
import lombok.RequiredArgsConstructor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShiftSummaryDraftService {
    private final CareTaskRepository careTaskRepository;
    private final IncidentRepository incidentRepository;
    private final SummaryAiService summaryAiService;
    private final AiSummaryDraftRepository draftRepository;
    private final ObjectMapper objectMapper;
    private final ResidentRepository residentRepository;

    public record DraftRequest(OffsetDateTime start, OffsetDateTime end, Long residentId,
                               String audience, String verifiedNotes) {}
    public record DraftResponse(Long draftId, List<String> sourceEvents, SummaryResponse draft,
                                boolean reviewRequired) {}
    public record SavedDraft(Long id, String audience, Long residentId, OffsetDateTime start,
                             OffsetDateTime end, OffsetDateTime createdAt,
                             List<String> sourceEvents, SummaryResponse draft) {}

    @Transactional
    public DraftResponse createDraft(DraftRequest request, String staffEmail) {
        if (request.verifiedNotes() != null && request.verifiedNotes().length() > 10000) {
            throw new IllegalArgumentException("Verified notes must be no more than 10000 characters");
        }
        if (request.start() == null || request.end() == null || !request.end().isAfter(request.start())) {
            throw new IllegalArgumentException("Shift end must be later than shift start");
        }
        if (Duration.between(request.start(), request.end()).compareTo(Duration.ofHours(24)) > 0) {
            throw new IllegalArgumentException("Choose a shift lasting no more than 24 hours");
        }
        if (request.end().isAfter(OffsetDateTime.now().plusMinutes(5))) {
            throw new IllegalArgumentException("Shift end must be in the past");
        }
        boolean family = "FAMILY".equalsIgnoreCase(request.audience());
        if (family && request.residentId() == null) {
            throw new IllegalArgumentException("A resident is required for a family update draft");
        }
        List<String> events = new ArrayList<>();
        List<SourceRecord> sources = new ArrayList<>();
        if (request.residentId() != null) {
            residentRepository.findByIdAndIsDeletedFalse(request.residentId())
                    .orElseThrow(() -> new IllegalArgumentException("Resident not found"));
        }
        for (var task : careTaskRepository.findByScheduledTimeBetween(request.start(), request.end())) {
            var resident = task.getCareIntervention().getCareGoal().getCarePlan().getResident();
            if (request.residentId() != null && !request.residentId().equals(resident.getId())) continue;
            if (task.getStatus() == null || resident.isDeleted() || sources.size() >= 100) continue;
            String description = "Care task " + task.getTaskType() + ": " + task.getStatus().name()
                    + "; scheduled " + task.getScheduledTime()
                    + (task.getCompletedAt() == null ? "" : "; completed " + task.getCompletedAt())
                    + (task.getGoal() == null ? "" : "; notes: " + task.getGoal());
            sources.add(new SourceRecord("TASK:" + task.getId(), resident.getId(),
                    resident.getFirstName() + " " + resident.getLastName(), description));
        }
        for (var incident : incidentRepository.findForShift(request.start(), request.end())) {
            if (request.residentId() != null && !request.residentId().equals(incident.getResident().getId())) continue;
            if (incident.getResident().isDeleted() || sources.size() >= 100) continue;
            // Family drafts remain internal and still need a licensed review before any disclosure.
            sources.add(new SourceRecord("INCIDENT:" + incident.getId(), incident.getResident().getId(),
                    incident.getResident().getFirstName() + " " + incident.getResident().getLastName(),
                    "Incident " + incident.getIncidentType() + " at " + incident.getOccurredAt()
                            + "; status " + incident.getStatus()
                            + (incident.getDescription() == null ? "" : "; notes: " + incident.getDescription())));
        }
        if (request.residentId() != null && request.verifiedNotes() != null && !request.verifiedNotes().isBlank()) {
            var resident = residentRepository.findByIdAndIsDeletedFalse(request.residentId()).orElseThrow();
            sources.add(new SourceRecord("NOTE:1", resident.getId(), resident.getFirstName() + " " + resident.getLastName(),
                    "Staff-verified notes: " + request.verifiedNotes()));
        }
        for (var source : sources) {
            events.add("[" + source.reference() + "] residentId=" + source.residentId() + " "
                    + source.residentName() + ": " + source.description());
        }
        if (events.isEmpty() && (request.verifiedNotes() == null || request.verifiedNotes().isBlank())) {
            throw new IllegalArgumentException("No recorded events in this period. Add verified notes or choose another shift.");
        }
        SummaryRequest summaryRequest = SummaryRequest.builder()
                .type(family ? "FAMILY_UPDATE" : "SHIFT_HANDOVER")
                .tone(family ? "FAMILY_FRIENDLY" : "CLINICAL")
                .shiftName(request.start() + " to " + request.end())
                .events(events)
                .extraNotes(request.verifiedNotes())
                .build();
        SummaryResponse draft = summaryAiService.generateSummary(summaryRequest);
        List<Action> actions = new ArrayList<>();
        if (!family && draft.getProposedActions() != null) {
            for (var item : draft.getProposedActions()) {
                if (actions.size() >= 12) break;
                if (HandoverWorkflowService.validSuggestion(item, sources)) {
                    actions.add(new Action(UUID.randomUUID().toString(), item.residentId(), item.description().trim(),
                            item.taskType(), item.sourceRefs(), null, null, null));
                }
            }
        }
        draft.setProposedActions(actions.stream().map(a -> new Suggestion(a.residentId(), a.description(), a.taskType(), a.sourceRefs())).toList());
        AiSummaryDraftEntity row = new AiSummaryDraftEntity();
        row.setAudience(family ? "FAMILY" : "STAFF");
        row.setResidentId(request.residentId());
        row.setCreatedBy(staffEmail);
        row.setShiftStart(request.start());
        row.setShiftEnd(request.end());
        try {
            row.setSourceEventsJson(objectMapper.writeValueAsString(events));
            row.setDraftJson(objectMapper.writeValueAsString(draft));
            if (!family) row.setHandoverJson(objectMapper.writeValueAsString(new Context(sources, actions)));
        } catch (Exception e) {
            throw new IllegalStateException("Could not save summary draft", e);
        }
        row = draftRepository.save(row);
        return new DraftResponse(row.getId(), events, draft, true);
    }

    @Transactional(readOnly = true)
    public List<SavedDraft> myDrafts(String staffEmail) {
        return draftRepository.findTop20ByCreatedByOrderByIdDesc(staffEmail).stream().map(row -> {
            try {
                List<String> events = objectMapper.readValue(row.getSourceEventsJson(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
                var draft = objectMapper.readValue(row.getDraftJson(), SummaryResponse.class);
                return new SavedDraft(row.getId(), row.getAudience(), row.getResidentId(),
                        row.getShiftStart(), row.getShiftEnd(), row.getCreatedAt(), events, draft);
            } catch (Exception e) {
                throw new IllegalStateException("Saved summary draft could not be read", e);
            }
        }).toList();
    }
}
