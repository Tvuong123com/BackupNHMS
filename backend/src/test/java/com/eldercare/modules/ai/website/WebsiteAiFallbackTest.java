package com.eldercare.modules.ai.website;

import com.eldercare.modules.ai.engine.AiExecutionService;
import com.eldercare.modules.ai.website.dto.CareQuizRequest;
import com.eldercare.modules.ai.website.dto.FaqSearchRequest;
import com.eldercare.modules.ai.website.dto.MemoryRiskRequest;
import com.eldercare.modules.ai.website.dto.ScreeningRequest;
import com.eldercare.modules.ai.website.dto.WebsiteChatRequest;
import com.eldercare.modules.ai.website.service.AdmissionScreenerAiService;
import com.eldercare.modules.ai.website.service.CareQuizAiService;
import com.eldercare.modules.ai.website.service.MemoryRiskAiService;
import com.eldercare.modules.ai.website.service.WebsiteChatService;
import com.eldercare.modules.ai.website.service.WebsiteFaqAiService;
import com.eldercare.modules.ai.website.service.WebsitePricingKnowledge;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WebsiteAiFallbackTest {

    private AiExecutionService executionService;
    private ObjectMapper objectMapper;
    private com.eldercare.modules.ai.website.service.WebsiteKnowledgeBase websiteKnowledgeBase;

    @BeforeEach
    void providerIsUnavailable() {
        executionService = mock(AiExecutionService.class);
        objectMapper = new ObjectMapper();
        websiteKnowledgeBase = new com.eldercare.modules.ai.website.service.WebsiteKnowledgeBase(objectMapper, mock(WebsitePricingKnowledge.class));
        when(executionService.executePublic(anyString(), anyString()))
                .thenThrow(new IllegalStateException("Provider unavailable"));
    }

    @Test
    void everyWebsiteFeatureMarksRuleBasedAnswersAsFallback() {
        WebsiteChatRequest chat = WebsiteChatRequest.builder().message("Hello").build();
        FaqSearchRequest faq = FaqSearchRequest.builder().question("How does admission work?").build();
        ScreeningRequest screen = ScreeningRequest.builder().age(80).build();
        CareQuizRequest quiz = CareQuizRequest.builder().primaryNeed("memory").build();
        MemoryRiskRequest memory = MemoryRiskRequest.builder().observedSigns(List.of("forgetful")).build();

        assertTrue(new WebsiteChatService(executionService, websiteKnowledgeBase).chat(chat).isFallback());
        assertTrue(new WebsiteFaqAiService(executionService, websiteKnowledgeBase).answerFaq(faq).isFallback());
        assertTrue(new AdmissionScreenerAiService(executionService, objectMapper).screen(screen).isFallback());
        assertTrue(new CareQuizAiService(executionService, objectMapper).evaluate(quiz).isFallback());
        assertTrue(new MemoryRiskAiService(executionService, objectMapper).explain(memory).isFallback());
    }
}
