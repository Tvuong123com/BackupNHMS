package com.eldercare.modules.ai.careplan.service;

import com.eldercare.modules.ai.careplan.dto.CarePlanSuggestionRequest;
import com.eldercare.modules.ai.careplan.dto.CarePlanSuggestionResponse;
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
public class CarePlanAiService {

    private final AiExecutionService aiExecutionService;
    private final ObjectMapper objectMapper;

    private static final String CARE_PLAN_SYSTEM_PROMPT = """
            Bạn là Chuyên gia Lập Kế hoạch Chăm sóc Y tế (Clinical Nurse Specialist & Care Plan Coordinator) trong viện dưỡng lão.
            Dựa trên thông tin đánh giá (Assessment), điểm ADL, chẩn đoán bệnh và lịch sử sự cố của cư dân, hãy thiết lập các mục tiêu chăm sóc (Care Goals) và biện pháp can thiệp (Interventions) tương ứng.
            
            Trả về DUY NHẤT một chuỗi JSON hợp lệ theo định dạng sau (không kèm văn bản ngoài):
            {
              "residentSummary": "Tóm tắt ngắn gọn tình trạng thể chất và nhận thức của cư dân",
              "suggestedGoals": [
                {
                  "goalName": "Tên mục tiêu chăm sóc (VD: Chương trình Phòng ngừa Té ngã)",
                  "description": "Mô tả chi tiết mục tiêu cần đạt được",
                  "priority": "HIGH | MEDIUM | LOW",
                  "rationale": "Lý do lâm sàng đề xuất mục tiêu này",
                  "interventions": [
                    {
                      "name": "Nội dung biện pháp can thiệp cụ thể",
                      "assignedRole": "CNA | NURSE | PT | OT | DOCTOR"
                    }
                  ]
                }
              ]
            }
            """;

    public CarePlanSuggestionResponse suggestCarePlan(CarePlanSuggestionRequest request) {
        try {
            StringBuilder prompt = new StringBuilder();
            prompt.append("Hồ sơ cư dân: ").append(request.getResidentName() != null ? request.getResidentName() : "Cư dân")
                    .append(", ").append(request.getAge() != null ? request.getAge() : 80).append(" tuổi")
                    .append(", Giới tính: ").append(request.getGender() != null ? request.getGender() : "Chưa rõ").append("\n");

            if (request.getCareLevel() != null) {
                prompt.append("Cấp độ chăm sóc (Care Level): ").append(request.getCareLevel()).append("\n");
            }
            if (request.getAdlScore() != null) {
                prompt.append("Điểm đánh giá sinh hoạt (ADL Total Score): ").append(request.getAdlScore()).append("/28\n");
            }
            if (request.getDiagnoses() != null && !request.getDiagnoses().isEmpty()) {
                prompt.append("Chẩn đoán bệnh lý nền: ").append(String.join(", ", request.getDiagnoses())).append("\n");
            }
            if (request.getRecentIncidents() != null && !request.getRecentIncidents().isEmpty()) {
                prompt.append("Lịch sử sự cố gần đây: ").append(String.join("; ", request.getRecentIncidents())).append("\n");
            }
            if (request.getExistingGoals() != null && !request.getExistingGoals().isEmpty()) {
                prompt.append("Các mục tiêu đã có: ").append(String.join(", ", request.getExistingGoals())).append("\n");
            }

            String raw = aiExecutionService.execute(CARE_PLAN_SYSTEM_PROMPT, prompt.toString());

            log.info("AI Care Plan suggestion raw response: {}", raw);
            return parseCarePlanResponse(raw, request);
        } catch (Exception e) {
            log.error("Lỗi khi gợi ý kế hoạch chăm sóc AI: {}", e.getMessage(), e);
            return createFallbackCarePlan(request);
        }
    }

    private CarePlanSuggestionResponse parseCarePlanResponse(String raw, CarePlanSuggestionRequest request) {
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

            return objectMapper.readValue(json, CarePlanSuggestionResponse.class);
        } catch (Exception e) {
            log.warn("Không thể parse trực tiếp JSON Care Plan từ AI, kích hoạt fallback thông minh: {}", e.getMessage());
            return createFallbackCarePlan(request);
        }
    }

    private CarePlanSuggestionResponse createFallbackCarePlan(CarePlanSuggestionRequest request) {
        List<CarePlanSuggestionResponse.SuggestedGoalDTO> goals = new ArrayList<>();

        // Fall Risk Goal nếu có tiền sử ngã hoặc điểm ADL cao
        boolean hasFallHistory = request.getRecentIncidents() != null &&
                request.getRecentIncidents().stream().anyMatch(i -> i.toLowerCase().contains("ngã") || i.toLowerCase().contains("fall"));

        if (hasFallHistory || (request.getAdlScore() != null && request.getAdlScore() >= 18)) {
            List<CarePlanSuggestionResponse.SuggestedInterventionDTO> fallInterventions = new ArrayList<>();
            fallInterventions.add(new CarePlanSuggestionResponse.SuggestedInterventionDTO("Lắp thanh vịn an toàn tại phòng tắm và cạnh giường", "CNA"));
            fallInterventions.add(new CarePlanSuggestionResponse.SuggestedInterventionDTO("Hỗ trợ cư dân di chuyển và đi vệ sinh vào ban đêm", "CNA"));
            fallInterventions.add(new CarePlanSuggestionResponse.SuggestedInterventionDTO("Đánh giá lại thăng bằng và chỉ định bài tập vật lý trị liệu 3 buổi/tuần", "PT"));

            goals.add(CarePlanSuggestionResponse.SuggestedGoalDTO.builder()
                    .goalName("Chương trình Phòng ngừa Té ngã Cá nhân hóa")
                    .description("Giảm thiểu tối đa nguy cơ té ngã khi di chuyển trong phòng và sinh hoạt hàng ngày.")
                    .priority("HIGH")
                    .rationale("Cư dân có tiền sử té ngã hoặc suy giảm thăng bằng vận động.")
                    .interventions(fallInterventions)
                    .build());
        }

        // Memory Care Goal
        boolean isMemoryCare = (request.getCareLevel() != null && request.getCareLevel().toLowerCase().contains("memory")) ||
                (request.getDiagnoses() != null && request.getDiagnoses().stream().anyMatch(d -> d.toLowerCase().contains("dementia") || d.toLowerCase().contains("alzheimer") || d.toLowerCase().contains("sa sút")));

        if (isMemoryCare) {
            List<CarePlanSuggestionResponse.SuggestedInterventionDTO> memoryInterventions = new ArrayList<>();
            memoryInterventions.add(new CarePlanSuggestionResponse.SuggestedInterventionDTO("Kiểm tra vị trí và giám sát phòng tránh đi lạc mỗi 30-45 phút", "CNA"));
            memoryInterventions.add(new CarePlanSuggestionResponse.SuggestedInterventionDTO("Tổ chức hoạt động gợi nhớ ký ức (Reminiscence Therapy) 2 lần/tuần", "OT"));

            goals.add(CarePlanSuggestionResponse.SuggestedGoalDTO.builder()
                    .goalName("Hỗ trợ Trí nhớ và An toàn Định hướng")
                    .description("Duy trì sự ổn định tâm lý, định hướng không gian và ngăn ngừa hành vi đi lạc.")
                    .priority("HIGH")
                    .rationale("Cư dân có triệu chứng sa sút trí tuệ / Alzheimer cần giám sát tăng cường.")
                    .interventions(memoryInterventions)
                    .build());
        }

        // General Health & Medication Management Goal
        List<CarePlanSuggestionResponse.SuggestedInterventionDTO> medInterventions = new ArrayList<>();
        medInterventions.add(new CarePlanSuggestionResponse.SuggestedInterventionDTO("Đo huyết áp và mạch 2 lần mỗi ngày (sáng/chiều)", "NURSE"));
        medInterventions.add(new CarePlanSuggestionResponse.SuggestedInterventionDTO("Cấp phát và giám sát uống thuốc theo đúng lịch eMAR", "NURSE"));

        goals.add(CarePlanSuggestionResponse.SuggestedGoalDTO.builder()
                .goalName("Quản lý Bệnh mạn tính & Tuân thủ Dược phẩm")
                .description("Theo dõi sát các chỉ số sinh tồn và đảm bảo uống thuốc đúng giờ, đúng liều lượng.")
                .priority("MEDIUM")
                .rationale("Cư dân cao tuổi cần duy trì huyết áp và chỉ số sinh tồn ổn định.")
                .interventions(medInterventions)
                .build());

        return CarePlanSuggestionResponse.builder()
                .residentSummary("Cư dân " + (request.getResidentName() != null ? request.getResidentName() : "") + " đang tiếp nhận chăm sóc theo dõi định kỳ.")
                .suggestedGoals(goals)
                .build();
    }
}
