package com.eldercare.modules.ai.engine;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
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
    private final RestClient restClient = RestClient.builder().build();

    public String execute(String specificSystemPrompt, String userPrompt) {
        AiSettingsDto settings = settingsService.getSettings();

        // 1. Build combined clinical system prompt with global operational rules
        StringBuilder fullSystemPrompt = new StringBuilder();
        if (settings.getSystemRules() != null && !settings.getSystemRules().isBlank()) {
            fullSystemPrompt.append(settings.getSystemRules().trim()).append("\n\n");
        }
        if (specificSystemPrompt != null && !specificSystemPrompt.isBlank()) {
            fullSystemPrompt.append(specificSystemPrompt.trim());
        }

        long startTime = System.currentTimeMillis();

        // 2. Route to Google Gemini if configured
        if ("GOOGLE_GEMINI".equalsIgnoreCase(settings.getProvider())
                && settings.getGeminiApiKey() != null
                && !settings.getGeminiApiKey().isBlank()) {
            try {
                String geminiResult = callGemini(settings, fullSystemPrompt.toString(), userPrompt);
                long elapsed = System.currentTimeMillis() - startTime;
                log.info("Google Gemini ({}) responded in {}ms", settings.getGeminiModel(), elapsed);
                return geminiResult;
            } catch (Exception e) {
                log.error("Google Gemini execution failed ({}), falling back to local Ollama: {}", settings.getGeminiModel(), e.getMessage());
            }
        }

        // 3. Route to / Fallback to Local Ollama
        try {
            String ollamaResult = callOllama(settings, fullSystemPrompt.toString(), userPrompt);
            long elapsed = System.currentTimeMillis() - startTime;
            log.info("Local Ollama ({}) responded in {}ms", settings.getOllamaModel(), elapsed);
            return ollamaResult;
        } catch (Exception e) {
            log.error("Local Ollama execution failed: {}", e.getMessage(), e);
            throw e;
        }
    }

    private String callOllama(AiSettingsDto settings, String systemPrompt, String userPrompt) {
        String baseUrl = (settings.getOllamaBaseUrl() != null && !settings.getOllamaBaseUrl().isBlank())
                ? settings.getOllamaBaseUrl() : "http://localhost:11434";
        String model = (settings.getOllamaModel() != null && !settings.getOllamaModel().isBlank())
                ? settings.getOllamaModel() : "qwen3.5:2b-q4_K_M";

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("stream", false);

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
            log.warn("Direct Ollama call failed ({}), attempting chatClient fallback: {}", baseUrl, e.getMessage());
            String fallback = chatClient.prompt()
                    .system(systemPrompt)
                    .user(userPrompt)
                    .call()
                    .content();
            return cleanContent(fallback, "");
        }
    }

    private String cleanContent(String content, String thinking) {
        if (content != null && !content.isBlank()) {
            String cleaned = content.replaceAll("(?s)<think>.*?</think>", "").trim();
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
        return "Clinical protocol analysis completed.";
    }

    private String callGemini(AiSettingsDto settings, String systemPrompt, String userPrompt) {
        String model = (settings.getGeminiModel() != null && !settings.getGeminiModel().isBlank())
                ? settings.getGeminiModel() : "gemini-3.8-flash";
        String apiKey = settings.getGeminiApiKey();

        String url = "https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent?key=" + apiKey;

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

        return parseGeminiResponse(jsonResponse);
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
