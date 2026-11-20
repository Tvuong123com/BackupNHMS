package com.eldercare.config;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;

/** Starts the workstation's local Ollama service for direct Maven development runs. */
public final class LocalOllamaBootstrap {
    private static final String API_URL = "http://127.0.0.1:11434/api/tags";
    private static final String DEFAULT_EXE = "D:\\CreateAssetGamer\\tools\\ai\\ollama\\ollama.exe";
    private static final String DEFAULT_MODELS = "D:\\CreateAssetGamer\\tools\\ai\\models\\ollama";

    private LocalOllamaBootstrap() {}

    public static void startIfNeeded() {
        String profiles = setting("SPRING_PROFILES_ACTIVE", "dev");
        boolean dev = Arrays.stream(profiles.split(",")).anyMatch(profile -> profile.trim().equalsIgnoreCase("dev"));
        if (!dev || !System.getProperty("os.name", "").toLowerCase().contains("windows")
                || !Boolean.parseBoolean(setting("ELDERCARE_AI_OLLAMA_AUTOSTART", "true"))) {
            return;
        }

        String baseUrl = setting("OLLAMA_BASE_URL", "http://127.0.0.1:11434").replaceAll("/+$", "");
        if (!baseUrl.equalsIgnoreCase("http://localhost:11434")
                && !baseUrl.equalsIgnoreCase("http://127.0.0.1:11434")) {
            return;
        }

        Path executable = Path.of(setting("OLLAMA_EXE", DEFAULT_EXE));
        Path models = Path.of(setting("OLLAMA_MODELS", DEFAULT_MODELS));
        if (!Files.isRegularFile(executable) || !Files.isDirectory(models)) {
            System.err.println("Local Ollama auto-start skipped: executable or model store is missing.");
            return;
        }

        try (HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(1)).build()) {
            if (responds(client)) {
                System.out.println("Local Ollama is already running at " + baseUrl);
                return;
            }

            ProcessBuilder builder = new ProcessBuilder(executable.toString(), "serve");
            builder.environment().put("OLLAMA_MODELS", models.toString());
            builder.environment().put("OLLAMA_HOST", "127.0.0.1:11434");
            builder.environment().put("OLLAMA_LLM_LIBRARY", "cpu");
            builder.environment().put("CUDA_VISIBLE_DEVICES", "-1");
            builder.redirectOutput(ProcessBuilder.Redirect.DISCARD);
            builder.redirectError(ProcessBuilder.Redirect.DISCARD);
            Process process = builder.start();

            for (int attempt = 0; attempt < 20; attempt++) {
                if (responds(client)) {
                    System.out.println("Local Ollama started in CPU mode (PID " + process.pid() + ").");
                    return;
                }
                if (!process.isAlive()) break;
                Thread.sleep(500);
            }
            System.err.println("Local Ollama did not become ready; backend will continue without it.");
        } catch (IOException e) {
            System.err.println("Could not start local Ollama: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Interrupted while starting local Ollama.");
        }
    }

    private static boolean responds(HttpClient client) throws InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(API_URL))
                .timeout(Duration.ofSeconds(2)).GET().build();
        try {
            return client.send(request, HttpResponse.BodyHandlers.discarding()).statusCode() == 200;
        } catch (IOException e) {
            return false;
        }
    }

    private static String setting(String name, String fallback) {
        String value = System.getProperty(name);
        if (value == null || value.isBlank()) value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value.trim();
    }
}
