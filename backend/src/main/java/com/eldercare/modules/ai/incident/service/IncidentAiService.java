package com.eldercare.modules.ai.incident.service;

import com.eldercare.modules.ai.incident.dto.IncidentAnalysisRequest;
import com.eldercare.modules.ai.incident.dto.IncidentAnalysisResponse;
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
public class IncidentAiService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    private static final String CLASSIFY_SYSTEM_PROMPT = """
            Bạn là chuyên gia thẩm định và phân loại sự cố y tế trong viện dưỡng lão (Nursing Home Incident Specialist).
            Dựa trên mô tả sự cố được cung cấp, hãy phân tích và trả về DUY NHẤT một chuỗi JSON hợp lệ (không kèm theo bất kỳ văn bản giải thích nào ngoài JSON) theo đúng cấu trúc sau:
            {
              "suggestedSeverity": "LOW | MEDIUM | HIGH | CRITICAL | EMERGENCY",
              "suggestedIncidentType": "FALL | MEDICATION_ERROR | ELOPEMENT | BEHAVIORAL | MEDICAL_EMERGENCY | INJURY | OTHER",
              "rationale": "Lý do ngắn gọn vì sao phân loại mức độ và loại sự cố này",
              "recommendedActions": ["Hành động 1", "Hành động 2", "Hành động 3"],
              "confidence": 0.95
            }

            Quy tắc xác định Severity:
            - EMERGENCY: Đe dọa tính mạng ngay lập tức, ngưng tim, chấn thương sọ não, bất tỉnh.
            - CRITICAL: Gãy xương, vết thương rách sâu cần cấp cứu ngoại viện, biến cố tim mạch.
            - HIGH: Té ngã có xây xát, nhầm thuốc nhưng chưa nguy kịch, đi lạc khỏi khuôn viên.
            - MEDIUM: Va chạm nhẹ, trầy da nhỏ, tranh cãi kích động nhẹ.
            - LOW: Sự cố nhỏ không gây thương tích hoặc ảnh hưởng sức khỏe.
            """;

    public IncidentAnalysisResponse classifyIncident(IncidentAnalysisRequest request) {
        try {
            StringBuilder userPrompt = new StringBuilder();
            userPrompt.append("Mô tả sự cố: ").append(request.getDescription()).append("\n");
            if (request.getResidentInfo() != null && !request.getResidentInfo().isBlank()) {
                userPrompt.append("Thông tin cư dân liên quan: ").append(request.getResidentInfo()).append("\n");
            }
            if (request.getLocation() != null && !request.getLocation().isBlank()) {
                userPrompt.append("Địa điểm xảy ra: ").append(request.getLocation()).append("\n");
            }

            String rawResponse = chatClient.prompt()
                    .system(CLASSIFY_SYSTEM_PROMPT)
                    .user(userPrompt.toString())
                    .call()
                    .content();

            log.info("AI raw classification response: {}", rawResponse);

            return parseJsonResponse(rawResponse);
        } catch (Exception e) {
            log.error("Lỗi khi phân loại sự cố bằng AI: {}", e.getMessage(), e);
            return createFallbackResponse(request.getDescription());
        }
    }

    private IncidentAnalysisResponse parseJsonResponse(String rawResponse) {
        try {
            String json = rawResponse.trim();
            // Trích xuất JSON nếu LLM trả về markdown block ```json ... ```
            if (json.contains("```json")) {
                int start = json.indexOf("```json") + 7;
                int end = json.indexOf("```", start);
                if (end > start) {
                    json = json.substring(start, end).trim();
                }
            } else if (json.contains("```")) {
                int start = json.indexOf("```") + 3;
                int end = json.indexOf("```", start);
                if (end > start) {
                    json = json.substring(start, end).trim();
                }
            }

            return objectMapper.readValue(json, IncidentAnalysisResponse.class);
        } catch (Exception e) {
            log.warn("Không thể parse trực tiếp JSON từ AI response, áp dụng fallback phân tích cơ bản: {}", e.getMessage());
            return createFallbackResponse(rawResponse);
        }
    }

    private IncidentAnalysisResponse createFallbackResponse(String text) {
        String lower = text != null ? text.toLowerCase() : "";
        String severity = "MEDIUM";
        String type = "OTHER";
        List<String> actions = new ArrayList<>();

        if (lower.contains("bất tỉnh") || lower.contains("ngưng thở") || lower.contains("cấp cứu") || lower.contains("nguy kịch")) {
            severity = "EMERGENCY";
            type = "MEDICAL_EMERGENCY";
            actions.add("Gọi ngay cấp cứu 115 / ER");
            actions.add("Thông báo Bác sĩ trực và Giám đốc điều dưỡng (DON)");
        } else if (lower.contains("ngã") || lower.contains("té") || lower.contains("fall")) {
            severity = "HIGH";
            type = "FALL";
            actions.add("Kiểm tra chấn thương đầu và khớp của cư dân");
            actions.add("Đo lại chỉ số sinh tồn (vitals) ngay lập tức");
            actions.add("Báo cáo cho Y tá trưởng và gia đình");
        } else if (lower.contains("thuốc") || lower.contains("medication")) {
            severity = "HIGH";
            type = "MEDICATION_ERROR";
            actions.add("Kiểm tra lại hồ sơ eMAR và đơn thuốc");
            actions.add("Theo dõi dấu hiệu phản ứng thuốc trong 4 giờ tới");
        } else if (lower.contains("đi lạc") || lower.contains("trốn") || lower.contains("elopement")) {
            severity = "CRITICAL";
            type = "ELOPEMENT";
            actions.add("Kích hoạt quy trình tìm kiếm khẩn cấp trong toàn viện");
            actions.add("Kiểm tra camera an ninh và báo cho bảo vệ các cổng");
        } else {
            actions.add("Theo dõi tình trạng cư dân và ghi nhận diễn biến tiếp theo");
        }

        return IncidentAnalysisResponse.builder()
                .suggestedSeverity(severity)
                .suggestedIncidentType(type)
                .rationale("Phân tích dựa trên các từ khóa an toàn lâm sàng từ mô tả sự cố.")
                .recommendedActions(actions)
                .confidence(0.80)
                .build();
    }
}
