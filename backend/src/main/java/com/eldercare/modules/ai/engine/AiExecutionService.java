package com.eldercare.modules.ai.engine;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AiExecutionService {

    private final ChatClient chatClient;
    private final AiSettingsService settingsService;
    private final ObjectMapper objectMapper;
    private final ElderCareClinicalKnowledgeBase clinicalKnowledgeBase;
    private final RestClient restClient = RestClient.builder().requestFactory(requestFactory()).build();

    private static SimpleClientHttpRequestFactory requestFactory() {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(90000);
        return factory;
    }

    @Value("${ELDERCARE_AI_OLLAMA_WEBSITE_MODEL:eldercare-website}")
    private String websiteOllamaModel = "eldercare-website";

    public String execute(String specificSystemPrompt, String userPrompt) {
        return execute(specificSystemPrompt, userPrompt, true);
    }

    /** Public website prompts must not inherit internal clinical rules or protocols. */
    public String executePublic(String specificSystemPrompt, String userPrompt) {
        return execute(specificSystemPrompt, userPrompt, false);
    }

    private String execute(String specificSystemPrompt, String userPrompt, boolean includeClinicalContext) {
        AiSettingsDto settings = settingsService.getSettings();
        String fullSystemPrompt = buildSystemPrompt(settings, specificSystemPrompt, userPrompt, includeClinicalContext);

        long startTime = System.currentTimeMillis();

        // 2. Route to Google Gemini if configured
        if ("GOOGLE_GEMINI".equalsIgnoreCase(settings.getProvider())
                && settings.getGeminiApiKey() != null
                && !settings.getGeminiApiKey().isBlank()) {
            try {
                String geminiResult = callGemini(settings, fullSystemPrompt, userPrompt);
                if (geminiResult == null || geminiResult.isBlank()) {
                    throw new IllegalStateException("Gemini returned no answer");
                }
                long elapsed = System.currentTimeMillis() - startTime;
                log.info("Google Gemini ({}) responded in {}ms", settings.getGeminiModel(), elapsed);
                return geminiResult;
            } catch (Exception e) {
                log.error("Google Gemini execution failed ({}), falling back to local Ollama: {}", settings.getGeminiModel(), e.getMessage());
            }
        }

        // 3. Route to / Fallback to Local Ollama
        try {
            String ollamaResult = callOllama(settings, fullSystemPrompt, userPrompt, includeClinicalContext);
            if (ollamaResult == null || ollamaResult.isBlank()) {
                throw new IllegalStateException("Ollama returned no answer");
            }
            long elapsed = System.currentTimeMillis() - startTime;
            log.info("Local Ollama ({}) responded in {}ms",
                    includeClinicalContext ? settings.getOllamaModel() : websiteOllamaModel, elapsed);
            return ollamaResult;
        } catch (Exception e) {
            log.error("Local Ollama execution failed: {}", e.getMessage(), e);
            throw e;
        }
    }

    String buildSystemPrompt(AiSettingsDto settings, String specificSystemPrompt,
                             String userPrompt, boolean includeClinicalContext) {
        StringBuilder fullSystemPrompt = new StringBuilder();
        if (includeClinicalContext && settings.getSystemRules() != null && !settings.getSystemRules().isBlank()) {
            fullSystemPrompt.append(settings.getSystemRules().trim()).append("\n\n");
        }
        if (specificSystemPrompt != null && !specificSystemPrompt.isBlank()) {
            fullSystemPrompt.append(specificSystemPrompt.trim()).append("\n\n");
        }

        if (includeClinicalContext) {
            String grounding = clinicalKnowledgeBase.getGroundingContext(userPrompt);
            if (grounding != null && !grounding.isBlank()) {
                fullSystemPrompt.append(grounding).append("\n\n");
            }
            fullSystemPrompt.append("Staff safety boundary: Runtime workflow reminders are not approved clinical protocols. "
                    + "Do not invent diagnoses, medication doses, emergency schedules, or facts absent from the supplied record. "
                    + "Direct urgent decisions to the facility's approved policy and a licensed clinician.\n");
        }

        return fullSystemPrompt.toString();
    }

    private String callOllama(AiSettingsDto settings, String systemPrompt, String userPrompt,
                              boolean includeClinicalContext) {
        String baseUrl = (settings.getOllamaBaseUrl() != null && !settings.getOllamaBaseUrl().isBlank())
                ? settings.getOllamaBaseUrl() : "http://localhost:11434";
        String model = (settings.getOllamaModel() != null && !settings.getOllamaModel().isBlank())
                ? settings.getOllamaModel() : "qwen3.5:2b-q4_K_M";
        if (!includeClinicalContext) {
            model = websiteOllamaModel != null && !websiteOllamaModel.isBlank()
                    ? websiteOllamaModel : "eldercare-website";
        }

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("stream", false);
        body.put("think", false);

        List<Map<String, String>> messages = new ArrayList<>();
        if (systemPrompt != null && !systemPrompt.isBlank()) {
            messages.add(Map.of("role", "system", "content", systemPrompt));
        }
        messages.add(Map.of("role", "user", "content", userPrompt));
        body.put("messages", messages);

        Map<String, Object> options = new HashMap<>();
        options.put("temperature", settings.getTemperature() != null ? settings.getTemperature() : 0.2);
        options.put("num_predict", settings.getMaxTokens() != null && settings.getMaxTokens() > 512 ? settings.getMaxTokens() : 1024);
        body.put("options", options);

        try {
            String json = restClient.post()
                    .uri(baseUrl + "/api/chat")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(json);
            JsonNode messageNode = root.path("message");
            String content = messageNode.path("content").asText("");
            String thinking = messageNode.path("thinking").asText("");

            return cleanContent(content, thinking);
        } catch (Exception e) {
            if (!includeClinicalContext) {
                throw new IllegalStateException("Public Ollama model unavailable", e);
            }
            log.warn("Direct Ollama call failed ({}), attempting chatClient fallback: {}", baseUrl, e.getMessage());
            String fallback = chatClient.prompt()
                    .system(systemPrompt)
                    .user(userPrompt)
                    .call()
                    .content();
            return cleanContent(fallback, "");
        }
    }

    public String cleanContent(String content, String thinking) {
        if (content != null && !content.isBlank()) {
            String cleaned = content.replaceAll("(?s)<think>.*?</think>", "").trim();
            // Aggressively strip out leaked internal monologues & 'Thinking Process:'
            if (cleaned.toLowerCase().contains("thinking process:") || cleaned.toLowerCase().contains("thought process:")) {
                int markerIdx = Math.max(cleaned.toLowerCase().indexOf("thinking process:"), cleaned.toLowerCase().indexOf("thought process:"));
                int endOfThinking = cleaned.indexOf("\n\n**", markerIdx);
                if (endOfThinking == -1) endOfThinking = cleaned.indexOf("\n\n1.", markerIdx);
                if (endOfThinking == -1) endOfThinking = cleaned.indexOf("\n\n#", markerIdx);
                if (endOfThinking == -1) endOfThinking = cleaned.indexOf("\n\n•", markerIdx);
                if (endOfThinking != -1) {
                    cleaned = cleaned.substring(endOfThinking).trim();
                } else {
                    cleaned = cleaned.replaceAll("(?is)^#*\\s*(thinking process|thought process):?.*?(?=\\n\\n|\\Z)", "").trim();
                }
            }
            // Strip initial 'Analyze the Request:' headers if present
            cleaned = cleaned.replaceAll("(?is)^1\\.\\s*\\*\\*analyze the request:\\*\\*.*?(?=\\n\\n|\\Z)", "").trim();

            if (!cleaned.isBlank()) {
                return cleaned;
            }
        }
        if (thinking != null && !thinking.isBlank()) {
            String[] markers = {"Finalizing the Content:", "Final Polish:", "Revised Plan:", "Steps:"};
            for (String marker : markers) {
                int idx = thinking.lastIndexOf(marker);
                if (idx != -1) {
                    String extracted = thinking.substring(idx + marker.length()).trim();
                    if (!extracted.isBlank() && (extracted.contains("*") || extracted.contains("-") || extracted.contains("1."))) {
                        return extracted;
                    }
                }
            }
            int firstBullet = thinking.lastIndexOf("\n* ");
            if (firstBullet != -1) {
                int blockStart = thinking.lastIndexOf("\n\n", firstBullet);
                if (blockStart != -1) {
                    return thinking.substring(blockStart).trim();
                }
                return thinking.substring(firstBullet).trim();
            }
        }
        return "";
    }

    private String callGemini(AiSettingsDto settings, String systemPrompt, String userPrompt) {
        String primaryModel = (settings.getGeminiModel() != null && !settings.getGeminiModel().isBlank())
                ? settings.getGeminiModel().trim() : "gemini-2.5-flash";
        if (primaryModel.startsWith("models/")) {
            primaryModel = primaryModel.substring("models/".length());
        }
        String apiKey = settings.getGeminiApiKey();

        List<String> available = settingsService.listAvailableGeminiModels(apiKey);
        List<String> modelsToTry = new ArrayList<>();
        modelsToTry.add(primaryModel);
        if (!primaryModel.endsWith("-latest")) {
            modelsToTry.add(primaryModel + "-latest");
        }

        // Add discovered flash models
        for (String m : available) {
            if (m.toLowerCase().contains("flash") && !modelsToTry.contains(m)) {
                modelsToTry.add(m);
            }
        }
        // Add other discovered models
        for (String m : available) {
            if (!modelsToTry.contains(m)) {
                modelsToTry.add(m);
            }
        }

        // Hardcoded fallbacks if list is empty
        List<String> fallbacks = List.of("gemini-2.5-flash", "gemini-1.5-flash-latest", "gemini-flash-latest", "gemini-3.8-flash", "gemini-1.5-flash-8b");
        for (String fb : fallbacks) {
            if (!modelsToTry.contains(fb)) {
                modelsToTry.add(fb);
            }
        }

        Exception lastException = null;
        for (String model : modelsToTry) {
            for (String apiVer : List.of("v1beta", "v1")) {
                try {
                    String url = "https://generativelanguage.googleapis.com/" + apiVer + "/models/" + model + ":generateContent?key=" + apiKey;

                    Map<String, Object> requestBody = new HashMap<>();

                    if (systemPrompt != null && !systemPrompt.isBlank()) {
                        requestBody.put("system_instruction", Map.of(
                                "parts", List.of(Map.of("text", systemPrompt))
                        ));
                    }

                    requestBody.put("contents", List.of(
                            Map.of("role", "user", "parts", List.of(Map.of("text", userPrompt)))
                    ));

                    Map<String, Object> genConfig = new HashMap<>();
                    genConfig.put("temperature", settings.getTemperature() != null ? settings.getTemperature() : 0.2);
                    genConfig.put("maxOutputTokens", settings.getMaxTokens() != null ? settings.getMaxTokens() : 1024);
                    requestBody.put("generationConfig", genConfig);

                    String jsonResponse = restClient.post()
                            .uri(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(requestBody)
                            .retrieve()
                            .body(String.class);

                    if (!model.equalsIgnoreCase(primaryModel)) {
                        log.warn("Primary model {} was unavailable, smoothly fulfilled request using fallback model {} [{}]", primaryModel, model, apiVer);
                    }
                    return cleanContent(parseGeminiResponse(jsonResponse), "");
                } catch (Exception e) {
                    lastException = e;
                    log.warn("Gemini model {} on {} failed ({}). Attempting next candidate...", model, apiVer, e.getMessage());
                    if (e.getMessage() != null && (e.getMessage().contains("503") || e.getMessage().contains("429"))) {
                        break; // Skip to next model
                    }
                }
            }
        }

        throw new RuntimeException("All Google Gemini models failed. Last error: " + (lastException != null ? lastException.getMessage() : "unknown"));
    }

    private String parseGeminiResponse(String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode candidates = root.path("candidates");
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode parts = candidates.get(0).path("content").path("parts");
                if (parts.isArray() && parts.size() > 0) {
                    return parts.get(0).path("text").asText("");
                }
            }
            return "";
        } catch (Exception e) {
            log.error("Failed to parse Gemini response: {}", e.getMessage());
            throw new RuntimeException("Malformed Gemini API response: " + e.getMessage());
        }
    }
}
