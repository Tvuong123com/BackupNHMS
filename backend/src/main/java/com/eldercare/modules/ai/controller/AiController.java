package com.eldercare.modules.ai.controller;

import com.eldercare.modules.ai.chat.dto.AiChatRequest;
import com.eldercare.modules.ai.chat.dto.AiChatResponse;
import com.eldercare.modules.ai.chat.service.AiChatService;
import com.eldercare.modules.ai.incident.dto.IncidentAnalysisRequest;
import com.eldercare.modules.ai.incident.dto.IncidentAnalysisResponse;
import com.eldercare.modules.ai.incident.service.IncidentAiService;
import com.eldercare.modules.ai.risk.service.ResidentReviewPriorityService;
import com.eldercare.modules.ai.risk.service.FallScreeningService;
import com.eldercare.modules.ai.summary.service.ShiftSummaryDraftService;
import com.eldercare.modules.ai.rag.service.SemanticSearchService;
import com.eldercare.modules.ai.voice.service.VoiceObservationConfirmationService;
import com.eldercare.modules.ai.engine.AiSettingsDto;
import com.eldercare.modules.ai.engine.AiSettingsService;
import com.eldercare.modules.ai.engine.AiTestResultDto;
import com.eldercare.modules.ai.website.dto.WebsiteChatRequest;
import com.eldercare.modules.ai.website.dto.WebsiteChatResponse;
import com.eldercare.modules.ai.website.dto.ScreeningRequest;
import com.eldercare.modules.ai.website.dto.ScreeningResponse;
import com.eldercare.modules.ai.website.dto.FaqSearchRequest;
import com.eldercare.modules.ai.website.dto.FaqSearchResponse;
import com.eldercare.modules.ai.website.dto.CareQuizRequest;
import com.eldercare.modules.ai.website.dto.CareQuizResponse;
import com.eldercare.modules.ai.website.dto.MemoryRiskRequest;
import com.eldercare.modules.ai.website.dto.MemoryRiskResponse;
import com.eldercare.modules.ai.website.service.WebsiteChatService;
import com.eldercare.modules.ai.website.service.AdmissionScreenerAiService;
import com.eldercare.modules.ai.website.service.WebsiteFaqAiService;
import com.eldercare.modules.ai.website.service.CareQuizAiService;
import com.eldercare.modules.ai.website.service.MemoryRiskAiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiChatService aiChatService;
    private final IncidentAiService incidentAiService;
    private final ResidentReviewPriorityService residentReviewPriorityService;
    private final FallScreeningService fallScreeningService;
    private final com.eldercare.modules.ai.risk.service.DynamicFallRiskService dynamicFallRiskService;
    private final ShiftSummaryDraftService shiftSummaryDraftService;
    private final SemanticSearchService semanticSearchService;
    private final VoiceObservationConfirmationService voiceObservationConfirmationService;
    private final com.eldercare.modules.ai.careplan.service.CarePlanAiService carePlanAiService;
    private final com.eldercare.modules.ai.summary.service.SummaryAiService summaryAiService;
    private final com.eldercare.modules.ai.voice.service.VoiceCareAiService voiceCareAiService;
    private final com.eldercare.modules.ai.medication.service.MedicationSafetyAiService medicationSafetyAiService;
    private final com.eldercare.modules.ai.elopement.service.ElopementRiskAiService elopementRiskAiService;
    private final AiSettingsService aiSettingsService;
    private final WebsiteChatService websiteChatService;
    private final AdmissionScreenerAiService admissionScreenerAiService;
    private final WebsiteFaqAiService websiteFaqAiService;
    private final CareQuizAiService careQuizAiService;
    private final MemoryRiskAiService memoryRiskAiService;

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
                "status", "CONFIGURED",
                "provider", s.getProvider(),
                "model", s.getProvider().equals("GOOGLE_GEMINI") ? s.getGeminiModel() : s.getOllamaModel(),
                "endpoint", s.getProvider().equals("GOOGLE_GEMINI") ? "Google AI Studio Cloud" : s.getOllamaBaseUrl(),
                "features", List.of(
                        "CHATBOT",
                        "WEBSITE_CHATBOT",
                        "ADMISSION_SCREENER",
                        "INCIDENT_CLASSIFY",
                        "CARE_PLAN_SUGGEST",
                        "SHIFT_SUMMARY",
                        "VOICE_ASSISTANT",
                        "MEDICATION_SAFETY",
                        "ELOPEMENT_RISK",
                        "DYNAMIC_FALL_PREDICTION"
                ),
                "message", "AI provider configured; use Settings → Test Connection to verify availability"
        ));
    }

    @PostMapping("/chat")
    public ResponseEntity<AiChatResponse> chat(@RequestBody AiChatRequest request, Authentication authentication) {
        boolean canReadResidentRecords = authentication.getAuthorities().stream().anyMatch(a ->
                List.of("ROLE_System_Administrator", "ROLE_ADMIN", "ROLE_NHA_ADMIN",
                        "ROLE_NHA_Admin", "ROLE_DON", "ROLE_NURSE").contains(a.getAuthority()));
        return ResponseEntity.ok(aiChatService.chat(request, authentication.getName(), canReadResidentRecords));
    }

    @GetMapping("/chat/history")
    public ResponseEntity<List<AiChatService.ChatTurn>> chatHistory(
            @RequestParam String conversationId, Authentication authentication) {
        return ResponseEntity.ok(aiChatService.history(conversationId, authentication.getName()));
    }

    @GetMapping("/risk/review-priorities")
    public ResponseEntity<List<ResidentReviewPriorityService.ReviewPriority>> reviewPriorities() {
        return ResponseEntity.ok(residentReviewPriorityService.recentPriorities());
    }

    @GetMapping("/risk/fall-screenings/latest")
    public ResponseEntity<FallScreeningService.LatestResponse> latestFallScreening(@RequestParam Long residentId) {
        return ResponseEntity.ok(fallScreeningService.latest(residentId));
    }

    @PostMapping("/risk/fall-screenings")
    public ResponseEntity<FallScreeningService.ScreeningResult> recordFallScreening(
            @RequestBody FallScreeningService.ScreeningRequest request, Authentication authentication) {
        return ResponseEntity.ok(fallScreeningService.record(request, authentication.getName()));
    }

    @GetMapping({"/resident-chart-context/{residentId}", "/risk/resident-chart-context/{residentId}"})
    public ResponseEntity<com.eldercare.modules.ai.risk.dto.ResidentChartContextDto> getResidentChartContext(
            @PathVariable Long residentId) {
        return ResponseEntity.ok(dynamicFallRiskService.getResidentChartContext(residentId));
    }

    @GetMapping("/risk/fall-prediction/{residentId}")
    public ResponseEntity<com.eldercare.modules.ai.risk.dto.DynamicFallRiskDto> getDynamicFallRisk(
            @PathVariable Long residentId) {
        return ResponseEntity.ok(dynamicFallRiskService.calculateFallRisk(residentId));
    }

    @GetMapping("/risk/fall-predictions")
    public ResponseEntity<List<com.eldercare.modules.ai.risk.dto.DynamicFallRiskDto>> getAllDynamicFallRisks() {
        return ResponseEntity.ok(dynamicFallRiskService.calculateAllActiveResidents());
    }

    @GetMapping("/risk/fall-history/{residentId}")
    public ResponseEntity<com.eldercare.modules.ai.risk.dto.FallRiskHistoryDto> getFallRiskHistory(
            @PathVariable Long residentId,
            @RequestParam(defaultValue = "15") int limit) {
        return ResponseEntity.ok(dynamicFallRiskService.getResidentRiskHistory(residentId, limit));
    }

    @PostMapping("/risk/fall-snapshot/{residentId}")
    public ResponseEntity<com.eldercare.modules.ai.risk.entity.FallRiskSnapshotEntity> recordFallSnapshot(
            @PathVariable Long residentId, Authentication authentication) {
        String assessedBy = authentication != null ? authentication.getName() : "CLINICAL_STAFF";
        return ResponseEntity.ok(dynamicFallRiskService.recordSnapshot(residentId, assessedBy));
    }

    @PostMapping("/risk/fall-intervention/apply")
    public ResponseEntity<com.eldercare.modules.ai.risk.dto.ApplyFallInterventionResponse> applyFallIntervention(
            @Valid @RequestBody com.eldercare.modules.ai.risk.dto.ApplyFallInterventionRequest request,
            Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "CLINICAL_STAFF";
        return ResponseEntity.ok(dynamicFallRiskService.applyInterventionToCarePlan(request, username));
    }

    @GetMapping("/search")
    public ResponseEntity<List<SemanticSearchService.SearchHit>> semanticSearch(
            @RequestParam String q, @RequestParam(required = false) Long residentId,
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(semanticSearchService.search(q, residentId, limit));
    }

    @PostMapping("/search/reindex")
    public ResponseEntity<Map<String, Integer>> reindexKnowledge() {
        return ResponseEntity.ok(Map.of("indexed", semanticSearchService.reindex()));
    }

    // ═══════════════════════════════════════════════════════════════════
    //  PUBLIC WEBSITE AI ENDPOINTS (No authentication required)
    // ═══════════════════════════════════════════════════════════════════

    /** W1 — Website Family Concierge Chatbot (public, no auth) */
    @PostMapping("/website/chat")
    public ResponseEntity<WebsiteChatResponse> websiteChat(@Valid @RequestBody WebsiteChatRequest request) {
        return ResponseEntity.ok(websiteChatService.chat(request));
    }

    /** W2 — AI Admission Eligibility Pre-Screener (public, no auth) */
    @PostMapping("/website/screen")
    public ResponseEntity<ScreeningResponse> screenAdmission(@Valid @RequestBody ScreeningRequest request) {
        return ResponseEntity.ok(admissionScreenerAiService.screen(request));
    }

    /** W3 — AI FAQ Smart Assistant (public, no auth) */
    @PostMapping("/website/faq")
    public ResponseEntity<FaqSearchResponse> answerFaq(@Valid @RequestBody FaqSearchRequest request) {
        return ResponseEntity.ok(websiteFaqAiService.answerFaq(request));
    }

    /** W4 — AI Care Needs Assessment Quiz (public, no auth) */
    @PostMapping("/website/care-quiz")
    public ResponseEntity<CareQuizResponse> evaluateCareQuiz(@Valid @RequestBody CareQuizRequest request) {
        return ResponseEntity.ok(careQuizAiService.evaluate(request));
    }

    /** W6 — AI Memory Care Risk Explainer (public, no auth) */
    @PostMapping("/website/memory-risk")
    public ResponseEntity<MemoryRiskResponse> explainMemoryRisk(@Valid @RequestBody MemoryRiskRequest request) {
        return ResponseEntity.ok(memoryRiskAiService.explain(request));
    }

    // ═══════════════════════════════════════════════════════════════════
    //  ADMIN AI ENDPOINTS (typically behind auth in production)
    // ═══════════════════════════════════════════════════════════════════

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

    @PostMapping("/summary/records-draft")
    public ResponseEntity<ShiftSummaryDraftService.DraftResponse> draftFromRecords(
            @RequestBody ShiftSummaryDraftService.DraftRequest request, Authentication authentication) {
        if ("FAMILY".equalsIgnoreCase(request.audience()) && authentication.getAuthorities().stream()
                .noneMatch(a -> List.of("ROLE_System_Administrator", "ROLE_ADMIN", "ROLE_NHA_ADMIN",
                        "ROLE_NHA_Admin", "ROLE_DON", "ROLE_NURSE").contains(a.getAuthority()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Family update drafts require nursing review access");
        }
        return ResponseEntity.ok(shiftSummaryDraftService.createDraft(request, authentication.getName()));
    }

    @GetMapping("/summary/drafts")
    public ResponseEntity<List<ShiftSummaryDraftService.SavedDraft>> mySummaryDrafts(Authentication authentication) {
        return ResponseEntity.ok(shiftSummaryDraftService.myDrafts(authentication.getName()));
    }

    @PostMapping("/voice/parse")
    public ResponseEntity<com.eldercare.modules.ai.voice.dto.VoiceCareParseResponse> parseVoiceNote(
            @RequestBody com.eldercare.modules.ai.voice.dto.VoiceCareParseRequest request) {
        return ResponseEntity.ok(voiceCareAiService.parseSpokenCareNote(request));
    }

    @PostMapping("/voice/confirm")
    public ResponseEntity<VoiceObservationConfirmationService.ConfirmationResult> confirmVoiceObservation(
            @RequestBody VoiceObservationConfirmationService.Confirmation request, Authentication authentication) {
        return ResponseEntity.ok(voiceObservationConfirmationService.confirm(request, authentication.getName()));
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
