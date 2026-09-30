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
                        currentSettings.setGeminiModel("gemini-2.5-flash");
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
                .geminiModel("gemini-2.5-flash")
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
            geminiModel = "gemini-2.5-flash";
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

    public List<String> listAvailableGeminiModels(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            return List.of();
        }

        List<String> endpoints = List.of(
                "https://generativelanguage.googleapis.com/v1beta/models?key=" + apiKey,
                "https://generativelanguage.googleapis.com/v1/models?key=" + apiKey
        );

        for (String url : endpoints) {
            try {
                String responseBody = restClient.get()
                        .uri(url)
                        .retrieve()
                        .body(String.class);

                if (responseBody != null && !responseBody.isBlank()) {
                    com.fasterxml.jackson.databind.JsonNode root = objectMapper.readTree(responseBody);
                    com.fasterxml.jackson.databind.JsonNode modelsNode = root.path("models");
                    if (modelsNode.isArray() && modelsNode.size() > 0) {
                        List<String> modelNames = new ArrayList<>();
                        for (com.fasterxml.jackson.databind.JsonNode m : modelsNode) {
                            com.fasterxml.jackson.databind.JsonNode methodsNode = m.path("supportedGenerationMethods");
                            boolean supportsGenerate = false;
                            if (methodsNode.isArray()) {
                                for (com.fasterxml.jackson.databind.JsonNode method : methodsNode) {
                                    if ("generateContent".equalsIgnoreCase(method.asText())) {
                                        supportsGenerate = true;
                                        break;
                                    }
                                }
                            }
                            if (supportsGenerate) {
                                String name = m.path("name").asText("");
                                if (name.startsWith("models/")) {
                                    name = name.substring("models/".length());
                                }
                                if (!name.isBlank() && !modelNames.contains(name)) {
                                    modelNames.add(name);
                                }
                            }
                        }
                        if (!modelNames.isEmpty()) {
                            log.info("Discovered {} available Gemini models for API key: {}", modelNames.size(), modelNames);
                            return modelNames;
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("Failed to fetch models from {}: {}", url, e.getMessage());
            }
        }

        return List.of();
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
                    ? testConfig.getGeminiModel().trim() : "gemini-2.5-flash";
            if (requestedModel.startsWith("models/")) {
                requestedModel = requestedModel.substring("models/".length());
            }

            // 1. Discover actual models enabled for this API key via Google's ListModels
            List<String> discoveredModels = listAvailableGeminiModels(apiKey);

            // 2. Prioritize testing the requested model, then its -latest alias, then discovered flash models
            List<String> modelsToTest = new ArrayList<>();
            modelsToTest.add(requestedModel);
            if (!requestedModel.endsWith("-latest")) {
                modelsToTest.add(requestedModel + "-latest");
            }

            for (String m : discoveredModels) {
                if (m.toLowerCase().contains("flash") && !modelsToTest.contains(m)) {
                    modelsToTest.add(m);
                }
            }
            for (String m : discoveredModels) {
                if (!modelsToTest.contains(m)) {
                    modelsToTest.add(m);
                }
            }

            // Standard fallback candidates in case discovery was empty
            List<String> standardFallbacks = List.of(
                    "gemini-2.5-flash",
                    "gemini-1.5-flash-latest",
                    "gemini-flash-latest",
                    "gemini-3.8-flash",
                    "gemini-1.5-flash-8b",
                    "gemini-1.5-pro-latest"
            );
            for (String fb : standardFallbacks) {
                if (!modelsToTest.contains(fb)) {
                    modelsToTest.add(fb);
                }
            }

            Exception lastEx = null;
            for (String model : modelsToTest) {
                // Test across v1beta and v1 API versions
                for (String apiVer : List.of("v1beta", "v1")) {
                    try {
                        String testUrl = "https://generativelanguage.googleapis.com/" + apiVer + "/models/" + model + ":generateContent?key=" + apiKey;
                        Map<String, Object> payload = Map.of(
                                "contents", List.of(
                                        Map.of("role", "user", "parts", List.of(Map.of("text", "Respond with exact word 'ONLINE'")))
                                )
                        );

                        restClient.post()
                                .uri(testUrl)
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                .body(payload)
                                .retrieve()
                                .body(Map.class);

                        long latency = System.currentTimeMillis() - start;
                        String message;
                        if (model.equalsIgnoreCase(requestedModel)) {
                            message = "Successfully connected to Google AI Studio (" + model + " [" + apiVer + "]) in " + latency + "ms";
                        } else {
                            message = "Model '" + requestedModel + "' was unavailable. Smoothly connected using active model '" + model + "' in " + latency + "ms!";
                        }

                        // Auto-update memory settings to the working model
                        currentSettings.setGeminiModel(model);

                        return AiTestResultDto.builder()
                                .success(true)
                                .latencyMs(latency)
                                .provider("GOOGLE_GEMINI")
                                .model(model)
                                .message(message)
                                .availableModels(discoveredModels)
                                .build();
                    } catch (Exception e) {
                        lastEx = e;
                        log.warn("Gemini test for model {} on {} failed: {}. Continuing search...", model, apiVer, e.getMessage());
                        if (e.getMessage() != null && (e.getMessage().contains("503") || e.getMessage().contains("429"))) {
                            break; // Skip to next model if server is overloaded
                        }
                    }
                }
            }

            long latency = System.currentTimeMillis() - start;
            String errMsg = lastEx != null ? lastEx.getMessage() : "Unknown error";
            if (errMsg.contains("503") || errMsg.contains("high demand")) {
                errMsg = "Google Gemini models are temporarily experiencing high demand (503). Please retry in a moment.";
            } else if (errMsg.contains("404")) {
                if (!discoveredModels.isEmpty()) {
                    errMsg = "Model '" + requestedModel + "' is not supported by your API key. Available models: " + String.join(", ", discoveredModels);
                } else {
                    errMsg = "Google AI Studio: Model '" + requestedModel + "' not found. Try 'gemini-2.5-flash' or 'gemini-1.5-flash-latest'.";
                }
            }

            return AiTestResultDto.builder()
                    .success(false)
                    .latencyMs(latency)
                    .provider("GOOGLE_GEMINI")
                    .model(requestedModel)
                    .message("Google AI Studio: " + errMsg)
                    .availableModels(discoveredModels)
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
