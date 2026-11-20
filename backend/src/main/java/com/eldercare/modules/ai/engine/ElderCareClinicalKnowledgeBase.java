package com.eldercare.modules.ai.engine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Staff workflow reminders only. These entries are not approved clinical protocols.
 * A facility-approved, versioned policy source is needed before detailed clinical grounding.
 */
@Component
public class ElderCareClinicalKnowledgeBase {
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClinicalProtocol {
        private String id;
        private String category;
        private String title;
        private List<String> keywords;
        private String structuredAnswer;
    }

    private final List<ClinicalProtocol> protocols = List.of(
            entry("FALL", "CLINICAL", "Fall Prevention & Response Protocol", List.of("fall", "té ngã", "ngã", "unwitnessed", "phòng ngừa té ngã", "phòng chống té ngã", "chống té ngã", "ngừa ngã"),
                    """
                    Quy trình Phòng ngừa & Can thiệp Té ngã Chuẩn Y khoa (CDC STEADI & Morse Guidelines):
                    1. Giám sát & Đánh giá Nguy cơ:
                       - Đánh giá định kỳ thang Morse Fall Scale và DFRS; rà soát đơn thuốc làm tăng nguy cơ ngã (FRIDs: an thần, hạ áp, lợi tiểu, chống loạn thần).
                       - Theo dõi sát cư dân có tiền sử ngã trong 90 ngày hoặc suy giảm nhận thức.
                    2. Can thiệp Môi trường & An toàn Thể chất:
                       - Hạ giường xuống mức thấp nhất, trải đệm giảm chấn cạnh giường (fall floor mats), khóa bánh xe giường/ghế.
                       - Cài đặt cảm biến giường/ghế (bed/chair alarm) cho cư dân nguy cơ cao; chuông gọi y tá trong tầm tay.
                       - Đảm bảo ánh sáng ban đêm, sàn nhà khô ráo, mang giày/vớ chống trượt (non-skid footwear).
                    3. Lịch Trình Chăm sóc & Vận động:
                       - Hỗ trợ đi vệ sinh chủ động theo khung giờ (Scheduled Toileting mỗi 2 giờ), giảm thiểu việc tự rời giường ban đêm.
                       - Phối hợp Vật lý trị liệu (PT/OT) duy trì thăng bằng và sức cơ; hỗ trợ 1:1 khi di chuyển nếu cần.
                    4. Xử trí khi xảy ra Té ngã (Post-Fall):
                       - Giữ nguyên tư thế, kiểm tra chấn thương đầu/cột sống/xương khớp, đo sinh hiệu trước khi nâng đỡ; báo ngay bác sĩ/điều dưỡng trực và ghi nhận hồ sơ sự cố.
                    """),
            entry("STROKE", "EMERGENCY", "Possible stroke", List.of("stroke", "đột quỵ", "tai biến", "méo miệng"),
                    "Possible acute neurological change needs immediate escalation under the facility emergency procedure. "
                            + "Record the last known well time and observed changes for the responding clinician. Do not determine treatment eligibility from this chat."),
            entry("MEDICATION", "MEDICATION", "Medication concern", List.of("warfarin", "anticoagulant", "nsaid", "bleeding", "thuốc", "chống đông"),
                    "Do not change or withhold a prescribed medication based on this assistant. Check the current order and contact the prescriber or pharmacist. "
                            + "Escalate urgent symptoms according to facility procedure and document the concern and response."),
            entry("PSYCHOTROPIC", "MEDICATION", "Psychotropic review", List.of("psychotropic", "antipsychotic", "gdr", "f758", "an thần"),
                    "Use the current facility policy and applicable regulation for psychotropic medication review. "
                            + "Document observed behaviors, attempted supports, prescriber decisions, and follow-up. Do not infer a legal deadline from this assistant."),
            entry("SKIN", "ASSESSMENT", "Skin and pressure injury concern", List.of("braden", "pressure injury", "pressure ulcer", "loét", "tì đè"),
                    "Record the assessed skin findings and risk score from the chart. Ask the licensed care team to select prevention and treatment steps under the current care plan and approved policy."),
            entry("SBAR", "DOCUMENTATION", "SBAR handoff", List.of("sbar", "handover", "giao ca", "bàn giao"),
                    "S: Current concern and onset. B: Relevant baseline and recent changes. A: Verified observations and measurements. "
                            + "R: Requested clinician review, pending actions, and notifications. Fill only from the verified record."),
            entry("DEMENTIA", "ASSESSMENT", "Behavioral change", List.of("dementia", "sundowning", "alzheimer", "kích động", "sa sút"),
                    "Describe the behavior, timing, possible triggers, and safety concerns without diagnosing the cause. "
                            + "Follow the individualized care plan and ask the responsible clinician to review a new or worsening change."),
            entry("ELOPEMENT", "EMERGENCY", "Missing resident", List.of("elopement", "missing resident", "code yellow", "đi lạc", "mất tích"),
                    "Activate the facility's approved missing-resident emergency procedure immediately. "
                            + "Notify the designated response team and document the last verified location, search actions, and escalation times."),
            entry("INFECTION", "NURSING_OPS", "Possible infection control concern", List.of("c diff", "cdiff", "isolation", "cách ly", "nhiễm khuẩn"),
                    "Follow the current infection prevention policy and instructions from the infection prevention team. "
                            + "Document symptoms and exposure facts; do not choose isolation or treatment details from this assistant."),
            entry("LEVEL_OF_CARE", "ADMISSIONS", "Level of care and rates", List.of("loc", "level of care", "rates", "pricing", "chi phí", "mức chăm sóc"),
                    "Use the resident's verified assessment and the current approved rate schedule. "
                            + "Do not infer placement, coverage, or a price without a documented decision and quote.")
    );

    private static ClinicalProtocol entry(String id, String category, String title,
                                          List<String> keywords, String answer) {
        return new ClinicalProtocol(id, category, title, keywords, answer);
    }

    public Optional<ClinicalProtocol> findMatchingKnowledge(String query) {
        if (query == null || query.isBlank()) return Optional.empty();
        String normalized = query.toLowerCase(Locale.ROOT);
        return protocols.stream()
                .filter(p -> p.getKeywords().stream().anyMatch(normalized::contains))
                .findFirst();
    }

    public String getGroundingContext(String query) {
        return findMatchingKnowledge(query)
                .map(p -> "Staff workflow reminder (not an approved clinical protocol): " + p.getStructuredAnswer())
                .orElse("");
    }

    public List<ClinicalProtocol> getAllProtocols() {
        return protocols;
    }
}
