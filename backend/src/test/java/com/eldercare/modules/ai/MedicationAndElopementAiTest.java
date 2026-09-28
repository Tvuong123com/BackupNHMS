package com.eldercare.modules.ai;

import com.eldercare.modules.ai.elopement.dto.ElopementRiskRequest;
import com.eldercare.modules.ai.elopement.dto.ElopementRiskResponse;
import com.eldercare.modules.ai.elopement.service.ElopementRiskAiService;
import com.eldercare.modules.ai.medication.dto.MedicationCheckRequest;
import com.eldercare.modules.ai.medication.dto.MedicationCheckResponse;
import com.eldercare.modules.ai.medication.service.MedicationSafetyAiService;
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
class MedicationAndElopementAiTest {

    @Mock
    private ChatClient chatClient;

    private MedicationSafetyAiService medicationSafetyAiService;
    private ElopementRiskAiService elopementRiskAiService;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        medicationSafetyAiService = new MedicationSafetyAiService(chatClient, objectMapper);
        elopementRiskAiService = new ElopementRiskAiService(chatClient, objectMapper);
    }

    @Test
    @DisplayName("Nên phát hiện tương tác nghiêm trọng khi kết hợp Warfarin với Ibuprofen/Aspirin")
    void shouldDetectSevereInteractionWhenWarfarinCombinedWithNsaid() {
        MedicationCheckRequest request = MedicationCheckRequest.builder()
                .residentName("Eleanor Vance")
                .currentMedications(List.of("Warfarin 5mg", "Amlodipine 10mg"))
                .newMedication("Ibuprofen 400mg")
                .build();

        MedicationCheckResponse response = medicationSafetyAiService.checkSafety(request);

        assertNotNull(response);
        assertEquals("SEVERE", response.getRiskLevel());
        assertFalse(response.getIsSafeToAdminister());
        assertTrue(response.getSummaryWarning().contains("xuất huyết"));
        assertEquals(1, response.getInteractions().size());
    }

    @Test
    @DisplayName("Nên phát hiện chống chỉ định khi thuốc trùng với tiền sử dị ứng của cư dân")
    void shouldDetectAllergyContraindication() {
        MedicationCheckRequest request = MedicationCheckRequest.builder()
                .residentName("Arthur Pendelton")
                .allergies(List.of("Penicillin"))
                .newMedication("Penicillin V Potassium")
                .build();

        MedicationCheckResponse response = medicationSafetyAiService.checkSafety(request);

        assertNotNull(response);
        assertEquals("CONTRAINDICATED", response.getRiskLevel());
        assertFalse(response.getIsSafeToAdminister());
        assertTrue(response.getSummaryWarning().contains("dị ứng"));
    }

    @Test
    @DisplayName("Nên cảnh báo nguy cơ cao và kích hoạt chốt cửa an ninh khi cư dân Memory Care có xu hướng đi lạc")
    void shouldTriggerDoorLockAndHighRiskForMemoryCareWandering() {
        ElopementRiskRequest request = ElopementRiskRequest.builder()
                .residentName("Arthur Pendelton")
                .roomNumber("108")
                .cognitiveScore(14)
                .hasDementiaDiagnosis(true)
                .recentBehaviors(List.of("Đứng tìm cách mở cửa thoát hiểm", "Nói muốn tìm đường về nhà"))
                .timeOfDay("NIGHT")
                .build();

        ElopementRiskResponse response = elopementRiskAiService.evaluateRisk(request);

        assertNotNull(response);
        assertEquals("HIGH", response.getRiskLevel());
        assertTrue(response.getRiskScore() >= 8.0);
        assertTrue(response.getTriggerDoorLockAlert());
        assertTrue(response.getImmediateSafeguards().size() > 0);
    }

    @Test
    @DisplayName("Nên đánh giá mức độ Moderate cho sa sút trí tuệ nhẹ chưa có biểu hiện đi lạc")
    void shouldAssessModerateRiskForMildCognitiveDecline() {
        ElopementRiskRequest request = ElopementRiskRequest.builder()
                .residentName("Mildred Hayes")
                .roomNumber("315")
                .cognitiveScore(21)
                .hasDementiaDiagnosis(true)
                .recentBehaviors(List.of("Quên vị trí bàn ăn trưa"))
                .build();

        ElopementRiskResponse response = elopementRiskAiService.evaluateRisk(request);

        assertNotNull(response);
        assertEquals("MODERATE", response.getRiskLevel());
        assertFalse(response.getTriggerDoorLockAlert());
    }
}
