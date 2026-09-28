package com.eldercare.modules.ai.voice.service;

import com.eldercare.modules.ai.voice.dto.VoiceCareParseRequest;
import com.eldercare.modules.ai.voice.dto.VoiceCareParseResponse;
import com.eldercare.modules.ai.engine.AiExecutionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class VoiceCareAiService {

    private final AiExecutionService aiExecutionService;
    private final ObjectMapper objectMapper;

    private static final String VOICE_PARSE_SYSTEM_PROMPT = """
            Bạn là Trợ lý AI Bóc tách Dữ liệu Giọng nói Y tế (Clinical Speech-to-Entity Extractor).
            Điều dưỡng hoặc hộ lý (CNA) đọc bằng giọng nói nhật ký chăm sóc cư dân.
            Hãy bóc tách thành chuỗi JSON hợp lệ (DUY NHẤT một chuỗi JSON, không kèm văn bản ngoài):
            {
              "residentIdentifier": "Tên cư dân nhận diện được hoặc null",
              "roomNumber": "Số phòng nhận diện được hoặc null",
              "dietaryIntakePercentage": 80 (số nguyên 0-100 hoặc null nếu không nhắc tới),
              "bloodPressureSystolic": 120 (hoặc null),
              "bloodPressureDiastolic": 80 (hoặc null),
              "heartRateBpm": 75 (hoặc null),
              "bodyTemperatureCelsius": 36.8 (hoặc null),
              "medicationTaken": true/false (hoặc null),
              "extractedNotes": "Tóm tắt ngắn gọn nội dung chăm sóc",
              "confidence": 0.95
            }
            """;

    public VoiceCareParseResponse parseSpokenCareNote(VoiceCareParseRequest request) {
        try {
            StringBuilder prompt = new StringBuilder();
            prompt.append("Câu nói của điều dưỡng: \"").append(request.getSpokenText()).append("\"\n");
            if (request.getExpectedResidentName() != null) {
                prompt.append("Cư dân dự kiến: ").append(request.getExpectedResidentName()).append("\n");
            }
            if (request.getExpectedRoomNumber() != null) {
                prompt.append("Phòng dự kiến: ").append(request.getExpectedRoomNumber()).append("\n");
            }

            String raw = aiExecutionService.execute(VOICE_PARSE_SYSTEM_PROMPT, prompt.toString());

            log.info("AI Voice note parse raw response: {}", raw);
            return parseJsonResponse(raw, request);
        } catch (Exception e) {
            log.error("Lỗi khi bóc tách ghi chú giọng nói AI: {}", e.getMessage(), e);
            return fallbackRegexParse(request);
        }
    }

    private VoiceCareParseResponse parseJsonResponse(String raw, VoiceCareParseRequest request) {
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

            return objectMapper.readValue(json, VoiceCareParseResponse.class);
        } catch (Exception e) {
            log.warn("Không parse được JSON từ AI voice parse, chuyển sang fallback regex: {}", e.getMessage());
            return fallbackRegexParse(request);
        }
    }

    private VoiceCareParseResponse fallbackRegexParse(VoiceCareParseRequest request) {
        String text = request.getSpokenText() != null ? request.getSpokenText() : "";
        Integer systolic = null;
        Integer diastolic = null;
        Integer intake = null;
        Boolean med = null;

        // Bóc tách huyết áp dạng 120/80 hoặc "120 trên 80"
        Matcher bpMatcher = Pattern.compile("(\\d{2,3})\\s*(/|trên)\\s*(\\d{2,3})").matcher(text);
        if (bpMatcher.find()) {
            systolic = Integer.parseInt(bpMatcher.group(1));
            diastolic = Integer.parseInt(bpMatcher.group(3));
        }

        // Bóc tách % ăn dạng 80% hoặc "80 phần trăm"
        Matcher intakeMatcher = Pattern.compile("(\\d{1,3})\\s*(%|phần trăm)").matcher(text);
        if (intakeMatcher.find()) {
            intake = Integer.parseInt(intakeMatcher.group(1));
        }

        if (text.toLowerCase().contains("uống thuốc") || text.toLowerCase().contains("đã uống")) {
            med = true;
        }

        return VoiceCareParseResponse.builder()
                .residentIdentifier(request.getExpectedResidentName())
                .roomNumber(request.getExpectedRoomNumber())
                .bloodPressureSystolic(systolic)
                .bloodPressureDiastolic(diastolic)
                .dietaryIntakePercentage(intake)
                .medicationTaken(med)
                .extractedNotes(text)
                .confidence(0.85)
                .build();
    }
}
