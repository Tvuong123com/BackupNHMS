package com.eldercare.modules.ai.rag.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;

@Entity
@Table(name = "ai_vector_documents", uniqueConstraints = @UniqueConstraint(columnNames = {"source_type", "source_id"}))
@Getter
@Setter
public class AiVectorDocumentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "source_type", nullable = false, length = 32)
    private String sourceType;
    @Column(name = "source_id", nullable = false, length = 64)
    private String sourceId;
    @Column(name = "resident_id")
    private Long residentId;
    @Column(nullable = false, length = 255)
    private String title;
    @Column(nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String content;
    @Column(name = "embedding_json", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String embeddingJson;
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();
}
