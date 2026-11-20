package com.eldercare.modules.ai.chat.service;

import com.eldercare.modules.ai.chat.dto.AiChatRequest;
import com.eldercare.modules.ai.chat.dto.AiChatResponse;
import com.eldercare.modules.ai.engine.AiExecutionService;
import com.eldercare.modules.ai.engine.AiSettingsService;
import com.eldercare.modules.ai.engine.ElderCareClinicalKnowledgeBase;
import com.eldercare.modules.ai.chat.entity.AiChatTurnEntity;
import com.eldercare.modules.ai.chat.repository.AiChatTurnRepository;
import com.eldercare.modules.ai.rag.service.SemanticSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.Comparator;

/**
 * Clinical AI Chat Service for NHMS staff (Nurses, DON, CNAs).
 * Uses staff workflow reminders when a model is unavailable.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AiChatService {

    private static final String CLINICAL_STAFF_SYSTEM_PROMPT = """
            You are the NHMS Clinical & Staff Care Assistant (Geriatric Nursing & Clinical Protocol Specialist).
            Your mission is to provide accurate, evidence-based geriatric nursing guidance, clinical protocols (CDC STEADI Fall Prevention, Morse Fall Scale, AGS Beers Criteria 2023, Braden Pressure Injury Scale, SBAR Handover), and operational assistance to facility staff (Nurses, CNAs, DON, Care Coordinators).
            
            Guidelines:
            1. For clinical questions (such as fall prevention, pressure ulcer care, medication safety, dementia agitation, infection control, shift handover):
               - Always provide clear, structured, evidence-based nursing interventions and practical steps.
               - Support the staff member with actionable strategies (e.g. bed alarms, low beds, non-skid socks, environmental checks, scheduled toileting, hydration, PT balance assessment).
               - Mention adherence to facility policies and physician-approved individual resident care plans as an integral professional practice.
            2. When retrieved records or resident chart context are provided:
               - Incorporate those chart details into your response.
            3. Language:
               - Reply in the same language as the user's message (Vietnamese if asked in Vietnamese, English if asked in English).
            """;

    private final AiExecutionService aiExecutionService;
    private final AiSettingsService settingsService;
    private final ElderCareClinicalKnowledgeBase clinicalKnowledgeBase;
    private final AiChatTurnRepository chatTurnRepository;
    private final SemanticSearchService semanticSearchService;

    public record ChatTurn(String role, String content) {}

    public List<ChatTurn> history(String conversationId, String ownerEmail) {
        UUID.fromString(conversationId);
        return chatTurnRepository.findTop12ByConversationIdAndOwnerEmailOrderByIdDesc(conversationId, ownerEmail)
                .stream().sorted(Comparator.comparing(AiChatTurnEntity::getId))
                .map(t -> new ChatTurn(t.getRole(), t.getContent())).toList();
    }

    public AiChatResponse chat(AiChatRequest request, String ownerEmail) {
        return chat(request, ownerEmail, false);
    }

    public AiChatResponse chat(AiChatRequest request, String ownerEmail, boolean canReadResidentRecords) {
        if (request.getMessage() == null || request.getMessage().isBlank()
                || request.getMessage().length() > 500) {
            throw new IllegalArgumentException("Message must contain 1â€“500 characters");
        }
        String conversationId = request.getConversationId() == null || request.getConversationId().isBlank()
                ? UUID.randomUUID().toString() : UUID.fromString(request.getConversationId()).toString();
        List<ChatTurn> prior = history(conversationId, ownerEmail);
        StringBuilder context = new StringBuilder("Earlier turns from this staff member (use only as conversation context, not verified chart data):\n");
        for (ChatTurn turn : prior) context.append(turn.role()).append(": ").append(turn.content()).append("\n");
        if (request.getContext() != null && !request.getContext().isBlank()) {
            context.append("Additional user context (unverified): ").append(request.getContext());
        }
        List<String> sources = new java.util.ArrayList<>();
        try {
            var hits = canReadResidentRecords
                    ? semanticSearchService.search(request.getMessage(), null, 3)
                    : semanticSearchService.searchWorkflow(request.getMessage());
            if (!hits.isEmpty()) {
                context.append("\nRetrieved Clinical & Facility Records (factual groundings):\n");
                for (var hit : hits) {
                    String source = hit.sourceType() + "/" + hit.sourceId();
                    sources.add(source);
                    context.append("[").append(source).append("] ").append(hit.excerpt()).append("\n");
                }
            }
        } catch (Exception e) {
            log.debug("Semantic workflow retrieval unavailable: {}", e.getClass().getSimpleName());
        }
        AiChatRequest withHistory = AiChatRequest.builder()
                .message(request.getMessage()).context(context.toString()).build();
        AiChatResponse response = chat(withHistory);
        saveTurn(conversationId, ownerEmail, "user", request.getMessage());
        saveTurn(conversationId, ownerEmail, "assistant", response.getReply());
        response.setConversationId(conversationId);
        response.setSources("ElderCare-Workflow-Fallback".equals(response.getModel()) ? List.of() : sources);
        return response;
    }

    private void saveTurn(String conversationId, String ownerEmail, String role, String content) {
        AiChatTurnEntity turn = new AiChatTurnEntity();
        turn.setConversationId(conversationId);
        turn.setOwnerEmail(ownerEmail);
        turn.setRole(role);
        turn.setContent(content);
        chatTurnRepository.save(turn);
    }

    public AiChatResponse chat(AiChatRequest request) {
        String userMsg = request.getMessage() != null ? request.getMessage().trim() : "";

        try {
            String prompt = userMsg;
            if (request.getContext() != null && !request.getContext().isBlank()) {
                prompt = "Resident / Facility Context:\n" + request.getContext() + "\n\nQuestion: " + userMsg;
            }

            String reply = aiExecutionService.execute(CLINICAL_STAFF_SYSTEM_PROMPT, prompt);

            String activeModel = "GOOGLE_GEMINI".equalsIgnoreCase(settingsService.getSettings().getProvider())
                    ? settingsService.getSettings().getGeminiModel()
                    : settingsService.getSettings().getOllamaModel();

            return AiChatResponse.builder()
                    .reply(reply)
                    .model(activeModel)
                    .success(true)
                    .build();
        } catch (Exception e) {
            log.warn("AI Engine execution failed ({}), using staff workflow reminder", e.getMessage());

            // Workflow fallback. These reminders are not approved clinical protocols.
            Optional<ElderCareClinicalKnowledgeBase.ClinicalProtocol> match =
                    clinicalKnowledgeBase.findMatchingKnowledge(userMsg);

            if (match.isPresent()) {
                ElderCareClinicalKnowledgeBase.ClinicalProtocol p = match.get();
                return AiChatResponse.builder()
                        .reply(p.getStructuredAnswer())
                        .model("ElderCare-Workflow-Fallback")
                        .success(true)
                        .build();
            }

            // General escalation guidance if no workflow entry matches.
            String fallbackClinicalReply = """
                    I cannot verify a clinical protocol while the AI service is unavailable.
                    Follow your facility's current approved policy and the resident's care plan.
                    For urgent concerns, use the emergency escalation process and contact the responsible licensed clinician.
                    Document observed facts and actions in the resident record.
                    """;

            return AiChatResponse.builder()
                    .reply(fallbackClinicalReply)
                    .model("ElderCare-Workflow-Fallback")
                    .success(true)
                    .build();
        }
    }
}
