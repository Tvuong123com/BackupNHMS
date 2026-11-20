package com.eldercare.modules.ai.incident.service;

import com.eldercare.modules.ai.incident.dto.IncidentAnalysisRequest;
import com.eldercare.modules.ai.incident.dto.IncidentAnalysisResponse;
import com.eldercare.modules.ai.engine.AiExecutionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class IncidentAiService {

    private final AiExecutionService aiExecutionService;
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
            - EMERGENCY: Đe dọa tính mạng ngay lập tức, ngưng tim, nghẹn sặc suy hô hấp, chấn thương sọ não, hôn mê bất tỉnh.
            - CRITICAL: Gãy cổ xương đùi, vết thương rách sâu cần cấp cứu ngoại viện, đi lạc ra ngoài khuôn viên (Elopement).
            - HIGH: Té ngã có xây xát hoặc va đập vùng đầu, nhầm thuốc cần theo dõi, kích động hành vi bạo lực.
            - MEDIUM: Va chạm nhẹ, trầy xước nông, tranh cãi kích động nhẹ tự thoái lui.
            - LOW: Sự cố cơ sở vật chất nhỏ không gây tổn hại sức khỏe cư dân.
            Chỉ nêu các bước rà soát và báo cáo theo quy trình đã được cơ sở phê duyệt.
            Không tự đặt lịch theo dõi, thời hạn thông báo, thủ thuật hoặc phác đồ cấp cứu.
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

            String rawResponse = aiExecutionService.execute(CLASSIFY_SYSTEM_PROMPT, userPrompt.toString());


            if (rawResponse == null || rawResponse.isBlank()) {
                return createFallbackResponse(request.getDescription());
            }

            return parseJsonResponse(rawResponse, request.getDescription());
        } catch (Exception e) {
            log.warn("Không phân loại được sự cố bằng AI: {}", e.getClass().getSimpleName());
            return createFallbackResponse(request.getDescription());
        }
    }

    private IncidentAnalysisResponse parseJsonResponse(String rawResponse, String description) {
        try {
            String json = rawResponse.trim();
            // Robust JSON block extraction
            if (json.contains("{") && json.contains("}")) {
                json = json.substring(json.indexOf('{'), json.lastIndexOf('}') + 1).trim();
            }

            IncidentAnalysisResponse parsed = objectMapper.readValue(json, IncidentAnalysisResponse.class);
            if (parsed == null || parsed.getSuggestedSeverity() == null
                    || parsed.getSuggestedIncidentType() == null || parsed.getRecommendedActions() == null) {
                return createFallbackResponse(description);
            }
            return parsed;
        } catch (Exception e) {
            log.warn("Không thể parse trực tiếp JSON từ AI response, áp dụng fallback phân tích lâm sàng: {}", e.getMessage());
            return createFallbackResponse(description);
        }
    }

    private IncidentAnalysisResponse createFallbackResponse(String text) {
        String lower = text != null ? text.toLowerCase() : "";
        String severity = "REVIEW_REQUIRED";
        String type = "OTHER";
        List<String> actions = new ArrayList<>();

        if (lower.contains("bất tỉnh") || lower.contains("ngưng thở") || lower.contains("cấp cứu")
                || lower.contains("nguy kịch") || lower.contains("sặc") || lower.contains("choking")) {
            severity = "EMERGENCY";
            type = "MEDICAL_EMERGENCY";
        } else if (lower.contains("đập đầu") || lower.contains("chấn thương đầu") || lower.contains("head strike")
                || lower.contains("gãy xương") || lower.contains("fracture")) {
            severity = "CRITICAL";
            type = "INJURY";
        } else if (lower.contains("ngã") || lower.contains("té") || lower.contains("fall")) {
            severity = "HIGH";
            type = "FALL";
        } else if (lower.contains("thuốc") || lower.contains("medication") || lower.contains("quá liều") || lower.contains("nhầm liều")) {
            severity = "HIGH";
            type = "MEDICATION_ERROR";
        } else if (lower.contains("đi lạc") || lower.contains("trốn") || lower.contains("elopement") || lower.contains("mất tích")) {
            severity = "CRITICAL";
            type = "ELOPEMENT";
        } else if (lower.contains("kích động") || lower.contains("đánh") || lower.contains("bạo lực") || lower.contains("combative")) {
            severity = "HIGH";
            type = "BEHAVIORAL";
        }
        actions.add("Bảo đảm an toàn trước mắt và kích hoạt quy trình xử lý sự cố đã được cơ sở phê duyệt.");
        actions.add("Liên hệ điều dưỡng/bác sĩ phụ trách để xác nhận mức độ và bước xử lý.");
        actions.add("Ghi nhận dữ kiện quan sát được, thời gian và người đã thông báo.");

        return IncidentAnalysisResponse.builder()
                .suggestedSeverity(severity)
                .suggestedIncidentType(type)
                .rationale("Gợi ý dựa trên từ khóa khi AI không khả dụng; nhân viên phải xác nhận mức độ và loại sự cố.")
                .recommendedActions(actions)
                .confidence("OTHER".equals(type) ? 0.0 : 0.3)
                .build();
    }
}
