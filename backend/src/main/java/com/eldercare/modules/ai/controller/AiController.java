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

import java.util.Map;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AiController {

    private final AiChatService aiChatService;
    private final IncidentAiService incidentAiService;

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
}
