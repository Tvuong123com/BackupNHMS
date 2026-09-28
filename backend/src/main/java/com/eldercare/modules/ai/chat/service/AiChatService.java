package com.eldercare.modules.ai.chat.service;

import com.eldercare.modules.ai.chat.dto.AiChatRequest;
import com.eldercare.modules.ai.chat.dto.AiChatResponse;
import com.eldercare.modules.ai.engine.AiExecutionService;
import com.eldercare.modules.ai.engine.AiSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AiChatService {

    private final AiExecutionService aiExecutionService;
    private final AiSettingsService settingsService;

    public AiChatResponse chat(AiChatRequest request) {
        try {
            String prompt = request.getMessage();
            if (request.getContext() != null && !request.getContext().isBlank()) {
                prompt = "Resident / Facility Context:\n" + request.getContext() + "\n\nQuestion: " + request.getMessage();
            }

            String reply = aiExecutionService.execute(null, prompt);

            String activeModel = settingsService.getSettings().getProvider().equals("GOOGLE_GEMINI")
                    ? settingsService.getSettings().getGeminiModel()
                    : settingsService.getSettings().getOllamaModel();

            return AiChatResponse.builder()
                    .reply(reply)
                    .model(activeModel)
                    .success(true)
                    .build();
        } catch (Exception e) {
            log.error("AI Chat error: {}", e.getMessage(), e);
            return AiChatResponse.builder()
                    .reply("The AI assistant encountered an error (" + e.getMessage() + "). Please verify AI settings or try again.")
                    .model("unknown")
                    .success(false)
                    .build();
        }
    }
}
