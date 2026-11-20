package com.eldercare.modules.ai.summary.service;

import com.eldercare.common.enums.CarePlanGoalStatusEnum;
import com.eldercare.modules.admin.user_management.UserRepository;
import com.eldercare.modules.ai.summary.dto.HandoverTypes.*;
import com.eldercare.modules.ai.summary.dto.SummaryResponse;
import com.eldercare.modules.ai.summary.entity.AiSummaryDraftEntity;
import com.eldercare.modules.ai.summary.repository.AiSummaryDraftRepository;
import com.eldercare.modules.careplan_management.careplan_design.repository.database_schema.CareGoalSchema;
import com.eldercare.modules.careplan_management.careplan_design.repository.database_schema.CareInterventionSchema;
import com.eldercare.modules.careplan_management.careplan_design.repository.jpa.*;
import com.eldercare.modules.careplan_management.cna_daily_tasks.entity.CareTaskEntity;
import com.eldercare.modules.careplan_management.cna_daily_tasks.enums.TaskStatus;
import com.eldercare.modules.careplan_management.cna_daily_tasks.repository.CareTaskRepository;
import com.eldercare.modules.resident_intake.resident.repository.ResidentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HandoverWorkflowService {
    public static final Set<String> TASK_TYPES = Set.of("OBSERVATION", "FALL_PREVENTION", "HYDRATION",
            "MEAL_ASSISTANCE", "MOBILITY", "REPOSITIONING", "TOILETING", "ACTIVITY");
    private final AiSummaryDraftRepository drafts;
    private final ResidentRepository residents;
    private final UserRepository users;
    private final JpaCarePlanRepository plans;
    private final JpaCareGoalRepository goals;
    private final JpaCareInterventionRepository interventions;
    private final CareTaskRepository tasks;
    private final ObjectMapper mapper;

    public List<ResidentOption> residentOptions() {
        return residents.findByStatusIgnoreCaseAndIsDeletedFalse("ACTIVE").stream()
                .map(r -> new ResidentOption(r.getId(), r.getFirstName() + " " + r.getLastName())).toList();
    }

    public Detail get(Long id, String viewer) {
        var row = drafts.findById(id).orElseThrow(() -> missing("Handover not found"));
        requireStaff(row);
        if (!row.getCreatedBy().equalsIgnoreCase(viewer) && !"APPROVED".equals(row.getApprovalStatus())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This draft is private until its author approves it");
        }
        return detail(row, viewer);
    }

    public List<Detail> shared(String viewer) {
        return drafts.findTop50ByAudienceAndApprovalStatusOrderByApprovedAtDesc("STAFF", "APPROVED")
                .stream().map(row -> detail(row, viewer)).toList();
    }

    @Transactional
    public Detail approve(Long id, Review review, String actor) {
        var row = drafts.findLockedById(id).orElseThrow(() -> missing("Handover not found"));
        requireStaff(row);
        if (!row.getCreatedBy().equalsIgnoreCase(actor)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the author can review and share this draft");
        }
        if ("APPROVED".equals(row.getApprovalStatus())) return detail(row, actor);
        var context = context(row);
        if (row.getHandoverJson() == null) throw invalid("This older draft has no resident/source links. Generate a new draft to share tasks.");
        if (review == null || review.title() == null || review.title().isBlank() || review.title().length() > 255
                || review.narrativeSummary() == null || review.narrativeSummary().isBlank() || review.narrativeSummary().length() > 20000) {
            throw invalid("Review the title and summary before sharing");
        }
        var suggestions = review.actions() == null ? List.<Suggestion>of() : review.actions();
        if (suggestions.size() > 12) throw invalid("A handover supports up to 12 follow-up actions");
        List<Action> reviewed = new ArrayList<>();
        Set<String> duplicates = new HashSet<>();
        for (var suggestion : suggestions) {
            if (!validSuggestion(suggestion, context.sources())) {
                throw invalid("Each action needs a supported care task type and source references for the selected resident");
            }
            String key = suggestion.residentId() + ":" + suggestion.description().trim().toLowerCase(Locale.ROOT);
            if (!duplicates.add(key)) throw invalid("Remove duplicate follow-up actions before sharing");
            reviewed.add(new Action(UUID.randomUUID().toString(), suggestion.residentId(), suggestion.description().trim(),
                    suggestion.taskType(), List.copyOf(suggestion.sourceRefs()), null, null, null));
        }
        var summary = summary(row);
        summary.setTitle(review.title().trim());
        summary.setNarrativeSummary(review.narrativeSummary().trim());
        summary.setHandoverActionItems(suggestions.stream().map(Suggestion::description).toList());
        summary.setProposedActions(suggestions);
        row.setDraftJson(json(summary));
        row.setHandoverJson(json(new Context(context.sources(), reviewed)));
        row.setApprovalStatus("APPROVED");
        row.setApprovedBy(actor);
        row.setApprovedAt(OffsetDateTime.now());
        drafts.save(row);
        return detail(row, actor);
    }

    @Transactional
    public Detail assign(Long id, String actionId, Assignment assignment, String actor) {
        // Lock the draft across checking/linking to prevent two clicks or staff from creating duplicate tasks.
        var row = drafts.findLockedById(id).orElseThrow(() -> missing("Handover not found"));
        requireStaff(row);
        if (!"APPROVED".equals(row.getApprovalStatus())) throw invalid("Approve and share the handover before creating care tasks");
        var context = context(row);
        var action = context.actions().stream().filter(a -> a.id().equals(actionId)).findFirst()
                .orElseThrow(() -> missing("Handover action not found"));
        if (action.careTaskId() != null) return detail(row, actor);
        if (assignment == null || assignment.assignedCnaId() == null || assignment.scheduledTime() == null
                || !assignment.scheduledTime().isAfter(OffsetDateTime.now())) {
            throw invalid("Choose an active CNA and a future scheduled time");
        }
        var resident = residents.findByIdAndIsDeletedFalse(action.residentId()).orElseThrow(() -> missing("Resident not found"));
        if (resident.isChartLocked() || !"ACTIVE".equalsIgnoreCase(resident.getStatus())) {
            throw invalid("Tasks cannot be added to an inactive or locked resident chart");
        }
        var cna = users.findById(assignment.assignedCnaId()).orElseThrow(() -> missing("CNA not found"));
        String role = cna.getRole() == null ? "" : cna.getRole().getRoleName();
        if (Boolean.TRUE.equals(cna.getIsDeleted()) || !"ACTIVE".equalsIgnoreCase(cna.getStatus())
                || !("CNA".equalsIgnoreCase(role) || "CNA_(Caregiver)".equalsIgnoreCase(role))) {
            throw invalid("The assignee must be an active CNA");
        }
        var plan = plans.findFirstByResident_IdAndStatusIgnoreCaseAndIsDeletedFalseOrderByCreatedAtDesc(resident.getId(), "ACTIVE")
                .orElseThrow(() -> invalid("Create or activate a care plan for this resident before assigning a handover task"));
        CareGoalSchema goal = new CareGoalSchema();
        goal.setCarePlan(plan);
        goal.setStatus(CarePlanGoalStatusEnum.IN_PROGRESS);
        goal.setTitle("Shift handover #" + id);
        goal.setDescription("Reviewed by " + row.getApprovedBy() + "; sources " + String.join(", ", action.sourceRefs()));
        goal = goals.save(goal);
        CareInterventionSchema intervention = new CareInterventionSchema();
        intervention.setCareGoal(goal);
        intervention.setAssignedRole("CNA");
        intervention.setTitle(action.description());
        intervention = interventions.save(intervention);
        CareTaskEntity task = new CareTaskEntity();
        task.setCareIntervention(intervention);
        task.setTaskType(action.taskType());
        task.setStatus(TaskStatus.PENDING);
        task.setIsAbnormalFlagged(false);
        task.setAssignedCna(cna);
        task.setScheduledTime(assignment.scheduledTime());
        task.setGoal(action.description() + " [Handover #" + id + "; approved by " + row.getApprovedBy()
                + "; assigned by " + actor + "; sources " + String.join(", ", action.sourceRefs()) + "]");
        task = tasks.save(task);
        var linked = new Action(action.id(), action.residentId(), action.description(), action.taskType(), action.sourceRefs(),
                task.getId(), actor, OffsetDateTime.now().toString());
        List<Action> actions = new ArrayList<>(context.actions());
        actions.replaceAll(a -> a.id().equals(actionId) ? linked : a);
        row.setHandoverJson(json(new Context(context.sources(), actions)));
        drafts.save(row);
        return detail(row, actor);
    }

    public static boolean validSuggestion(Suggestion action, List<SourceRecord> sources) {
        return action != null && action.residentId() != null && action.description() != null
                && !action.description().isBlank() && action.description().length() <= 2000
                && TASK_TYPES.contains(action.taskType() == null ? "" : action.taskType())
                && action.sourceRefs() != null && !action.sourceRefs().isEmpty()
                && action.sourceRefs().stream().allMatch(ref -> sources.stream()
                    .anyMatch(s -> s.reference().equals(ref) && s.residentId().equals(action.residentId())));
    }

    private Detail detail(AiSummaryDraftEntity row, String viewer) {
        var context = context(row);
        var actions = context.actions().stream().map(action -> {
            String name = context.sources().stream().filter(s -> s.residentId().equals(action.residentId()))
                    .map(SourceRecord::residentName).findFirst().orElse("Resident #" + action.residentId());
            var task = action.careTaskId() == null ? null : tasks.findById(action.careTaskId()).orElse(null);
            var cna = task == null ? null : task.getAssignedCna();
            return new ActionView(action, name, task == null ? (action.careTaskId() == null ? null : "DELETED") : task.getStatus().name(),
                    cna == null ? null : cna.getId(), cna == null ? null : cna.getFirstName() + " " + cna.getLastName(),
                    task == null ? null : task.getScheduledTime());
        }).toList();
        return new Detail(row.getId(), row.getApprovalStatus(), row.getCreatedBy(), row.getApprovedBy(), row.getApprovedAt(),
                row.getShiftStart(), row.getShiftEnd(), summary(row), context.sources(), actions,
                "DRAFT".equals(row.getApprovalStatus()) && row.getCreatedBy().equalsIgnoreCase(viewer) && row.getHandoverJson() != null);
    }

    private Context context(AiSummaryDraftEntity row) {
        if (row.getHandoverJson() == null) return new Context(List.of(), List.of());
        try { return mapper.readValue(row.getHandoverJson(), Context.class); }
        catch (Exception e) { throw new IllegalStateException("Could not read handover source links", e); }
    }
    private SummaryResponse summary(AiSummaryDraftEntity row) {
        try { return mapper.readValue(row.getDraftJson(), SummaryResponse.class); }
        catch (Exception e) { throw new IllegalStateException("Could not read handover summary", e); }
    }
    private String json(Object value) {
        try { return mapper.writeValueAsString(value); }
        catch (Exception e) { throw new IllegalStateException("Could not save reviewed handover", e); }
    }
    private void requireStaff(AiSummaryDraftEntity row) {
        if (!"STAFF".equals(row.getAudience())) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only staff handovers can be shared here");
    }
    private static ResponseStatusException invalid(String message) { return new ResponseStatusException(HttpStatus.BAD_REQUEST, message); }
    private static ResponseStatusException missing(String message) { return new ResponseStatusException(HttpStatus.NOT_FOUND, message); }
}
