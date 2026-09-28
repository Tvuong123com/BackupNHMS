package com.eldercare.modules.ai.elopement.service;

import com.eldercare.modules.ai.elopement.dto.ElopementRiskRequest;
import com.eldercare.modules.ai.elopement.dto.ElopementRiskResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ElopementRiskAiService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    private static final String ELOPEMENT_SYSTEM_PROMPT = """
            Bạn là Chuyên gia Tâm thần Lão khoa & An toàn Khu Chăm sóc Trí nhớ (Memory Care Safety Director).
            Hãy đánh giá nguy cơ bỏ trốn hoặc đi lạc (Elopement / Wandering Risk) của cư dân dựa trên điểm nhận thức và hành vi gần đây.
            
            Trả về DUY NHẤT một chuỗi JSON hợp lệ theo cấu trúc sau (không kèm văn bản ngoài):
            {
              "riskLevel": "LOW | MODERATE | HIGH | CRITICAL",
              "riskScore": 8.5,
              "triggerDoorLockAlert": true | false,
              "behavioralAnalysis": "Phân tích trạng thái tâm lý và xu hướng hành vi của cư dân",
              "immediateSafeguards": ["Biện pháp an toàn 1", "Biện pháp an toàn 2"]
            }
            """;

    public ElopementRiskResponse evaluateRisk(ElopementRiskRequest request) {
        try {
            StringBuilder prompt = new StringBuilder();
            prompt.append("Cư dân: ").append(request.getResidentName() != null ? request.getResidentName() : "Cư dân")
                    .append(", Phòng: ").append(request.getRoomNumber() != null ? request.getRoomNumber() : "Chưa rõ").append("\n");
            prompt.append("Điểm nhận thức (MMSE/30): ").append(request.getCognitiveScore() != null ? request.getCognitiveScore() : 20).append("\n");
            prompt.append("Chẩn đoán sa sút trí tuệ: ").append(Boolean.TRUE.equals(request.getHasDementiaDiagnosis()) ? "Có" : "Không").append("\n");
            if (request.getRecentBehaviors() != null && !request.getRecentBehaviors().isEmpty()) {
                prompt.append("Hành vi ghi nhận gần đây: ").append(String.join(", ", request.getRecentBehaviors())).append("\n");
            }
            if (request.getTimeOfDay() != null) {
                prompt.append("Thời điểm: ").append(request.getTimeOfDay()).append("\n");
            }

            String raw = chatClient.prompt()
                    .system(ELOPEMENT_SYSTEM_PROMPT)
                    .user(prompt.toString())
                    .call()
                    .content();

            log.info("AI Elopement risk evaluation raw response: {}", raw);
            return parseJsonResponse(raw, request);
        } catch (Exception e) {
            log.error("Lỗi khi đánh giá nguy cơ đi lạc bằng AI: {}", e.getMessage(), e);
            return createFallbackResponse(request);
        }
    }

    private ElopementRiskResponse parseJsonResponse(String raw, ElopementRiskRequest request) {
        try {
            String json = raw.trim();
            if (json.contains("```json")) {
                int start = json.indexOf("```json") + 7;
                int end = json.indexOf("```", start);
                if (end > start) json = json.substring(start, end).trim();
            } else if (json.contains("```")) {
                int start = json.indexOf("```") + 3;
                int end = json.indexOf("```", start);
                if (end > start) json = json.substring(start, end).trim();
            }

            return objectMapper.readValue(json, ElopementRiskResponse.class);
        } catch (Exception e) {
            log.warn("Không parse được JSON từ AI elopement eval, dùng fallback: {}", e.getMessage());
            return createFallbackResponse(request);
        }
    }

    private ElopementRiskResponse createFallbackResponse(ElopementRiskRequest request) {
        int score = request.getCognitiveScore() != null ? request.getCognitiveScore() : 24;
        boolean hasDementia = Boolean.TRUE.equals(request.getHasDementiaDiagnosis());
        List<String> behaviors = request.getRecentBehaviors() != null ? request.getRecentBehaviors() : new ArrayList<>();

        boolean wanderingSignals = behaviors.stream().anyMatch(b ->
                b.toLowerCase().contains("cửa") || b.toLowerCase().contains("chìa khóa") ||
                b.toLowerCase().contains("về nhà") || b.toLowerCase().contains("đi lạc"));

        String level = "LOW";
        double riskScore = 3.0;
        boolean doorLock = false;
        List<String> safeguards = new ArrayList<>();

        if (wanderingSignals || (hasDementia && score < 18)) {
            level = "HIGH";
            riskScore = 8.5;
            doorLock = true;
            safeguards.add("Gắn vòng đeo tay định vị RFID chuyên dụng cho khu Memory Care.");
            safeguards.add("Tăng tần suất kiểm tra vị trí phòng mỗi 30 phút.");
            safeguards.add("Thông báo cho nhân viên lễ tân và trực cửa an ninh.");
        } else if (hasDementia || score < 22) {
            level = "MODERATE";
            riskScore = 5.5;
            safeguards.add("Duy trì giám sát trực quan định kỳ.");
            safeguards.add("Hướng dẫn cư dân tham gia các hoạt động định hướng tại phòng sinh hoạt chung.");
        } else {
            safeguards.add("Tiếp tục theo dõi các chỉ số sinh hoạt bình thường.");
        }

        return ElopementRiskResponse.builder()
                .riskLevel(level)
                .riskScore(riskScore)
                .triggerDoorLockAlert(doorLock)
                .behavioralAnalysis("Đánh giá dựa trên điểm nhận thức MMSE (" + score + "/30) và các biểu hiện hành vi ghi nhận.")
                .immediateSafeguards(safeguards)
                .build();
    }
}
