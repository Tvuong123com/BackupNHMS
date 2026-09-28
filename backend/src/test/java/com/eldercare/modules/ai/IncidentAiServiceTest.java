package com.eldercare.modules.ai;

import com.eldercare.modules.ai.incident.dto.IncidentAnalysisRequest;
import com.eldercare.modules.ai.incident.dto.IncidentAnalysisResponse;
import com.eldercare.modules.ai.incident.service.IncidentAiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class IncidentAiServiceTest {

    @Mock
    private ChatClient chatClient;

    private IncidentAiService incidentAiService;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        incidentAiService = new IncidentAiService(chatClient, objectMapper);
    }

    @Test
    @DisplayName("Nên phân loại té ngã thành loại FALL và mức độ HIGH khi fallback kích hoạt")
    void shouldClassifyFallIncidentCorrectly() {
        IncidentAnalysisRequest request = IncidentAnalysisRequest.builder()
                .description("Cụ ngã từ mép giường xuống sàn nhà lúc 2h sáng, xây xát nhẹ ở đầu gối")
                .location("Phòng 204")
                .residentInfo("Eleanor Vance - 82 tuổi")
                .build();

        IncidentAnalysisResponse response = incidentAiService.classifyIncident(request);

        assertNotNull(response);
        assertEquals("HIGH", response.getSuggestedSeverity());
        assertEquals("FALL", response.getSuggestedIncidentType());
        assertTrue(response.getRecommendedActions().size() > 0);
    }

    @Test
    @DisplayName("Nên phân loại trường hợp khẩn cấp bất tỉnh thành EMERGENCY")
    void shouldClassifyEmergencyCorrectly() {
        IncidentAnalysisRequest request = IncidentAnalysisRequest.builder()
                .description("Cư dân bất tỉnh nhân sự, thở dốc cần cấp cứu ngay lập tức")
                .location("Hành lang tầng 1")
                .build();

        IncidentAnalysisResponse response = incidentAiService.classifyIncident(request);

        assertNotNull(response);
        assertEquals("EMERGENCY", response.getSuggestedSeverity());
        assertEquals("MEDICAL_EMERGENCY", response.getSuggestedIncidentType());
    }

    @Test
    @DisplayName("Nên phân loại sự cố nhầm thuốc thành MEDICATION_ERROR")
    void shouldClassifyMedicationErrorCorrectly() {
        IncidentAnalysisRequest request = IncidentAnalysisRequest.builder()
                .description("Điều dưỡng cấp phát nhầm liều thuốc hạ áp lúc sáng sớm")
                .build();

        IncidentAnalysisResponse response = incidentAiService.classifyIncident(request);

        assertNotNull(response);
        assertEquals("HIGH", response.getSuggestedSeverity());
        assertEquals("MEDICATION_ERROR", response.getSuggestedIncidentType());
    }
}
