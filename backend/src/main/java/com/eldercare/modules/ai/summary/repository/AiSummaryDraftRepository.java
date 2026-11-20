package com.eldercare.modules.ai.summary.repository;

import com.eldercare.modules.ai.summary.entity.AiSummaryDraftEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface AiSummaryDraftRepository extends JpaRepository<AiSummaryDraftEntity, Long> {
    List<AiSummaryDraftEntity> findTop20ByCreatedByOrderByIdDesc(String createdBy);
    List<AiSummaryDraftEntity> findTop50ByAudienceAndApprovalStatusOrderByApprovedAtDesc(String audience, String status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select d from AiSummaryDraftEntity d where d.id = :id")
    Optional<AiSummaryDraftEntity> findLockedById(@Param("id") Long id);
}
