package com.eldercare.modules.ai.engine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClinicalKnowledgeSafetyTest {
    @Test
    void fallReminderDoesNotPretendToBeAnApprovedProtocol() {
        String guidance = new ElderCareClinicalKnowledgeBase().getGroundingContext("unwitnessed fall");
        assertTrue(guidance.contains("not an approved clinical protocol"));
        assertTrue(guidance.contains("licensed clinician"));
        assertFalse(guidance.contains("q15m"));
        assertFalse(guidance.contains("72-hour"));
    }
}
