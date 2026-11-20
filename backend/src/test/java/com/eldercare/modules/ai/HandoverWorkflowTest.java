package com.eldercare.modules.ai;

import com.eldercare.modules.ai.summary.dto.HandoverTypes.*;
import com.eldercare.modules.ai.summary.dto.SummaryRequest;
import com.eldercare.modules.ai.summary.dto.SummaryResponse;
import com.eldercare.modules.ai.summary.entity.AiSummaryDraftEntity;
import com.eldercare.modules.ai.summary.repository.AiSummaryDraftRepository;
import com.eldercare.modules.ai.summary.service.*;
import com.eldercare.modules.ai.engine.AiExecutionService;
import com.eldercare.modules.admin.user_management.*;
import com.eldercare.modules.resident_intake.resident.repository.ResidentRepository;
import com.eldercare.modules.resident_intake.resident_profile.ResidentEntity;
import com.eldercare.modules.careplan_management.careplan_design.repository.database_schema.*;
import com.eldercare.modules.careplan_management.careplan_design.repository.jpa.*;
import com.eldercare.modules.careplan_management.cna_daily_tasks.entity.CareTaskEntity;
import com.eldercare.modules.careplan_management.cna_daily_tasks.repository.CareTaskRepository;
import com.eldercare.modules.risk_incident.incident_tracking.repository.IncidentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import java.time.OffsetDateTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

class HandoverWorkflowTest {
    private final ObjectMapper mapper = new ObjectMapper();
    private final AiSummaryDraftRepository drafts = mock(AiSummaryDraftRepository.class);
    private final ResidentRepository residents = mock(ResidentRepository.class);
    private final UserRepository users = mock(UserRepository.class);
    private final JpaCarePlanRepository plans = mock(JpaCarePlanRepository.class);
    private final JpaCareGoalRepository goals = mock(JpaCareGoalRepository.class);
    private final JpaCareInterventionRepository interventions = mock(JpaCareInterventionRepository.class);
    private final CareTaskRepository tasks = mock(CareTaskRepository.class);
    private final HandoverWorkflowService workflow = new HandoverWorkflowService(drafts, residents, users, plans, goals, interventions, tasks, mapper);
    private final SourceRecord source = new SourceRecord("NOTE:1", 7L, "Fixture Resident", "Staff verified observation");
    private final Suggestion suggestion = new Suggestion(7L, "Assist with documented mobility needs", "MOBILITY", List.of("NOTE:1"));

    private AiSummaryDraftEntity draft() throws Exception {
        var row = new AiSummaryDraftEntity(); row.setId(5L); row.setAudience("STAFF"); row.setCreatedBy("nurse@example.test");
        row.setShiftStart(OffsetDateTime.now().minusHours(8)); row.setShiftEnd(OffsetDateTime.now().minusMinutes(1));
        row.setDraftJson(mapper.writeValueAsString(SummaryResponse.builder().title("Draft").narrativeSummary("Verified summary").generationMethod("AI").build()));
        row.setHandoverJson(mapper.writeValueAsString(new Context(List.of(source), List.of(
                new Action("a1", 7L, suggestion.description(), "MOBILITY", List.of("NOTE:1"), null, null, null)))));
        when(drafts.findById(5L)).thenReturn(Optional.of(row)); when(drafts.findLockedById(5L)).thenReturn(Optional.of(row));
        return row;
    }
    private void approved(AiSummaryDraftEntity row) {
        row.setApprovalStatus("APPROVED"); row.setApprovedBy("nurse@example.test"); row.setApprovedAt(OffsetDateTime.now());
    }
    private void assignmentSetup() {
        var resident = ResidentEntity.builder().id(7L).firstName("Fixture").lastName("Resident").status("ACTIVE").build();
        when(residents.findByIdAndIsDeletedFalse(7L)).thenReturn(Optional.of(resident));
        var cna = new UserEntity(); cna.setId(23L); cna.setStatus("ACTIVE"); cna.setFirstName("Test"); cna.setLastName("CNA");
        cna.setRole(RoleEntity.builder().roleName("CNA_(Caregiver)").build());
        when(users.findById(23L)).thenReturn(Optional.of(cna));
        when(plans.findFirstByResident_IdAndStatusIgnoreCaseAndIsDeletedFalseOrderByCreatedAtDesc(7L, "ACTIVE")).thenReturn(Optional.of(new CarePlanSchema()));
        when(goals.save(any())).thenAnswer(call -> call.getArgument(0)); when(interventions.save(any())).thenAnswer(call -> call.getArgument(0));
        when(tasks.save(any())).thenAnswer(call -> { CareTaskEntity task = call.getArgument(0); task.setId(10003L); when(tasks.findById(10003L)).thenReturn(Optional.of(task)); return task; });
    }
    @Test void privateDraftCannotBeReadOrApprovedByAnotherStaffMember() throws Exception {
        draft();
        assertEquals(403, assertThrows(ResponseStatusException.class, () -> workflow.get(5L, "other@example.test")).getStatusCode().value());
        assertEquals(403, assertThrows(ResponseStatusException.class, () -> workflow.approve(5L, new Review("Reviewed", "Reviewed summary", List.of()), "other@example.test")).getStatusCode().value());
        verify(drafts, never()).save(any());
    }
    @Test void approvalStoresReviewedTextAndAuthorAuditAndMakesItReadableByAnotherReviewer() throws Exception {
        var row = draft();
        var result = workflow.approve(5L, new Review("Reviewed", "Reviewed summary", List.of(suggestion)), "nurse@example.test");
        assertEquals("APPROVED", result.status()); assertEquals("nurse@example.test", result.approvedBy()); assertNotNull(result.approvedAt());
        assertEquals("Reviewed summary", result.summary().getNarrativeSummary());
        assertEquals(7L, workflow.get(5L, "don@example.test").actions().get(0).action().residentId());
        assertFalse(workflow.get(5L, "don@example.test").canApprove());
        verify(drafts).save(row);
    }
    @Test void approvalRejectsSourceFromAnotherResidentAndMedicationTasks() throws Exception {
        draft();
        var mismatch = new Suggestion(99L, "Wrong chart", "OBSERVATION", List.of("NOTE:1"));
        assertThrows(ResponseStatusException.class, () -> workflow.approve(5L, new Review("x", "y", List.of(mismatch)), "nurse@example.test"));
        var medication = new Suggestion(7L, "Administer medication", "MEDICATION", List.of("NOTE:1"));
        assertThrows(ResponseStatusException.class, () -> workflow.approve(5L, new Review("x", "y", List.of(medication)), "nurse@example.test"));
        verify(drafts, never()).save(any());
    }
    @Test void taskCreationRequiresApprovalAndFamilyDraftsAreNeverShared() throws Exception {
        var row = draft();
        assertThrows(ResponseStatusException.class, () -> workflow.assign(5L, "a1", new Assignment(23L, OffsetDateTime.now().plusHours(1)), "nurse@example.test"));
        row.setAudience("FAMILY"); approved(row);
        assertThrows(ResponseStatusException.class, () -> workflow.get(5L, "nurse@example.test"));
        verifyNoInteractions(tasks, plans, goals, interventions);
    }
    @Test void reviewedActionCreatesCorrectResidentAndCnaTaskAndRepeatedAssignmentIsIdempotent() throws Exception {
        var row = draft(); approved(row); assignmentSetup();
        var due = OffsetDateTime.now().plusHours(2);
        var result = workflow.assign(5L, "a1", new Assignment(23L, due), "don@example.test");
        var item = result.actions().get(0);
        assertEquals(10003L, item.action().careTaskId()); assertEquals("PENDING", item.taskStatus()); assertEquals(23L, item.assignedCnaId()); assertEquals(due, item.scheduledTime());
        var capture = org.mockito.ArgumentCaptor.forClass(CareTaskEntity.class); verify(tasks).save(capture.capture());
        assertEquals("MOBILITY", capture.getValue().getTaskType()); assertTrue(capture.getValue().getGoal().contains("Handover #5"));
        assertTrue(capture.getValue().getGoal().contains("don@example.test"));
        assertEquals(10003L, workflow.assign(5L, "a1", new Assignment(23L, due), "other@example.test").actions().get(0).action().careTaskId());
        verify(tasks, times(1)).save(any());
    }
    @Test void lockedResidentOrWrongAssigneeCannotCreateTasks() throws Exception {
        var row = draft(); approved(row); assignmentSetup();
        residents.findByIdAndIsDeletedFalse(7L).orElseThrow().setChartLocked(true);
        assertThrows(ResponseStatusException.class, () -> workflow.assign(5L, "a1", new Assignment(23L, OffsetDateTime.now().plusHours(1)), "nurse@example.test"));
        residents.findByIdAndIsDeletedFalse(7L).orElseThrow().setChartLocked(false);
        users.findById(23L).orElseThrow().setRole(RoleEntity.builder().roleName("NURSE").build());
        assertThrows(ResponseStatusException.class, () -> workflow.assign(5L, "a1", new Assignment(23L, OffsetDateTime.now().plusHours(1)), "nurse@example.test"));
        verify(tasks, never()).save(any());
    }
    @Test void aiSuggestionsAreGroundedToResidentAndSourceBeforeSaving() throws Exception {
        var ai = mock(AiExecutionService.class);
        when(ai.execute(anyString(), anyString())).thenReturn("""
                {"title":"AI handover","narrativeSummary":"Verified observations need review.","proposedActions":[
                {"residentId":7,"description":"Review documented mobility support","taskType":"MOBILITY","sourceRefs":["NOTE:1"]},
                {"residentId":99,"description":"Invented resident","taskType":"OBSERVATION","sourceRefs":["NOTE:1"]},
                {"residentId":7,"description":"Invented source","taskType":"OBSERVATION","sourceRefs":["TASK:999"]}]}
                """);
        when(residents.findByIdAndIsDeletedFalse(7L)).thenReturn(Optional.of(ResidentEntity.builder().id(7L).firstName("Fixture").lastName("Resident").build()));
        when(drafts.save(any())).thenAnswer(call -> { AiSummaryDraftEntity row = call.getArgument(0); row.setId(5L); return row; });
        var service = new ShiftSummaryDraftService(tasks, mock(IncidentRepository.class), new SummaryAiService(ai, mapper), drafts, mapper, residents);
        var end = OffsetDateTime.now().minusMinutes(1);
        var response = service.createDraft(new ShiftSummaryDraftService.DraftRequest(end.minusHours(8), end, 7L, "STAFF", "Documented mobility support needs review"), "nurse@example.test");
        assertEquals("AI", response.draft().getGenerationMethod()); assertEquals(1, response.draft().getProposedActions().size());
        verify(ai).execute(contains("proposedActions"), contains("residentId=7"));
    }
    @Test void unavailableAiIsExplicitlyMarkedAndDoesNotInventFollowUps() {
        var ai = mock(AiExecutionService.class); when(ai.execute(anyString(), anyString())).thenThrow(new IllegalStateException("offline"));
        var result = new SummaryAiService(ai, mapper).generateSummary(SummaryRequest.builder().events(List.of("Verified input")).build());
        assertEquals("RECORDED_INPUT_FALLBACK", result.getGenerationMethod()); assertTrue(result.getProposedActions().isEmpty());
    }
    @Test void partialAiJsonGetsSafeDisplayDefaultsRatherThanBreakingReviewForm() {
        var ai = mock(AiExecutionService.class);
        when(ai.execute(anyString(), anyString())).thenReturn("{\"narrativeSummary\":\"Verified observations only.\"}");
        var result = new SummaryAiService(ai, mapper).generateSummary(SummaryRequest.builder().events(List.of("Verified input")).build());
        assertEquals("Shift handover draft", result.getTitle()); assertNotNull(result.getProposedActions());
        assertNotNull(result.getHandoverActionItems()); assertEquals("AI", result.getGenerationMethod());
    }
}
