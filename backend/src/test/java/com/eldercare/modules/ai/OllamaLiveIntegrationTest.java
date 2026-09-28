package com.eldercare.modules.ai;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OllamaLiveIntegrationTest {

    private boolean isOllamaReachable() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("127.0.0.1", 11434), 1000);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Test
    @DisplayName("Kiểm tra Ollama Server cục bộ đang lắng nghe và phản hồi thẻ tags")
    void shouldConnectToLocalOllamaServer() {
        if (!isOllamaReachable()) {
            System.out.println("Ollama local server không hoạt động tại 11434, bỏ qua test live.");
            return;
        }

        RestClient client = RestClient.builder()
                .baseUrl("http://127.0.0.1:11434")
                .build();

        Map response = client.get()
                .uri("/api/tags")
                .retrieve()
                .body(Map.class);

        assertNotNull(response);
        assertTrue(response.containsKey("models"));
        System.out.println("Ollama Live Models: " + response.get("models"));
    }
}
