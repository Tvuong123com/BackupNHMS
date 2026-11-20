package com.eldercare.modules.ai;

import com.eldercare.modules.ai.engine.*;
import com.eldercare.modules.ai.careplan.service.CarePlanAiService;
import com.eldercare.modules.ai.careplan.dto.CarePlanSuggestionRequest;
import com.eldercare.modules.resident_intake.resident.repository.*;
import com.eldercare.modules.resident_intake.resident_profile.ResidentEntity;
import com.eldercare.modules.resident_intake.assessment.repository.AssessmentRepository;
import com.eldercare.modules.risk_incident.incident_tracking.repository.IncidentRepository;
import com.eldercare.modules.risk_incident.incident_tracking.entity.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.ai.chat.client.ChatClient;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@EnabledIfEnvironmentVariable(named = "NHMS_LIVE_AI_CHECK", matches = "true")
class LiveCarePlanAiCheckTest {
    @Test void configuredAiGeneratesEditableGoalsFromSyntheticChartOnly() throws Exception {
        assertTrue(Files.exists(Path.of("ai-settings.json")), "Use the already-configured local provider settings");
        var mapper = new ObjectMapper();
        var settings = new AiSettingsService(mapper); settings.init();
        var knowledge = mock(ElderCareClinicalKnowledgeBase.class); when(knowledge.getGroundingContext(anyString())).thenReturn("");
        var execution = new AiExecutionService(mock(ChatClient.class), settings, mapper, knowledge);
        var residents = mock(ResidentRepository.class);
        when(residents.findByIdAndIsDeletedFalse(900001L)).thenReturn(Optional.of(ResidentEntity.builder().id(900001L)
                .firstName("Synthetic").lastName("Test Resident").dateOfBirth(LocalDate.of(1940, 1, 1)).status("ACTIVE").build()));
        var incidents = mock(IncidentRepository.class);
        when(incidents.findTop5ByResidentIdOrderByOccurredAtDesc(900001L)).thenReturn(List.of(IncidentEntity.builder()
                .incidentType(IncidentType.FALL).description("SYNTHETIC SOFTWARE TEST: Staff documented a need for assistance with transfers. No real patient record is supplied.").build()));
        var service = new CarePlanAiService(execution, mapper, residents, mock(AssessmentRepository.class), incidents, mock(ClinicalRecordRepository.class));
        var result = service.suggestCarePlan(CarePlanSuggestionRequest.builder().residentId(900001L).build());
        assertEquals("AI", result.getGenerationMethod()); assertFalse(result.getSuggestedGoals().isEmpty());
        assertTrue(result.getSuggestedGoals().stream().allMatch(goal -> !goal.getInterventions().isEmpty()));
        var report = Map.of("syntheticChartOnly", true, "databaseWrites", false, "provider", settings.getPublicSettings().getProvider(), "result", result);
        Files.writeString(Path.of("..", "deliverables", "nhms-careplan-live-ai-check.json"), mapper.writerWithDefaultPrettyPrinter().writeValueAsString(report));
    }
}
