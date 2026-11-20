package com.eldercare.modules.ai.rag.repository;

import com.eldercare.modules.ai.rag.entity.AiVectorDocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AiVectorDocumentRepository extends JpaRepository<AiVectorDocumentEntity, Long> {
    Optional<AiVectorDocumentEntity> findBySourceTypeAndSourceId(String sourceType, String sourceId);
}
