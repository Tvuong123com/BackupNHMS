package com.eldercare.modules.ai.summary.service;

import com.eldercare.modules.ai.summary.dto.SummaryRequest;
import com.eldercare.modules.ai.summary.dto.SummaryResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SummaryAiService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    private static final String SUMMARY_SYSTEM_PROMPT = """
            Bạn là Trợ lý Tổng hợp Y tế và Chăm sóc trong Viện dưỡng lão (Clinical Medical Scribe & Documentation Specialist).
            Dựa trên thông tin ca trực hoặc sự cố được cung cấp, hãy biên soạn một bản tóm tắt mạch lạc, cô đọng, khách quan và chuyên nghiệp.
            
            Trả về DUY NHẤT một chuỗi JSON hợp lệ theo định dạng sau (không kèm văn bản ngoài):
            {
              "title": "Tiêu đề báo cáo (VD: Báo Cáo Giao Ca Sáng 28/09/2026)",
              "narrativeSummary": "Đoạn văn tự nhiên tóm tắt diễn biến chăm sóc, sức khỏe cư dân và các sự cố phát sinh",
              "keyHighlights": ["Điểm nổi bật 1", "Điểm nổi bật 2"],
              "handoverActionItems": ["Việc cần bàn giao cho ca tiếp theo 1", "Việc bàn giao 2"]
            }
            """;

    public SummaryResponse generateSummary(SummaryRequest request) {
        try {
            StringBuilder prompt = new StringBuilder();
            prompt.append("Loại báo cáo: ").append(request.getType() != null ? request.getType() : "SHIFT_HANDOVER").append("\n");
            prompt.append("Phong cách viết: ").append(request.getTone() != null ? request.getTone() : "CLINICAL").append("\n");
            if (request.getShiftName() != null) {
                prompt.append("Tên ca trực: ").append(request.getShiftName()).append("\n");
            }
            if (request.getResidentName() != null) {
                prompt.append("Cư dân: ").append(request.getResidentName()).append("\n");
            }
            if (request.getEvents() != null && !request.getEvents().isEmpty()) {
                prompt.append("Các sự việc ghi nhận trong ca:\n");
                for (String ev : request.getEvents()) {
                    prompt.append("- ").append(ev).append("\n");
                }
            }
            if (request.getExtraNotes() != null) {
                prompt.append("Ghi chú thêm: ").append(request.getExtraNotes()).append("\n");
            }

            String raw = chatClient.prompt()
                    .system(SUMMARY_SYSTEM_PROMPT)
                    .user(prompt.toString())
                    .call()
                    .content();

            log.info("AI Summary raw response: {}", raw);
            SummaryResponse response = parseSummaryResponse(raw);
            response.setGeneratedAt(OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            return response;
        } catch (Exception e) {
            log.error("Lỗi khi sinh báo cáo AI: {}", e.getMessage(), e);
            return createFallbackSummary(request);
        }
    }

    private SummaryResponse parseSummaryResponse(String raw) {
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

            return objectMapper.readValue(json, SummaryResponse.class);
        } catch (Exception e) {
            log.warn("Không thể parse trực tiếp JSON Summary, chuyển sang fallback: {}", e.getMessage());
            return SummaryResponse.builder()
                    .title("Báo Cáo Tóm Tắt Hoạt Động Chăm Sóc")
                    .narrativeSummary(raw)
                    .keyHighlights(List.of("Tổng hợp tự động từ thông tin hệ thống."))
                    .handoverActionItems(List.of("Kiểm tra lại chỉ số sinh tồn của cư dân trong ca kế tiếp."))
                    .generatedAt(OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                    .build();
        }
    }

    private SummaryResponse createFallbackSummary(SummaryRequest request) {
        String shift = request.getShiftName() != null ? request.getShiftName() : "Ca trực hiện tại";
        List<String> highlights = new ArrayList<>();
        List<String> handover = new ArrayList<>();

        if (request.getEvents() != null && !request.getEvents().isEmpty()) {
            highlights.addAll(request.getEvents());
        } else {
            highlights.add("Các hoạt động chăm sóc và sinh hoạt của cư dân diễn ra ổn định.");
        }

        handover.add("Theo dõi chỉ số sinh tồn ca tiếp theo");
        handover.add("Đảm bảo cấp phát thuốc đúng giờ theo lịch eMAR");

        return SummaryResponse.builder()
                .title("Báo Cáo Tổng Hợp " + shift)
                .narrativeSummary("Trong " + shift + ", toàn bộ công tác chăm sóc cư dân được triển khai theo đúng quy trình. Các cư dân được hỗ trợ dinh dưỡng, vệ sinh và theo dõi sức khỏe liên tục.")
                .keyHighlights(highlights)
                .handoverActionItems(handover)
                .generatedAt(OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .build();
    }
}
