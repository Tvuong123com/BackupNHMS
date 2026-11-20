package com.eldercare.modules.ai.rag.service;

import com.eldercare.modules.ai.engine.AiSettingsService;
import com.eldercare.modules.ai.engine.ElderCareClinicalKnowledgeBase;
import com.eldercare.modules.ai.rag.entity.AiVectorDocumentEntity;
import com.eldercare.modules.ai.rag.repository.AiVectorDocumentRepository;
import com.eldercare.modules.risk_incident.incident_tracking.repository.IncidentRepository;
import com.eldercare.modules.careplan_management.careplan_design.repository.jpa.JpaCareGoalRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/** SQL Server stores embeddings as JSON; cosine ranking runs in Java for this small index. */
@Service
@RequiredArgsConstructor
public class SemanticSearchService {
    private final AiVectorDocumentRepository repository;
    private final ElderCareClinicalKnowledgeBase knowledgeBase;
    private final IncidentRepository incidentRepository;
    private final JpaCareGoalRepository careGoalRepository;
    private final AiSettingsService settingsService;
    private final ObjectMapper objectMapper;
    private final RestClient restClient = RestClient.builder().requestFactory(requestFactory()).build();

    private static SimpleClientHttpRequestFactory requestFactory() {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(60000);
        return factory;
    }

    public record SearchHit(String sourceType, String sourceId, Long residentId,
                            String title, String excerpt, double similarity) {}

    @Transactional
    public synchronized int reindex() {
        List<Document> documents = new ArrayList<>();
        for (var item : knowledgeBase.getAllProtocols()) {
            documents.add(new Document("WORKFLOW", item.getId(), null, item.getTitle(),
                    "Staff workflow reminder, not an approved clinical protocol. " + item.getStructuredAnswer()));
        }
        for (var incident : incidentRepository.findRecentWithResident(OffsetDateTime.now().minusDays(90))) {
            if (incident.getResident() == null || incident.getResident().isDeleted()) continue;
            String name = incident.getResident().getFirstName() + " " + incident.getResident().getLastName();
            documents.add(new Document("INCIDENT", incident.getId().toString(), incident.getResident().getId(),
                    "Incident " + incident.getId() + " · " + name,
                    "Documented incident " + incident.getIncidentType() + " at " + incident.getOccurredAt()
                            + ". " + (incident.getDescription() == null ? "" : incident.getDescription().substring(0,
                                    Math.min(2000, incident.getDescription().length())))));
        }
        for (var goal : careGoalRepository.findAll()) {
            var plan = goal.getCarePlan();
            if (plan == null || Boolean.TRUE.equals(plan.getIsDeleted())
                    || "ARCHIVED".equalsIgnoreCase(plan.getStatus()) || plan.getResident() == null
                    || goal.getTitle() == null || goal.getTitle().isBlank()
                    || plan.getResident().isDeleted()) continue;
            documents.add(new Document("CARE_GOAL", goal.getId().toString(), plan.getResident().getId(),
                    "Care goal " + goal.getId() + " · " + plan.getResident().getFirstName() + " "
                            + plan.getResident().getLastName(),
                    "Documented care plan goal: " + goal.getTitle() + ". " + goal.getDescription()));
        }
        Set<String> activeKeys = new HashSet<>();
        for (Document doc : documents) {
            activeKeys.add(doc.type() + "/" + doc.id());
            AiVectorDocumentEntity row = repository.findBySourceTypeAndSourceId(doc.type(), doc.id())
                    .orElseGet(AiVectorDocumentEntity::new);
            row.setSourceType(doc.type());
            row.setSourceId(doc.id());
            row.setResidentId(doc.residentId());
            row.setTitle(doc.title());
            row.setContent(doc.content());
            try {
                row.setEmbeddingJson(objectMapper.writeValueAsString(embed(doc.title() + "\n" + doc.content())));
            } catch (Exception e) {
                throw new IllegalStateException("Could not embed source " + doc.type() + "/" + doc.id(), e);
            }
            row.setUpdatedAt(OffsetDateTime.now());
            repository.save(row);
        }
        repository.findAll().stream()
                .filter(row -> !activeKeys.contains(row.getSourceType() + "/" + row.getSourceId()))
                .forEach(repository::delete);
        return documents.size();
    }

    public List<SearchHit> search(String query, Long residentId, int limit) {
        return rank(query, residentId, limit, false);
    }

    public List<SearchHit> searchWorkflow(String query) {
        return rank(query, null, 3, true);
    }

    private List<SearchHit> rank(String query, Long residentId, int limit, boolean workflowOnly) {
        if (query == null || query.isBlank() || query.length() > 500) {
            throw new IllegalArgumentException("Query must contain 1–500 characters");
        }
        double[] queryVector = embed(query);
        List<SearchHit> hits = new ArrayList<>();
        for (var row : repository.findAll()) {
            if (workflowOnly && !"WORKFLOW".equals(row.getSourceType())) continue;
            if (residentId != null && row.getResidentId() != null && !residentId.equals(row.getResidentId())) continue;
            try {
                double[] vector = objectMapper.readValue(row.getEmbeddingJson(), double[].class);
                double score = cosine(queryVector, vector);
                if (score < 0.25) continue;
                hits.add(new SearchHit(row.getSourceType(), row.getSourceId(), row.getResidentId(),
                        row.getTitle(), row.getContent(), Math.round(score * 1000.0) / 1000.0));
            } catch (Exception ignored) {
                // Skip a corrupt vector; reindex can repair it.
            }
        }
        return hits.stream().sorted(Comparator.comparingDouble(SearchHit::similarity).reversed())
                .limit(Math.min(10, Math.max(1, limit))).toList();
    }

    static double cosine(double[] a, double[] b) {
        if (a.length == 0 || a.length != b.length) return 0;
        double dot = 0, na = 0, nb = 0;
        for (int i = 0; i < a.length; i++) { dot += a[i] * b[i]; na += a[i] * a[i]; nb += b[i] * b[i]; }
        return na == 0 || nb == 0 ? 0 : dot / Math.sqrt(na * nb);
    }

    private double[] embed(String text) {
        try {
            String baseUrl = settingsService.getSettings().getOllamaBaseUrl();
            if (baseUrl == null || baseUrl.isBlank()) baseUrl = "http://localhost:11434";
            baseUrl = baseUrl.replaceAll("/+$", "");
            String response = restClient.post().uri(baseUrl + "/api/embed")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("model", "nomic-embed-text", "input", text))
                    .retrieve().body(String.class);
            var embeddings = objectMapper.readTree(response).path("embeddings");
            return objectMapper.treeToValue(embeddings.get(0), double[].class);
        } catch (Exception e) {
            throw new IllegalStateException("Ollama embedding model is unavailable", e);
        }
    }

    private record Document(String type, String id, Long residentId, String title, String content) {}
}
