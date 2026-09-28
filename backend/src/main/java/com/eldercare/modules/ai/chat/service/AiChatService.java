package com.eldercare.modules.ai.chat.service;

import com.eldercare.modules.ai.chat.dto.AiChatRequest;
import com.eldercare.modules.ai.chat.dto.AiChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AiChatService {

    private final ChatClient chatClient;

    @Value("${spring.ai.ollama.chat.options.model:qwen3.5:2b-q4_K_M}")
    private String modelName;

    public AiChatResponse chat(AiChatRequest request) {
        try {
            String prompt = request.getMessage();
            if (request.getContext() != null && !request.getContext().isBlank()) {
                prompt = "Ngữ cảnh hồ sơ cư dân / hệ thống:\n" + request.getContext() + "\n\nCâu hỏi: " + request.getMessage();
            }

            String reply = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            return AiChatResponse.builder()
                    .reply(reply)
                    .model(modelName)
                    .success(true)
                    .build();
        } catch (Exception e) {
            log.error("Lỗi khi gọi AI Chat Service: {}", e.getMessage(), e);
            return AiChatResponse.builder()
                    .reply("Xin lỗi, hiện tại hệ thống AI đang bận hoặc không thể kết nối tới mô hình AI (" + e.getMessage() + "). Vui lòng thử lại sau.")
                    .model(modelName)
                    .success(false)
                    .build();
        }
    }
}
