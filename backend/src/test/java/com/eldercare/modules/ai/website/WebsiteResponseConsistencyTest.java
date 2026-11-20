package com.eldercare.modules.ai.website;

import com.eldercare.modules.ai.engine.AiExecutionService;
import com.eldercare.modules.ai.website.dto.CareQuizRequest;
import com.eldercare.modules.ai.website.dto.ScreeningRequest;
import com.eldercare.modules.ai.website.service.AdmissionScreenerAiService;
import com.eldercare.modules.ai.website.service.CareQuizAiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import org.mockito.ArgumentCaptor;

class WebsiteResponseConsistencyTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void screeningNeverAssignsCareLevelOrInventsTimelineFromAiText() {
        AiExecutionService execution = mock(AiExecutionService.class);
        when(execution.executePublic(anyString(), anyString())).thenReturn("""
                {"recommendedService":"Skilled Nursing Care","suggestedCareLevel":"Level 5 — Total Care",
                 "estimatedTimeline":"Tomorrow","rationale":"Mobility support may help.",
                 "nextStep":"Arrange an in-person assessment."}
                """);
        var result = new AdmissionScreenerAiService(execution, mapper)
                .screen(ScreeningRequest.builder().age(78).mobilityStatus("WHEELCHAIR").build());

        assertFalse(result.isFallback());
        assertTrue(result.getSuggestedCareLevel().contains("in-person clinical assessment"));
        assertTrue(result.getEstimatedTimeline().contains("2–5 business days"));
        assertTrue(result.getNextStep().contains("No admission or bed is confirmed"));
    }

    @Test
    void quizRejectsServiceThatConflictsWithSelectedNeed() {
        AiExecutionService execution = mock(AiExecutionService.class);
        when(execution.executePublic(anyString(), anyString())).thenReturn("""
                {"recommendedService":"Skilled Nursing","serviceUrl":"/services/skilled-nursing",
                 "badge":"Suggested Program","headline":"Mobility support","summary":"Discuss care needs.",
                 "keyBenefits":["Dementia-trained staff"],"recommendedAction":"Contact the team."}
                """);
        var result = new CareQuizAiService(execution, mapper)
                .evaluate(CareQuizRequest.builder().primaryNeed("mobility").build());

        assertTrue(result.isFallback());
        assertEquals("Physical Therapy", result.getRecommendedService());
        assertEquals("/services/physical-therapy", result.getServiceUrl());
        assertFalse(result.getKeyBenefits().contains("Dementia-trained staff"));
    }

    @Test
    void quizBenefitsMatchServiceWhenAiSelectsExpectedProgram() {
        AiExecutionService execution = mock(AiExecutionService.class);
        when(execution.executePublic(anyString(), anyString())).thenReturn("""
                {"recommendedService":"Skilled Nursing","serviceUrl":"/services/skilled-nursing",
                 "badge":"Suggested Program","headline":"Nursing support","summary":"Discuss care needs.",
                 "keyBenefits":["Dementia-trained staff"],"recommendedAction":"Contact the team."}
                """);
        var result = new CareQuizAiService(execution, mapper)
                .evaluate(CareQuizRequest.builder().primaryNeed("complex medical and nursing").build());
        assertFalse(result.isFallback());
        assertEquals("Skilled Nursing", result.getRecommendedService());
        assertEquals("Nursing support", result.getKeyBenefits().get(0));
    }

    @Test
    void invalidAiJsonIsLabeledAsFallback() {
        AiExecutionService execution = mock(AiExecutionService.class);
        when(execution.executePublic(anyString(), anyString())).thenReturn("not JSON");
        var result = new AdmissionScreenerAiService(execution, mapper)
                .screen(ScreeningRequest.builder().age(78).mobilityStatus("CANE_WALKER").build());
        assertTrue(result.isFallback());
    }

    @Test
    void navigatorUsesPersonalGoalAndKeepsModelQuestions() {
        AiExecutionService execution = mock(AiExecutionService.class);
        when(execution.executePublic(anyString(), anyString())).thenReturn("""
                {"recommendedService":"Physical Therapy","serviceUrl":"/services/physical-therapy",
                 "headline":"Discuss safer walking","summary":"You use a walking aid and want to walk safely to meals.",
                 "questionsForTeam":["What mobility assessment is needed?","Which walking goals can be reviewed?"]}
                """);
        var result = new CareQuizAiService(execution, mapper).evaluate(CareQuizRequest.builder()
                .primaryNeed("Rehabilitation and mobility support")
                .mobility("Uses a walking aid")
                .symptomsOrGoals(java.util.List.of("walk safely to meals")).build());

        ArgumentCaptor<String> prompt = ArgumentCaptor.forClass(String.class);
        verify(execution).executePublic(anyString(), prompt.capture());
        assertTrue(prompt.getValue().contains("walk safely to meals"));
        assertFalse(result.isFallback());
        assertEquals(2, result.getQuestionsForTeam().size());
    }

    @Test
    void screenerFallbackReflectsSelectedRehabilitationNeed() {
        AiExecutionService execution = mock(AiExecutionService.class);
        when(execution.executePublic(anyString(), anyString())).thenThrow(new IllegalStateException("offline"));
        var result = new AdmissionScreenerAiService(execution, mapper).screen(ScreeningRequest.builder()
                .mobilityStatus("CANE_WALKER")
                .medicalConditions(java.util.List.of("Post-Stroke Recovery"))
                .dailyNeedsHelp(java.util.List.of("BATHING")).build());
        assertTrue(result.isFallback());
        assertEquals("Physical Therapy", result.getRecommendedService());
        assertTrue(result.getRationale().contains("Post-Stroke Recovery"));
    }

    @Test
    void screenerRejectsMedicationClaimAbsentFromAnswers() {
        AiExecutionService execution = mock(AiExecutionService.class);
        when(execution.executePublic(anyString(), anyString())).thenReturn("""
                {"recommendedService":"Physical Therapy","rationale":"Post-stroke recovery and medication management require a therapy plan."}
                """);
        var result = new AdmissionScreenerAiService(execution, mapper).screen(ScreeningRequest.builder()
                .mobilityStatus("CANE_WALKER")
                .medicalConditions(java.util.List.of("Post-Stroke Recovery"))
                .dailyNeedsHelp(java.util.List.of("BATHING")).build());

        assertTrue(result.isFallback());
        assertFalse(result.getRationale().toLowerCase().contains("medication"));
    }
}
