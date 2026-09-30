package com.eldercare.modules.ai.controller;

import com.eldercare.modules.ai.chat.dto.AiChatRequest;
import com.eldercare.modules.ai.chat.dto.AiChatResponse;
import com.eldercare.modules.ai.chat.service.AiChatService;
import com.eldercare.modules.ai.incident.dto.IncidentAnalysisRequest;
import com.eldercare.modules.ai.incident.dto.IncidentAnalysisResponse;
import com.eldercare.modules.ai.incident.service.IncidentAiService;
import com.eldercare.modules.ai.engine.AiSettingsDto;
import com.eldercare.modules.ai.engine.AiSettingsService;
import com.eldercare.modules.ai.engine.AiTestResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AiController {

    private final AiChatService aiChatService;
    private final IncidentAiService incidentAiService;
    private final com.eldercare.modules.ai.careplan.service.CarePlanAiService carePlanAiService;
    private final com.eldercare.modules.ai.summary.service.SummaryAiService summaryAiService;
    private final com.eldercare.modules.ai.voice.service.VoiceCareAiService voiceCareAiService;
    private final com.eldercare.modules.ai.medication.service.MedicationSafetyAiService medicationSafetyAiService;
    private final com.eldercare.modules.ai.elopement.service.ElopementRiskAiService elopementRiskAiService;
    private final AiSettingsService aiSettingsService;

    @GetMapping("/settings")
    public ResponseEntity<AiSettingsDto> getSettings() {
        return ResponseEntity.ok(aiSettingsService.getPublicSettings());
    }

    @PutMapping("/settings")
    public ResponseEntity<AiSettingsDto> updateSettings(@RequestBody AiSettingsDto newSettings) {
        return ResponseEntity.ok(aiSettingsService.updateSettings(newSettings));
    }

    @PostMapping("/settings/test")
    public ResponseEntity<AiTestResultDto> testConnection(@RequestBody AiSettingsDto testConfig) {
        return ResponseEntity.ok(aiSettingsService.testConnection(testConfig));
    }

    @GetMapping("/models")
    public ResponseEntity<Map<String, Object>> getAvailableModels(@RequestParam(required = false) String apiKey) {
        String keyToUse = (apiKey != null && !apiKey.isBlank() && !apiKey.contains("..."))
                ? apiKey : aiSettingsService.getSettings().getGeminiApiKey();
        List<String> models = aiSettingsService.listAvailableGeminiModels(keyToUse);
        return ResponseEntity.ok(Map.of("models", models));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        AiSettingsDto s = aiSettingsService.getSettings();
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "provider", s.getProvider(),
                "model", s.getProvider().equals("GOOGLE_GEMINI") ? s.getGeminiModel() : s.getOllamaModel(),
                "endpoint", s.getProvider().equals("GOOGLE_GEMINI") ? "Google AI Studio Cloud" : s.getOllamaBaseUrl(),
                "features", List.of(
                        "CHATBOT",
                        "INCIDENT_CLASSIFY",
                        "CARE_PLAN_SUGGEST",
                        "SHIFT_SUMMARY",
                        "VOICE_ASSISTANT",
                        "MEDICATION_SAFETY",
                        "ELOPEMENT_RISK"
                ),
                "message", "ElderCare AI Subsystem is active (" + s.getProvider() + ")"
        ));
    }

    @PostMapping("/chat")
    public ResponseEntity<AiChatResponse> chat(@RequestBody AiChatRequest request) {
        return ResponseEntity.ok(aiChatService.chat(request));
    }

    @PostMapping("/incident/classify")
    public ResponseEntity<IncidentAnalysisResponse> classifyIncident(@RequestBody IncidentAnalysisRequest request) {
        return ResponseEntity.ok(incidentAiService.classifyIncident(request));
    }

    @PostMapping("/careplan/suggest")
    public ResponseEntity<com.eldercare.modules.ai.careplan.dto.CarePlanSuggestionResponse> suggestCarePlan(
            @RequestBody com.eldercare.modules.ai.careplan.dto.CarePlanSuggestionRequest request) {
        return ResponseEntity.ok(carePlanAiService.suggestCarePlan(request));
    }

    @PostMapping("/summary/generate")
    public ResponseEntity<com.eldercare.modules.ai.summary.dto.SummaryResponse> generateSummary(
            @RequestBody com.eldercare.modules.ai.summary.dto.SummaryRequest request) {
        return ResponseEntity.ok(summaryAiService.generateSummary(request));
    }

    @PostMapping("/voice/parse")
    public ResponseEntity<com.eldercare.modules.ai.voice.dto.VoiceCareParseResponse> parseVoiceNote(
            @RequestBody com.eldercare.modules.ai.voice.dto.VoiceCareParseRequest request) {
        return ResponseEntity.ok(voiceCareAiService.parseSpokenCareNote(request));
    }

    @PostMapping("/medication/check")
    public ResponseEntity<com.eldercare.modules.ai.medication.dto.MedicationCheckResponse> checkMedicationSafety(
            @RequestBody com.eldercare.modules.ai.medication.dto.MedicationCheckRequest request) {
        return ResponseEntity.ok(medicationSafetyAiService.checkSafety(request));
    }

    @PostMapping("/elopement/evaluate")
    public ResponseEntity<com.eldercare.modules.ai.elopement.dto.ElopementRiskResponse> evaluateElopementRisk(
            @RequestBody com.eldercare.modules.ai.elopement.dto.ElopementRiskRequest request) {
        return ResponseEntity.ok(elopementRiskAiService.evaluateRisk(request));
    }
}
