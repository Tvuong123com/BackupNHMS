package com.eldercare.modules.ai.elopement.service;

import com.eldercare.modules.ai.elopement.dto.ElopementRiskRequest;
import com.eldercare.modules.ai.elopement.dto.ElopementRiskResponse;
import com.eldercare.modules.ai.engine.AiExecutionService;
import com.eldercare.modules.resident_intake.resident.repository.ResidentRepository;
import com.eldercare.modules.resident_intake.resident.repository.ClinicalRecordRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ElopementRiskAiService {

    private final AiExecutionService aiExecutionService;
    private final ObjectMapper objectMapper;
    private final ResidentRepository residentRepository;
    private final ClinicalRecordRepository clinicalRecordRepository;

    private static final String ELOPEMENT_SYSTEM_PROMPT = """
            Bạn là Chuyên gia Tâm thần Lão khoa & An toàn Khu Chăm sóc Trí nhớ (Memory Care Safety Director).
            Hãy đánh giá nguy cơ bỏ trốn hoặc đi lạc (Elopement / Wandering Risk) của cư dân dựa trên điểm nhận thức và hành vi gần đây.
            Chỉ dùng dữ kiện đã cung cấp; không suy diễn điểm MMSE, chẩn đoán hoặc hành vi chưa ghi nhận.
            Đây là bản nháp để nhân viên lâm sàng duyệt, không tự kích hoạt khóa cửa hay quy trình khẩn cấp.
            
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
        if (request.getResidentId() == null) throw new IllegalArgumentException("Resident ID is required");
        var resident = residentRepository.findByIdAndIsDeletedFalse(request.getResidentId())
                .orElseThrow(() -> new IllegalArgumentException("Resident not found"));
        request.setResidentName(resident.getFirstName() + " " + resident.getLastName());
        request.setRoomNumber(null);
        request.setHasDementiaDiagnosis(clinicalRecordRepository
                .findByResidentIdAndRecordTypeAndIsDeletedFalse(request.getResidentId(), "DIAGNOSIS")
                .stream().anyMatch(d -> d.getDescription().toLowerCase().matches(".*(dementia|alzheimer|sa sút).*")));
        if ((request.getRecentBehaviors() == null || request.getRecentBehaviors().isEmpty())
                && request.getCognitiveScore() == null) {
            throw new IllegalArgumentException("Documented observations or a verified cognitive score are required");
        }
        try {
            StringBuilder prompt = new StringBuilder();
            prompt.append("Cư dân: ").append(request.getResidentName() != null ? request.getResidentName() : "Cư dân")
                    .append(", Phòng: ").append(request.getRoomNumber() != null ? request.getRoomNumber() : "Chưa rõ").append("\n");
            prompt.append("Điểm nhận thức (MMSE/30): ").append(request.getCognitiveScore() != null ? request.getCognitiveScore() : "Chưa cung cấp").append("\n");
            prompt.append("Chẩn đoán sa sút trí tuệ: ").append(Boolean.TRUE.equals(request.getHasDementiaDiagnosis()) ? "Có" : "Không").append("\n");
            if (request.getRecentBehaviors() != null && !request.getRecentBehaviors().isEmpty()) {
                prompt.append("Hành vi ghi nhận gần đây: ").append(String.join(", ", request.getRecentBehaviors())).append("\n");
            }
            if (request.getTimeOfDay() != null) {
                prompt.append("Thời điểm: ").append(request.getTimeOfDay()).append("\n");
            }

            String raw = aiExecutionService.execute(ELOPEMENT_SYSTEM_PROMPT, prompt.toString());

            return parseJsonResponse(raw, request);
        } catch (Exception e) {
            log.warn("Elopement AI unavailable: {}", e.getClass().getSimpleName());
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

            int firstBrace = json.indexOf('{');
            int lastBrace = json.lastIndexOf('}');
            if (firstBrace >= 0 && lastBrace > firstBrace) {
                json = json.substring(firstBrace, lastBrace + 1).trim();
            }

            var result = objectMapper.readValue(json, ElopementRiskResponse.class);
            result.setTriggerDoorLockAlert(false);
            return result;
        } catch (Exception e) {
            log.warn("Không parse được JSON từ AI elopement eval, dùng fallback: {}", e.getMessage());
            return createFallbackResponse(request);
        }
    }

    private ElopementRiskResponse createFallbackResponse(ElopementRiskRequest request) {
        return ElopementRiskResponse.builder()
                .riskLevel("UNDETERMINED")
                .riskScore(null)
                .triggerDoorLockAlert(false)
                .behavioralAnalysis("Automated review unavailable. A clinician must review the documented observations and current care plan.")
                .immediateSafeguards(List.of("Follow the facility's approved missing-resident procedure for an active emergency."))
                .build();
    }
}
