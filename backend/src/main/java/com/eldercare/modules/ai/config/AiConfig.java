package com.eldercare.modules.ai.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Value("${spring.ai.ollama.base-url:http://localhost:11434}")
    private String baseUrl;

    @Value("${spring.ai.ollama.chat.options.model:qwen3.5:2b-q4_K_M}")
    private String modelName;

    @Bean
    public ChatModel chatModel() {
        OllamaApi ollamaApi = new OllamaApi(baseUrl);
        return OllamaChatModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(OllamaOptions.builder()
                        .model(modelName)
                        .temperature(0.2)
                        .numPredict(512)
                        .build())
                .build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultSystem("Bạn là trợ lý trí tuệ nhân tạo chuyên sâu trong Hệ thống Quản lý Viện dưỡng lão và Chăm sóc Sức khỏe Người cao tuổi (ElderCare NHMS). " +
                        "Nhiệm vụ của bạn là hỗ trợ nhân viên y tế, điều dưỡng và ban quản trị trong việc đánh giá an toàn, phân loại sự cố, và hỗ trợ chăm sóc cư dân. " +
                        "Luôn đảm bảo câu trả lời chuẩn xác, văn phong chuyên nghiệp, tôn trọng người cao tuổi và tuân thủ các nguyên tắc bảo mật thông tin y tế.")
                .build();
    }
}


