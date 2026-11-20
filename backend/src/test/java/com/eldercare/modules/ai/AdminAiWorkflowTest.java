package com.eldercare.modules.ai;

import com.eldercare.modules.ai.risk.service.ResidentReviewPriorityService;
import com.eldercare.modules.ai.risk.repository.FallScreeningRepository;
import com.eldercare.modules.ai.risk.entity.FallScreeningEntity;
import com.eldercare.modules.ai.summary.service.ShiftSummaryDraftService;
import com.eldercare.modules.ai.summary.service.SummaryAiService;
import com.eldercare.modules.ai.summary.repository.AiSummaryDraftRepository;
import com.eldercare.modules.ai.summary.dto.SummaryResponse;
import com.eldercare.modules.ai.summary.entity.AiSummaryDraftEntity;
import com.eldercare.modules.ai.chat.entity.AiChatTurnEntity;
import com.eldercare.modules.ai.chat.repository.AiChatTurnRepository;
import com.eldercare.modules.ai.chat.service.AiChatService;
import com.eldercare.modules.ai.engine.AiExecutionService;
import com.eldercare.modules.ai.engine.AiSettingsService;
import com.eldercare.modules.ai.engine.ElderCareClinicalKnowledgeBase;
import com.eldercare.modules.ai.rag.service.SemanticSearchService;
import com.eldercare.modules.ai.voice.dto.VoiceCareParseResponse;
import com.eldercare.modules.ai.voice.entity.AiVoiceObservationEntity;
import com.eldercare.modules.ai.voice.repository.AiVoiceObservationRepository;
import com.eldercare.modules.ai.voice.service.VoiceObservationConfirmationService;
import com.eldercare.modules.admin.user_management.UserEntity;
import com.eldercare.modules.admin.user_management.UserRepository;
import com.eldercare.modules.clinical.vitals_sign.VitalSignEntity;
import com.eldercare.modules.clinical.vitals_sign.VitalSignRepository;
import com.eldercare.modules.careplan_management.cna_daily_tasks.entity.CareTaskEntity;
import com.eldercare.modules.careplan_management.cna_daily_tasks.enums.TaskStatus;
import com.eldercare.modules.careplan_management.cna_daily_tasks.repository.CareTaskRepository;
import com.eldercare.modules.resident_intake.resident.repository.ResidentRepository;
import com.eldercare.modules.resident_intake.assessment.AssessmentEntity;
import com.eldercare.modules.resident_intake.assessment.repository.AssessmentRepository;
import com.eldercare.modules.resident_intake.resident_profile.ResidentEntity;
import com.eldercare.modules.risk_incident.incident_tracking.entity.IncidentEntity;
import com.eldercare.modules.risk_incident.incident_tracking.entity.IncidentType;
import com.eldercare.modules.risk_incident.incident_tracking.repository.IncidentRepository;
import com.eldercare.modules.careplan_management.careplan_design.repository.database_schema.CareInterventionSchema;
import com.eldercare.modules.careplan_management.careplan_design.repository.database_schema.CareGoalSchema;
import com.eldercare.modules.careplan_management.careplan_design.repository.database_schema.CarePlanSchema;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AdminAiWorkflowTest {
    @Test
    void familyDraftIsSavedAndListedOnlyForItsCreator() {
        CareTaskRepository tasks = mock(CareTaskRepository.class);
        IncidentRepository incidents = mock(IncidentRepository.class);
        SummaryAiService summary = mock(SummaryAiService.class);
        AiSummaryDraftRepository drafts = mock(AiSummaryDraftRepository.class);
        when(summary.generateSummary(any())).thenReturn(SummaryResponse.builder()
                .title("Family draft").narrativeSummary("Resident ate lunch.")
                .keyHighlights(List.of()).handoverActionItems(List.of()).build());
        when(drafts.save(any())).thenAnswer(call -> {
            AiSummaryDraftEntity row = call.getArgument(0);
            row.setId(5L);
            return row;
        });
        ResidentRepository residents = mock(ResidentRepository.class);
        when(residents.findByIdAndIsDeletedFalse(7L)).thenReturn(Optional.of(ResidentEntity.builder().id(7L).firstName("Test").lastName("Resident").build()));
        var service = new ShiftSummaryDraftService(tasks, incidents, summary, drafts, new ObjectMapper(), residents);
        OffsetDateTime end = OffsetDateTime.now().minusMinutes(1);
        var request = new ShiftSummaryDraftService.DraftRequest(
                end.minusHours(8), end, 7L, "FAMILY", "Resident ate lunch.");

        assertEquals(5L, service.createDraft(request, "nurse@example.test").draftId());
        var saved = org.mockito.ArgumentCaptor.forClass(AiSummaryDraftEntity.class);
        verify(drafts).save(saved.capture());
        assertEquals("FAMILY", saved.getValue().getAudience());
        assertEquals("nurse@example.test", saved.getValue().getCreatedBy());
        when(drafts.findTop20ByCreatedByOrderByIdDesc("nurse@example.test"))
                .thenReturn(List.of(saved.getValue()));
        assertEquals(1, service.myDrafts("nurse@example.test").size());
        assertTrue(service.myDrafts("other@example.test").isEmpty());
    }

    @Test
    void familyDraftRequiresResidentAndShiftWithin24Hours() {
        CareTaskRepository tasks = mock(CareTaskRepository.class);
        IncidentRepository incidents = mock(IncidentRepository.class);
        var service = new ShiftSummaryDraftService(tasks, incidents, mock(SummaryAiService.class),
                mock(AiSummaryDraftRepository.class), new ObjectMapper(), mock(ResidentRepository.class));
        OffsetDateTime end = OffsetDateTime.now().minusMinutes(1);

        assertThrows(IllegalArgumentException.class, () -> service.createDraft(
                new ShiftSummaryDraftService.DraftRequest(end.minusHours(1), end, null, "FAMILY", null), "nurse@example.test"));
        assertThrows(IllegalArgumentException.class, () -> service.createDraft(
                new ShiftSummaryDraftService.DraftRequest(end.minusHours(24).minusMinutes(1),
                        end, 7L, "FAMILY", "Verified note"), "nurse@example.test"));
        verifyNoInteractions(tasks, incidents);
    }

    @Test
    void chatHistoryIsScopedToAuthenticatedOwner() {
        AiChatTurnRepository turns = mock(AiChatTurnRepository.class);
        String conversation = "65d1cc74-6c0c-480d-a9b4-96694c7306c3";
        AiChatTurnEntity row = new AiChatTurnEntity();
        row.setId(1L);
        row.setRole("user");
        row.setContent("Previous question");
        when(turns.findTop12ByConversationIdAndOwnerEmailOrderByIdDesc(conversation, "nurse@example.test"))
                .thenReturn(List.of(row));
        var service = new AiChatService(mock(AiExecutionService.class), mock(AiSettingsService.class),
                mock(ElderCareClinicalKnowledgeBase.class), turns, mock(SemanticSearchService.class));

        assertEquals("Previous question", service.history(conversation, "nurse@example.test").getFirst().content());
        assertTrue(service.history(conversation, "other@example.test").isEmpty());
        verify(turns).findTop12ByConversationIdAndOwnerEmailOrderByIdDesc(conversation, "other@example.test");
    }

    @Test
    void reviewPriorityUsesOnlyRecordedIncidentsAndExplainsScore() {
        IncidentRepository incidents = mock(IncidentRepository.class);
        AssessmentRepository assessments = mock(AssessmentRepository.class);
        ResidentRepository residents = mock(ResidentRepository.class);
        ResidentEntity resident = ResidentEntity.builder().id(7L).firstName("Lan").lastName("Nguyen").build();
        when(residents.findByStatusIgnoreCaseAndIsDeletedFalse("ACTIVE")).thenReturn(List.of(resident));
        when(incidents.findRecentWithResident(any(OffsetDateTime.class))).thenReturn(List.of(
                IncidentEntity.builder().id(101L).resident(resident).incidentType(IncidentType.FALL)
                        .occurredAt(OffsetDateTime.now().minusDays(2)).build(),
                IncidentEntity.builder().id(102L).resident(resident).incidentType(IncidentType.MEDICATION_ERROR)
                        .occurredAt(OffsetDateTime.now().minusDays(3)).build()));

        var priority = new ResidentReviewPriorityService(incidents, assessments,
                mock(FallScreeningRepository.class), residents).recentPriorities().getFirst();

        assertEquals(7L, priority.residentId());
        assertEquals(2, priority.incidentCount());
        assertEquals(2, priority.recentIncidentCount());
        assertEquals(6, priority.priorityScore());
        assertEquals("HIGH", priority.priority());
        assertEquals("incident-review-v1", priority.methodVersion());
        assertEquals(List.of(101L, 102L), priority.sourceIncidentIds());
        assertTrue(priority.dataGaps().contains("MORSE_NOT_RECORDED"));
        assertTrue(priority.dataGaps().contains("ASSESSMENT_NOT_RECORDED"));
        assertTrue(priority.factors().stream().anyMatch(f -> f.contains("documented fall")));
        assertTrue(priority.factors().stream().anyMatch(f -> f.contains("No staff-recorded Morse")));
    }

    @Test
    void olderIncidentsRemainVisibleWithConfirmedAdlAsContext() {
        IncidentRepository incidents = mock(IncidentRepository.class);
        AssessmentRepository assessments = mock(AssessmentRepository.class);
        FallScreeningRepository screenings = mock(FallScreeningRepository.class);
        ResidentRepository residents = mock(ResidentRepository.class);
        ResidentEntity resident = ResidentEntity.builder().id(8L).firstName("Mai").lastName("Tran").build();
        when(residents.findByStatusIgnoreCaseAndIsDeletedFalse("ACTIVE")).thenReturn(List.of(resident));
        when(incidents.findRecentWithResident(any(OffsetDateTime.class))).thenReturn(List.of(
                IncidentEntity.builder().resident(resident).incidentType(IncidentType.FALL)
                        .occurredAt(OffsetDateTime.now().minusDays(60)).build()));
        AssessmentEntity assessment = new AssessmentEntity();
        assessment.setId(201L);
        assessment.setStatus("CONFIRMED");
        assessment.setAdlTotalScore(24);
        assessment.setCreatedAt(OffsetDateTime.now().minusDays(65));
        when(assessments.findByResidentIdAndIsCurrentTrue(8L)).thenReturn(Optional.of(assessment));
        FallScreeningEntity screening = new FallScreeningEntity();
        screening.setId(301L);
        screening.setTotalScore(45);
        screening.setAssessedAt(OffsetDateTime.now().minusDays(1));
        when(screenings.findFirstByResidentIdOrderByIdDesc(8L)).thenReturn(Optional.of(screening));

        var priority = new ResidentReviewPriorityService(incidents, assessments,
                screenings, residents).recentPriorities().getFirst();

        assertEquals(1, priority.incidentCount());
        assertEquals(0, priority.recentIncidentCount());
        assertEquals(24, priority.currentAdlScore());
        assertEquals(45, priority.morseScore());
        assertEquals(201L, priority.sourceAssessmentId());
        assertEquals(301L, priority.sourceScreeningId());
        assertTrue(priority.dataGaps().isEmpty());
        assertEquals(1, priority.priorityScore());
        assertTrue(priority.factors().stream().anyMatch(f -> f.contains("days 31–90")));
    }

    @Test
    void activeResidentWithoutIncidentStillAppearsWithRecordedScreening() {
        IncidentRepository incidents = mock(IncidentRepository.class);
        AssessmentRepository assessments = mock(AssessmentRepository.class);
        FallScreeningRepository screenings = mock(FallScreeningRepository.class);
        ResidentRepository residents = mock(ResidentRepository.class);
        ResidentEntity resident = ResidentEntity.builder().id(9L).firstName("An").lastName("Le").build();
        when(residents.findByStatusIgnoreCaseAndIsDeletedFalse("ACTIVE")).thenReturn(List.of(resident));
        FallScreeningEntity screening = new FallScreeningEntity();
        screening.setTotalScore(60);
        screening.setAssessedAt(OffsetDateTime.now());
        when(screenings.findFirstByResidentIdOrderByIdDesc(9L)).thenReturn(Optional.of(screening));

        var priority = new ResidentReviewPriorityService(incidents, assessments,
                screenings, residents).recentPriorities().getFirst();

        assertEquals(0, priority.incidentCount());
        assertEquals(0, priority.priorityScore());
        assertEquals(60, priority.morseScore());
        assertTrue(priority.recommendedAction().contains("No incident-based risk estimate"));
    }

    @Test
    void voiceConfirmationRejectsTaskForAnotherResidentWithoutSaving() {
        AiVoiceObservationRepository observations = mock(AiVoiceObservationRepository.class);
        ResidentRepository residents = mock(ResidentRepository.class);
        CareTaskRepository tasks = mock(CareTaskRepository.class);
        when(residents.findByIdAndIsDeletedFalse(7L)).thenReturn(Optional.of(
                ResidentEntity.builder().id(7L).build()));
        when(tasks.findById(3L)).thenReturn(Optional.of(taskForResident(8L, "nutrition")));
        var service = new VoiceObservationConfirmationService(observations, residents, tasks,
                mock(UserRepository.class), mock(VitalSignRepository.class), new ObjectMapper());
        var request = new VoiceObservationConfirmationService.Confirmation(
                7L, 3L, "Ate half of lunch", VoiceCareParseResponse.builder().dietaryIntakePercentage(50).build());

        assertThrows(IllegalArgumentException.class, () -> service.confirm(request, "nurse@example.test"));
        verify(observations, never()).save(any());
        verify(tasks, never()).save(any());
    }

    @Test
    void voiceConfirmationDoesNotWriteToLockedChart() {
        AiVoiceObservationRepository observations = mock(AiVoiceObservationRepository.class);
        ResidentRepository residents = mock(ResidentRepository.class);
        CareTaskRepository tasks = mock(CareTaskRepository.class);
        VitalSignRepository vitalSigns = mock(VitalSignRepository.class);
        when(residents.findByIdAndIsDeletedFalse(7L)).thenReturn(Optional.of(
                ResidentEntity.builder().id(7L).isChartLocked(true).build()));
        var service = new VoiceObservationConfirmationService(observations, residents, tasks,
                mock(UserRepository.class), vitalSigns, new ObjectMapper());

        assertThrows(IllegalStateException.class, () -> service.confirm(
                new VoiceObservationConfirmationService.Confirmation(7L, null, "BP 120/80",
                        VoiceCareParseResponse.builder().bloodPressureSystolic(120).build()),
                "nurse@example.test"));
        verifyNoInteractions(observations, tasks, vitalSigns);
    }

    @Test
    void voiceConfirmationCannotCompleteMedicationTask() {
        AiVoiceObservationRepository observations = mock(AiVoiceObservationRepository.class);
        ResidentRepository residents = mock(ResidentRepository.class);
        CareTaskRepository tasks = mock(CareTaskRepository.class);
        when(residents.findByIdAndIsDeletedFalse(7L)).thenReturn(Optional.of(
                ResidentEntity.builder().id(7L).build()));
        when(tasks.findById(3L)).thenReturn(Optional.of(taskForResident(7L, "Medication administration")));
        var service = new VoiceObservationConfirmationService(observations, residents, tasks,
                mock(UserRepository.class), mock(VitalSignRepository.class), new ObjectMapper());
        var request = new VoiceObservationConfirmationService.Confirmation(
                7L, 3L, "Medication given", VoiceCareParseResponse.builder().medicationTaken(true).build());

        assertThrows(IllegalArgumentException.class, () -> service.confirm(request, "nurse@example.test"));
        verify(observations, never()).save(any());
        verify(tasks, never()).save(any());
    }

    @Test
    void voiceConfirmationPersistsReviewedObservation() {
        AiVoiceObservationRepository observations = mock(AiVoiceObservationRepository.class);
        ResidentRepository residents = mock(ResidentRepository.class);
        CareTaskRepository tasks = mock(CareTaskRepository.class);
        when(residents.findByIdAndIsDeletedFalse(7L)).thenReturn(Optional.of(
                ResidentEntity.builder().id(7L).build()));
        when(observations.save(any())).thenAnswer(call -> {
            AiVoiceObservationEntity row = call.getArgument(0);
            row.setId(11L);
            return row;
        });
        var service = new VoiceObservationConfirmationService(observations, residents, tasks,
                mock(UserRepository.class), mock(VitalSignRepository.class), new ObjectMapper());
        var request = new VoiceObservationConfirmationService.Confirmation(
                7L, null, "Ate half of lunch", VoiceCareParseResponse.builder().dietaryIntakePercentage(50).build());

        assertEquals(11L, service.confirm(request, "nurse@example.test").observationId());
        verify(observations).save(argThat(row -> row.getResidentId().equals(7L)
                && row.getConfirmedBy().equals("nurse@example.test")
                && row.getParsedJson().contains("50")));
        verifyNoInteractions(tasks);
    }

    @Test
    void confirmedVoiceVitalsAreWrittenToCanonicalChart() {
        AiVoiceObservationRepository observations = mock(AiVoiceObservationRepository.class);
        ResidentRepository residents = mock(ResidentRepository.class);
        CareTaskRepository tasks = mock(CareTaskRepository.class);
        UserRepository users = mock(UserRepository.class);
        VitalSignRepository vitalSigns = mock(VitalSignRepository.class);
        when(residents.findByIdAndIsDeletedFalse(7L)).thenReturn(Optional.of(
                ResidentEntity.builder().id(7L).build()));
        UserEntity nurse = new UserEntity();
        nurse.setId(9L);
        when(users.findByEmailAndIsDeletedFalse("nurse@example.test")).thenReturn(Optional.of(nurse));
        when(observations.save(any())).thenAnswer(call -> {
            AiVoiceObservationEntity row = call.getArgument(0);
            row.setId(11L);
            return row;
        });
        when(vitalSigns.save(any())).thenAnswer(call -> {
            VitalSignEntity row = call.getArgument(0);
            row.setId(12L);
            return row;
        });
        var service = new VoiceObservationConfirmationService(observations, residents, tasks,
                users, vitalSigns, new ObjectMapper());
        var parsed = VoiceCareParseResponse.builder().bloodPressureSystolic(120)
                .bloodPressureDiastolic(80).heartRateBpm(72).bodyTemperatureCelsius(37.0).build();

        var result = service.confirm(new VoiceObservationConfirmationService.Confirmation(
                7L, null, "BP 120 over 80, pulse 72, temperature 37 C", parsed), "nurse@example.test");

        assertEquals(12L, result.vitalSignId());
        verify(vitalSigns).save(argThat(row -> row.getResidentId().equals(7L)
                && row.getRecordedBy().equals(9L)
                && row.getBloodPressureSystolic() == 120
                && row.getTemperatureFahrenheit().toPlainString().equals("98.6")));
    }

    private CareTaskEntity taskForResident(Long residentId, String type) {
        var resident = ResidentEntity.builder().id(residentId).build();
        var plan = new CarePlanSchema();
        plan.setResident(resident);
        var goal = new CareGoalSchema();
        goal.setCarePlan(plan);
        var intervention = new CareInterventionSchema();
        intervention.setCareGoal(goal);
        var task = new CareTaskEntity();
        task.setCareIntervention(intervention);
        task.setTaskType(type);
        task.setStatus(TaskStatus.PENDING);
        return task;
    }
}
