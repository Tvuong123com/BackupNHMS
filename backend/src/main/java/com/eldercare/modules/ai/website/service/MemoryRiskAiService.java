package com.eldercare.modules.ai.website.service;

import com.eldercare.modules.ai.website.dto.MemoryRiskRequest;
import com.eldercare.modules.ai.website.dto.MemoryRiskResponse;
import com.eldercare.modules.ai.engine.AiExecutionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AI Memory Care Risk Explainer Service.
 * Helps families understand observed memory and cognitive signs with compassion and clinical grounding.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MemoryRiskAiService {

    private final AiExecutionService aiExecutionService;
    private final ObjectMapper objectMapper;

    private static final String MEMORY_SYSTEM_PROMPT = """
            You are the ElderCare Memory Care Educational Advisor AI.
            Based on the cognitive signs observed by a family member, provide a compassionate, educational explanation.
            DO NOT diagnose a medical condition. Use terms like 'may suggest', 'common in', and emphasize consulting a neurologist or geriatrician.

            Return ONLY a valid JSON object matching this structure:
            {
              "estimatedCategory": "Mild Cognitive Signs | Moderate Memory Care Consideration | Advanced Support Needs",
              "clinicalPerspective": "A gentle, 2-3 sentence overview explaining what these signs typically represent.",
              "homeStrategies": [
                "Establish predictable daily routines to reduce disorientation",
                "Use gentle redirection rather than correcting forgotten details",
                "Ensure safety modifications like nightlights and clear walking paths"
              ],
              "facilitySupportPlan": "How our Memory Care program provides structured, dignified environments tailored to these needs.",
              "recommendedNextStep": "We recommend an evaluation with a physician and invite you to consult with our Memory Care Director."
            }
            """;

    public MemoryRiskResponse explain(MemoryRiskRequest request) {
        try {
            StringBuilder prompt = new StringBuilder();
            prompt.append("Observed cognitive signs:\n");
            if (request.getObservedSigns() != null && !request.getObservedSigns().isEmpty()) {
                prompt.append("- Signs: ").append(String.join(", ", request.getObservedSigns())).append("\n");
            }
            if (request.getDuration() != null) {
                prompt.append("- Duration: ").append(request.getDuration()).append("\n");
            }
            if (request.getAdditionalObservations() != null && !request.getAdditionalObservations().isBlank()) {
                prompt.append("- Family notes: ").append(request.getAdditionalObservations()).append("\n");
            }

            String raw = aiExecutionService.executePublic(MEMORY_SYSTEM_PROMPT, prompt.toString());
            String json = raw.trim();
            if (json.contains("{")) {
                json = json.substring(json.indexOf('{'), json.lastIndexOf('}') + 1);
            }
            MemoryRiskResponse parsed = objectMapper.readValue(json, MemoryRiskResponse.class);
            parsed.setSuccess(true);
            return parsed;
        } catch (Exception e) {
            log.error("AI Memory Risk error: {}", e.getMessage(), e);
            return fallbackResponse(request);
        }
    }

    private MemoryRiskResponse fallbackResponse(MemoryRiskRequest request) {
        int count = request.getObservedSigns() != null ? request.getObservedSigns().size() : 0;
        String category = count > 3 ? "Moderate Memory Care Consideration" : "Early Cognitive Changes";

        return MemoryRiskResponse.builder()
                .estimatedCategory(category)
                .clinicalPerspective("The signs you've observed are common patterns families notice in changing cognitive health. Recognizing them early allows for supportive strategies that preserve dignity and comfort.")
                .homeStrategies(List.of(
                        "Maintain consistent daily routines and visual reminders",
                        "Simplify communication using clear, encouraging one-step statements",
                        "Keep living spaces well-lit and free of tripping hazards"
                ))
                .facilitySupportPlan("Our Memory Care wing features secure sensory gardens, specialized dementia-certified staff, and individualized cognitive engagement programs designed to foster a sense of belonging and calm.")
                .recommendedNextStep("We recommend consulting with your loved one's primary care physician, and we invite your family to tour our Memory Care wing to see our personalized programs in action.")
                .success(true)
                .fallback(true)
                .build();
    }
}
