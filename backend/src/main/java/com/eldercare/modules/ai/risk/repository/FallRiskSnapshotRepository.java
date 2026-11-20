package com.eldercare.modules.ai.risk.repository;

import com.eldercare.modules.ai.risk.entity.FallRiskSnapshotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface FallRiskSnapshotRepository extends JpaRepository<FallRiskSnapshotEntity, Long> {

    List<FallRiskSnapshotEntity> findByResidentIdOrderByAssessedAtDesc(Long residentId);

    List<FallRiskSnapshotEntity> findByResidentIdAndAssessedAtAfterOrderByAssessedAtAsc(Long residentId, OffsetDateTime since);
}
