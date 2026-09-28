package com.eldercare.modules.ai.controller;

import com.eldercare.modules.ai.chat.dto.AiChatRequest;
import com.eldercare.modules.ai.chat.dto.AiChatResponse;
import com.eldercare.modules.ai.chat.service.AiChatService;
import com.eldercare.modules.ai.incident.dto.IncidentAnalysisRequest;
import com.eldercare.modules.ai.incident.dto.IncidentAnalysisResponse;
import com.eldercare.modules.ai.incident.service.IncidentAiService;
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

    @Value("${spring.ai.ollama.chat.options.model:qwen3.5:2b-q4_K_M}")
    private String modelName;

    @Value("${spring.ai.ollama.base-url:http://localhost:11434}")
    private String ollamaBaseUrl;

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "provider", "OLLAMA_LOCAL",
                "model", modelName,
                "endpoint", ollamaBaseUrl,
                "features", List.of(
                        "CHATBOT",
                        "INCIDENT_CLASSIFY",
                        "CARE_PLAN_SUGGEST",
                        "SHIFT_SUMMARY",
                        "VOICE_ASSISTANT",
                        "MEDICATION_SAFETY",
                        "ELOPEMENT_RISK"
                ),
                "message", "ElderCare AI Subsystem is active"
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
