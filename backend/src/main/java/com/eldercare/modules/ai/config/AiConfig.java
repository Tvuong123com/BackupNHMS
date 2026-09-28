package com.eldercare.modules.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultSystem("Bạn là trợ lý trí tuệ nhân tạo chuyên sâu trong Hệ thống Quản lý Viện dưỡng lão và Chăm sóc Sức khỏe Người cao tuổi (ElderCare NHMS). " +
                        "Nhiệm vụ của bạn là hỗ trợ nhân viên y tế, điều dưỡng và ban quản trị trong việc đánh giá an toàn, phân loại sự cố, và hỗ trợ chăm sóc cư dân. " +
                        "Luôn đảm bảo câu trả lời chuẩn xác, văn phong chuyên nghiệp, tôn trọng người cao tuổi và tuân thủ các nguyên tắc bảo mật thông tin y tế.")
                .build();
    }
}
