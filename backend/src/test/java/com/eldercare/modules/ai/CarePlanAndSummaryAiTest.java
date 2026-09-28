package com.eldercare.modules.ai;

import com.eldercare.modules.ai.careplan.dto.CarePlanSuggestionRequest;
import com.eldercare.modules.ai.careplan.dto.CarePlanSuggestionResponse;
import com.eldercare.modules.ai.careplan.service.CarePlanAiService;
import com.eldercare.modules.ai.summary.dto.SummaryRequest;
import com.eldercare.modules.ai.summary.dto.SummaryResponse;
import com.eldercare.modules.ai.summary.service.SummaryAiService;
import com.eldercare.modules.ai.voice.dto.VoiceCareParseRequest;
import com.eldercare.modules.ai.voice.dto.VoiceCareParseResponse;
import com.eldercare.modules.ai.voice.service.VoiceCareAiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CarePlanAndSummaryAiTest {

    @Mock
    private ChatClient chatClient;

    private CarePlanAiService carePlanAiService;
    private SummaryAiService summaryAiService;
    private VoiceCareAiService voiceCareAiService;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        carePlanAiService = new CarePlanAiService(chatClient, objectMapper);
        summaryAiService = new SummaryAiService(chatClient, objectMapper);
        voiceCareAiService = new VoiceCareAiService(chatClient, objectMapper);
    }

    @Test
    @DisplayName("Nên đề xuất mục tiêu phòng ngừa té ngã khi cư dân có tiền sử té ngã")
    void shouldSuggestFallPreventionGoal() {
        CarePlanSuggestionRequest request = CarePlanSuggestionRequest.builder()
                .residentName("Eleanor Vance")
                .age(82)
                .careLevel("Skilled Nursing")
                .adlScore(22)
                .recentIncidents(List.of("Té ngã tại phòng vệ sinh tuần trước"))
                .build();

        CarePlanSuggestionResponse response = carePlanAiService.suggestCarePlan(request);

        assertNotNull(response);
        assertNotNull(response.getSuggestedGoals());
        assertTrue(response.getSuggestedGoals().stream().anyMatch(g -> g.getGoalName().contains("Phòng ngừa Té ngã")));
    }

    @Test
    @DisplayName("Nên sinh báo cáo tóm tắt ca trực thành công")
    void shouldGenerateShiftSummary() {
        SummaryRequest request = SummaryRequest.builder()
                .shiftName("Ca sáng")
                .events(List.of("Cư dân phòng 204 ăn uống tốt", "Đo huyết áp định kỳ ổn định 120/80"))
                .build();

        SummaryResponse response = summaryAiService.generateSummary(request);

        assertNotNull(response);
        assertNotNull(response.getTitle());
        assertNotNull(response.getNarrativeSummary());
        assertTrue(response.getKeyHighlights().size() > 0);
    }

    @Test
    @DisplayName("Nên bóc tách chỉ số sinh tồn và khẩu phần ăn từ giọng nói")
    void shouldParseSpokenVoiceNote() {
        VoiceCareParseRequest request = VoiceCareParseRequest.builder()
                .spokenText("Cụ phòng 204 ăn hết 80% phần cơm, huyết áp 130/85, đã uống thuốc")
                .expectedRoomNumber("204")
                .build();

        VoiceCareParseResponse response = voiceCareAiService.parseSpokenCareNote(request);

        assertNotNull(response);
        assertEquals(80, response.getDietaryIntakePercentage());
        assertEquals(130, response.getBloodPressureSystolic());
        assertEquals(85, response.getBloodPressureDiastolic());
        assertTrue(response.getMedicationTaken());
    }
}
