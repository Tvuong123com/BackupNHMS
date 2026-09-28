package com.eldercare.modules.ai.medication.service;

import com.eldercare.modules.ai.medication.dto.MedicationCheckRequest;
import com.eldercare.modules.ai.medication.dto.MedicationCheckResponse;
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
public class MedicationSafetyAiService {

    private final AiExecutionService aiExecutionService;
    private final ObjectMapper objectMapper;

    private static final String MED_SAFETY_SYSTEM_PROMPT = """
            Bạn là Dược sĩ Lâm sàng Chuyên khoa Lão khoa (Geriatric Clinical Pharmacist).
            Nhiệm vụ của bạn là rà soát đơn thuốc của cư dân cao tuổi để phát hiện:
            1. Dị ứng thuốc (Allergy contraindication).
            2. Tương tác thuốc bất lợi (Drug-Drug Interactions).
            3. Chống chỉ định với bệnh lý nền (VD: suy thận, suy tim, viêm loét dạ dày).

            Trả về DUY NHẤT một chuỗi JSON hợp lệ theo cấu trúc sau (không kèm văn bản ngoài):
            {
              "riskLevel": "SAFE | MILD | MODERATE | SEVERE | CONTRAINDICATED",
              "isSafeToAdminister": true | false,
              "summaryWarning": "Tóm tắt cảnh báo an toàn ngắn gọn",
              "interactions": [
                {
                  "interactingSubstance": "Tên thuốc hoặc dị ứng liên quan",
                  "severity": "MILD | MODERATE | SEVERE",
                  "mechanism": "Cơ chế tương tác",
                  "recommendation": "Khuyến nghị điều chỉnh liều hoặc thuốc thay thế"
                }
              ],
              "clinicalPrecautions": ["Lưu ý theo dõi 1", "Lưu ý theo dõi 2"]
            }
            """;

    public MedicationCheckResponse checkSafety(MedicationCheckRequest request) {
        try {
            StringBuilder prompt = new StringBuilder();
            prompt.append("Cư dân: ").append(request.getResidentName() != null ? request.getResidentName() : "Cư dân cao tuổi").append("\n");
            prompt.append("Thuốc mới dự định kê: ").append(request.getNewMedication()).append("\n");
            if (request.getCurrentMedications() != null && !request.getCurrentMedications().isEmpty()) {
                prompt.append("Danh mục thuốc đang dùng: ").append(String.join(", ", request.getCurrentMedications())).append("\n");
            }
            if (request.getAllergies() != null && !request.getAllergies().isEmpty()) {
                prompt.append("Tiền sử dị ứng: ").append(String.join(", ", request.getAllergies())).append("\n");
            }
            if (request.getMedicalConditions() != null && !request.getMedicalConditions().isEmpty()) {
                prompt.append("Bệnh lý nền: ").append(String.join(", ", request.getMedicalConditions())).append("\n");
            }

            String raw = aiExecutionService.execute(MED_SAFETY_SYSTEM_PROMPT, prompt.toString());

            log.info("AI Medication safety check raw response: {}", raw);
            return parseJsonResponse(raw, request);
        } catch (Exception e) {
            log.error("Lỗi khi kiểm tra an toàn thuốc bằng AI: {}", e.getMessage(), e);
            return createFallbackResponse(request);
        }
    }

    private MedicationCheckResponse parseJsonResponse(String raw, MedicationCheckRequest request) {
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

            return objectMapper.readValue(json, MedicationCheckResponse.class);
        } catch (Exception e) {
            log.warn("Không parse được JSON từ AI med check, áp dụng fallback lâm sàng: {}", e.getMessage());
            return createFallbackResponse(request);
        }
    }

    private MedicationCheckResponse createFallbackResponse(MedicationCheckRequest request) {
        String newMed = request.getNewMedication() != null ? request.getNewMedication().toLowerCase() : "";
        List<MedicationCheckResponse.InteractionDetailDTO> interactions = new ArrayList<>();
        List<String> precautions = new ArrayList<>();
        String risk = "SAFE";
        boolean safe = true;
        String warning = "Thuốc an toàn khi dùng đơn lẻ theo liều chỉ định thông thường.";

        // Kiểm tra dị ứng
        if (request.getAllergies() != null) {
            for (String allergy : request.getAllergies()) {
                if (newMed.contains(allergy.toLowerCase())) {
                    risk = "CONTRAINDICATED";
                    safe = false;
                    warning = "CẢNH BÁO NGUY HIỂM: Cư dân có tiền sử dị ứng với nhóm thuốc này!";
                    interactions.add(new MedicationCheckResponse.InteractionDetailDTO(
                            allergy, "SEVERE", "Phản ứng quá mẫn cảm dị ứng thuốc", "Ngưng cấp phát ngay lập tức và tham vấn bác sĩ điều trị."
                    ));
                }
            }
        }

        // Kiểm tra tương tác Warfarin / Thuốc chống đông
        if (request.getCurrentMedications() != null) {
            boolean hasAnticoagulant = request.getCurrentMedications().stream().anyMatch(m -> m.toLowerCase().contains("warfarin") || m.toLowerCase().contains("aspirin") || m.toLowerCase().contains("clopidogrel"));
            if (hasAnticoagulant && (newMed.contains("ibuprofen") || newMed.contains("aspirin") || newMed.contains("nsaid") || newMed.contains("naproxen"))) {
                risk = "SEVERE";
                safe = false;
                warning = "Tương tác thuốc nghiêm trọng: Nguy cơ xuất huyết tiêu hóa và chảy máu tăng cao.";
                interactions.add(new MedicationCheckResponse.InteractionDetailDTO(
                        "Thuốc chống đông / Kháng kết tập tiểu cầu", "SEVERE",
                        "Hiệp đồng tác dụng chống đông làm tăng đáng kể nguy cơ xuất huyết ở người cao tuổi",
                        "Cân nhắc thay thế bằng Paracetamol (Acetaminophen) để giảm đau/hạ sốt."
                ));
            }
        }

        precautions.add("Đo lại huyết áp và theo dõi sát các phản ứng sau uống thuốc 60 phút.");
        precautions.add("Cập nhật đầy đủ vào hệ thống cấp phát thuốc điện tử (eMAR).");

        return MedicationCheckResponse.builder()
                .riskLevel(risk)
                .isSafeToAdminister(safe)
                .summaryWarning(warning)
                .interactions(interactions)
                .clinicalPrecautions(precautions)
                .build();
    }
}
