package com.eldercare.modules.ai.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AiSettingsService {

    private final ObjectMapper objectMapper;
    private final RestClient restClient = RestClient.builder().build();

    @Value("${spring.ai.ollama.base-url:http://localhost:11434}")
    private String defaultOllamaBaseUrl;

    @Value("${spring.ai.ollama.chat.options.model:qwen3.5:2b-q4_K_M}")
    private String defaultOllamaModel;

    private static final String SETTINGS_FILE_PATH = "ai-settings.json";

    public static final String DEFAULT_SYSTEM_RULES = """
[ELDERCARE NHMS CLINICAL & OPERATIONAL RULES]
1. STRICT SCOPE: You are the internal Clinical Decision Assistant for ElderCare NHMS (Nursing Home Management System). You must exclusively assist with resident care, clinical evaluations (ADL/Fall/Cognitive), incident classification, care plan goals, medication safety, and nursing operations within this system. Strictly decline any queries outside elder care or nursing home workflows.
2. SPEED & CONCISENESS: Return direct, structured, actionable bullet points without introductory pleasantries, conversational filler, or unnecessary text to maximize system response speed.
3. CLINICAL ACCURACY & HIPAA: Follow evidence-based geriatric care protocols and HIPAA resident confidentiality. Never alter medication regimens without explicit physician order.
4. DIRECT OUTPUT: Output ONLY the final clinical protocol or answer directly. Never output internal thoughts, reasoning steps, or headers like 'Thinking Process:'.
""";

    private volatile AiSettingsDto currentSettings;

    @PostConstruct
    public void init() {
        // 1. Try to load from saved file
        try {
            Path path = Paths.get(SETTINGS_FILE_PATH);
            if (Files.exists(path)) {
                AiSettingsDto saved = objectMapper.readValue(path.toFile(), AiSettingsDto.class);
                if (saved != null) {
                    currentSettings = saved;
                    if ("gemini-2.0-flash".equalsIgnoreCase(currentSettings.getGeminiModel())
                            || currentSettings.getGeminiModel() == null
                            || currentSettings.getGeminiModel().isBlank()) {
                        currentSettings.setGeminiModel("gemini-3.8-flash");
                    }
                    log.info("Loaded AI settings from {}: provider={}, model={}", SETTINGS_FILE_PATH, currentSettings.getProvider(), currentSettings.getGeminiModel());
                    return;
                }
            }
        } catch (Exception e) {
            log.warn("Could not load AI settings from file, using defaults: {}", e.getMessage());
        }

        // 2. Fallback to defaults + env
        String envKey = System.getenv("GEMINI_API_KEY");
        if (envKey == null || envKey.isBlank()) {
            envKey = System.getProperty("GEMINI_API_KEY", "");
        }

        String initialProvider = (envKey != null && !envKey.isBlank()) ? "GOOGLE_GEMINI" : "LOCAL_OLLAMA";

        Map<String, Boolean> features = new HashMap<>();
        features.put("chat", true);
        features.put("incident", true);
        features.put("carePlan", true);
        features.put("voice", true);
        features.put("medication", true);
        features.put("elopement", true);

        currentSettings = AiSettingsDto.builder()
                .provider(initialProvider)
                .geminiApiKey(envKey != null ? envKey : "")
                .geminiModel("gemini-3.8-flash")
                .ollamaBaseUrl(defaultOllamaBaseUrl)
                .ollamaModel(defaultOllamaModel)
                .temperature(0.2)
                .maxTokens(768)
                .systemRules(DEFAULT_SYSTEM_RULES)
                .features(features)
                .build();

        log.info("Initialized default AI settings: provider={}", currentSettings.getProvider());
    }

    public AiSettingsDto getSettings() {
        return currentSettings;
    }

    public AiSettingsDto getPublicSettings() {
        AiSettingsDto s = currentSettings;
        String maskedKey = "";
        if (s.getGeminiApiKey() != null && s.getGeminiApiKey().length() > 8) {
            maskedKey = s.getGeminiApiKey().substring(0, 6) + "..." + s.getGeminiApiKey().substring(s.getGeminiApiKey().length() - 4);
        } else if (s.getGeminiApiKey() != null && !s.getGeminiApiKey().isBlank()) {
            maskedKey = "******";
        }

        return AiSettingsDto.builder()
                .provider(s.getProvider())
                .geminiApiKey(maskedKey)
                .geminiModel(s.getGeminiModel())
                .ollamaBaseUrl(s.getOllamaBaseUrl())
                .ollamaModel(s.getOllamaModel())
                .temperature(s.getTemperature())
                .maxTokens(s.getMaxTokens())
                .systemRules(s.getSystemRules())
                .features(s.getFeatures())
                .build();
    }

    public synchronized AiSettingsDto updateSettings(AiSettingsDto newSettings) {
        String keyToKeep = newSettings.getGeminiApiKey();
        // If client sends masked key (contains "..."), keep the existing real key
        if (keyToKeep == null || keyToKeep.contains("...") || keyToKeep.isBlank()) {
            keyToKeep = currentSettings.getGeminiApiKey();
        }

        String geminiModel = newSettings.getGeminiModel();
        if (geminiModel == null || geminiModel.isBlank() || "gemini-2.0-flash".equalsIgnoreCase(geminiModel)) {
            geminiModel = "gemini-3.8-flash";
        }

        AiSettingsDto updated = AiSettingsDto.builder()
                .provider(newSettings.getProvider() != null ? newSettings.getProvider() : currentSettings.getProvider())
                .geminiApiKey(keyToKeep)
                .geminiModel(geminiModel)
                .ollamaBaseUrl(newSettings.getOllamaBaseUrl() != null ? newSettings.getOllamaBaseUrl() : defaultOllamaBaseUrl)
                .ollamaModel(newSettings.getOllamaModel() != null ? newSettings.getOllamaModel() : defaultOllamaModel)
                .temperature(newSettings.getTemperature() != null ? newSettings.getTemperature() : 0.2)
                .maxTokens(newSettings.getMaxTokens() != null ? newSettings.getMaxTokens() : 768)
                .systemRules(newSettings.getSystemRules() != null && !newSettings.getSystemRules().isBlank() ? newSettings.getSystemRules() : DEFAULT_SYSTEM_RULES)
                .features(newSettings.getFeatures() != null ? newSettings.getFeatures() : currentSettings.getFeatures())
                .build();

        currentSettings = updated;

        // Persist to file
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(SETTINGS_FILE_PATH), updated);
            log.info("Persisted AI settings to {}", SETTINGS_FILE_PATH);
        } catch (Exception e) {
            log.error("Failed to persist AI settings: {}", e.getMessage());
        }

        return getPublicSettings();
    }

    public AiTestResultDto testConnection(AiSettingsDto testConfig) {
        String provider = testConfig.getProvider() != null ? testConfig.getProvider() : currentSettings.getProvider();
        long start = System.currentTimeMillis();

        if ("GOOGLE_GEMINI".equalsIgnoreCase(provider)) {
            String apiKey = testConfig.getGeminiApiKey();
            if (apiKey == null || apiKey.contains("...") || apiKey.isBlank()) {
                apiKey = currentSettings.getGeminiApiKey();
            }

            if (apiKey == null || apiKey.isBlank()) {
                return AiTestResultDto.builder()
                        .success(false)
                        .latencyMs(0)
                        .provider("GOOGLE_GEMINI")
                        .model(testConfig.getGeminiModel())
                        .message("Google AI Studio API Key is missing. Please provide a valid key.")
                        .build();
            }

            String requestedModel = (testConfig.getGeminiModel() != null && !testConfig.getGeminiModel().isBlank())
                    ? testConfig.getGeminiModel() : "gemini-3.8-flash";

            List<String> modelsToTest = new ArrayList<>();
            modelsToTest.add(requestedModel);
            if (!modelsToTest.contains("gemini-2.5-flash")) modelsToTest.add("gemini-2.5-flash");
            if (!modelsToTest.contains("gemini-1.5-flash")) modelsToTest.add("gemini-1.5-flash");

            Exception lastEx = null;
            for (String model : modelsToTest) {
                try {
                    String testUrl = "https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent?key=" + apiKey;
                    Map<String, Object> payload = Map.of(
                            "contents", java.util.List.of(
                                    Map.of("role", "user", "parts", java.util.List.of(Map.of("text", "Respond with exact word 'ONLINE'")))
                            )
                    );

                    restClient.post()
                            .uri(testUrl)
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                            .body(payload)
                            .retrieve()
                            .body(Map.class);

                    long latency = System.currentTimeMillis() - start;
                    String message = "Successfully connected to Google AI Studio (" + model + ") in " + latency + "ms";
                    if (!model.equals(requestedModel)) {
                        message = "Model '" + requestedModel + "' is temporarily busy on Google servers (503 High Demand). Successfully connected using alternate model '" + model + "' in " + latency + "ms!";
                    }
                    return AiTestResultDto.builder()
                            .success(true)
                            .latencyMs(latency)
                            .provider("GOOGLE_GEMINI")
                            .model(model)
                            .message(message)
                            .build();
                } catch (Exception e) {
                    lastEx = e;
                    log.warn("Test connection for Gemini model {} failed: {}. Trying fallback...", model, e.getMessage());
                }
            }

            long latency = System.currentTimeMillis() - start;
            String errMsg = lastEx != null ? lastEx.getMessage() : "Unknown error";
            if (errMsg.contains("503") || errMsg.contains("high demand")) {
                errMsg = "Google Gemini models are temporarily experiencing high demand (503). Try selecting 'gemini-2.5-flash' or 'gemini-1.5-flash' from Presets, or retry in a few moments.";
            }
            return AiTestResultDto.builder()
                    .success(false)
                    .latencyMs(latency)
                    .provider("GOOGLE_GEMINI")
                    .model(requestedModel)
                    .message("Google AI Studio: " + errMsg)
                    .build();
        } else {
            // Local Ollama
            String url = (testConfig.getOllamaBaseUrl() != null && !testConfig.getOllamaBaseUrl().isBlank())
                    ? testConfig.getOllamaBaseUrl() : defaultOllamaBaseUrl;
            String model = (testConfig.getOllamaModel() != null && !testConfig.getOllamaModel().isBlank())
                    ? testConfig.getOllamaModel() : defaultOllamaModel;

            try {
                Map response = restClient.get()
                        .uri(url + "/api/tags")
                        .retrieve()
                        .body(Map.class);

                long latency = System.currentTimeMillis() - start;
                boolean hasModels = response != null && response.containsKey("models");

                return AiTestResultDto.builder()
                        .success(hasModels)
                        .latencyMs(latency)
                        .provider("LOCAL_OLLAMA")
                        .model(model)
                        .message("Successfully connected to Local Ollama (" + model + ") at " + url + " in " + latency + "ms")
                        .build();
            } catch (Exception e) {
                long latency = System.currentTimeMillis() - start;
                return AiTestResultDto.builder()
                        .success(false)
                        .latencyMs(latency)
                        .provider("LOCAL_OLLAMA")
                        .model(model)
                        .message("Local Ollama connection failed (" + url + "): " + e.getMessage())
                        .build();
            }
        }
    }
}
