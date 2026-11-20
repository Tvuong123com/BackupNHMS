package com.eldercare.modules.ai.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class AiExecutionPromptTest {

    @Test
    void publicWebsitePromptDoesNotIncludeInternalRulesOrClinicalGrounding() {
        ElderCareClinicalKnowledgeBase knowledgeBase = mock(ElderCareClinicalKnowledgeBase.class);
        AiExecutionService service = new AiExecutionService(
                mock(ChatClient.class), mock(AiSettingsService.class), mock(ObjectMapper.class), knowledgeBase);
        AiSettingsDto settings = AiSettingsDto.builder().systemRules("INTERNAL_RULES").build();

        String publicPrompt = service.buildSystemPrompt(settings, "FAMILY_CONCIERGE", "fall", false);
        assertTrue(publicPrompt.contains("FAMILY_CONCIERGE"));
        assertFalse(publicPrompt.contains("INTERNAL_RULES"));
        verifyNoInteractions(knowledgeBase);

        when(knowledgeBase.getGroundingContext("fall")).thenReturn("CLINICAL_PROTOCOL");
        String staffPrompt = service.buildSystemPrompt(settings, "STAFF_ASSISTANT", "fall", true);
        assertTrue(staffPrompt.contains("INTERNAL_RULES"));
        assertTrue(staffPrompt.contains("CLINICAL_PROTOCOL"));
        assertTrue(staffPrompt.contains("not approved clinical protocols"));
    }
}
