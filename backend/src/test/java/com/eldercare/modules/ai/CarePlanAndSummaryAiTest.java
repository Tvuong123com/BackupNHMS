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
import com.eldercare.modules.ai.engine.AiExecutionService;
import com.eldercare.modules.resident_intake.resident.repository.ResidentRepository;
import com.eldercare.modules.resident_intake.assessment.repository.AssessmentRepository;
import com.eldercare.modules.risk_incident.incident_tracking.repository.IncidentRepository;
import com.eldercare.modules.resident_intake.resident.repository.ClinicalRecordRepository;
import com.eldercare.modules.resident_intake.resident_profile.ResidentEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CarePlanAndSummaryAiTest {

    @Mock
    private AiExecutionService aiExecutionService;

    private CarePlanAiService carePlanAiService;
    private SummaryAiService summaryAiService;
    private VoiceCareAiService voiceCareAiService;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        carePlanAiService = new CarePlanAiService(aiExecutionService, objectMapper,
                mock(ResidentRepository.class), mock(AssessmentRepository.class),
                mock(IncidentRepository.class), mock(ClinicalRecordRepository.class));
        summaryAiService = new SummaryAiService(aiExecutionService, objectMapper);
        voiceCareAiService = new VoiceCareAiService(aiExecutionService, objectMapper);
    }

    @Test
    @DisplayName("Clinical suggestions require a real resident record")
    void shouldRejectUnidentifiedResident() {
        CarePlanSuggestionRequest request = CarePlanSuggestionRequest.builder()
                .residentName("Eleanor Vance")
                .age(82)
                .careLevel("Skilled Nursing")
                .adlScore(22)
                .recentIncidents(List.of("Té ngã tại phòng vệ sinh tuần trước"))
                .build();

        assertThrows(IllegalArgumentException.class, () -> carePlanAiService.suggestCarePlan(request));
    }

    @Test
    @DisplayName("Unavailable AI does not invent clinical goals or interventions")
    void shouldNotInventGoalsOnAiFailure() {
        ResidentRepository residents = mock(ResidentRepository.class);
        when(residents.findByIdAndIsDeletedFalse(7L)).thenReturn(Optional.of(
                ResidentEntity.builder().id(7L).firstName("Lan").lastName("Nguyen").build()));
        when(aiExecutionService.execute(anyString(), anyString()))
                .thenThrow(new IllegalStateException("unavailable"));
        var service = new CarePlanAiService(aiExecutionService, new ObjectMapper(), residents,
                mock(AssessmentRepository.class), mock(IncidentRepository.class),
                mock(ClinicalRecordRepository.class));

        var response = service.suggestCarePlan(CarePlanSuggestionRequest.builder().residentId(7L).build());

        assertTrue(response.getSuggestedGoals().isEmpty());
        assertTrue(response.getResidentSummary().contains("chưa tạo được"));
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
