package com.eldercare.modules.ai.risk.repository;

import com.eldercare.modules.ai.risk.entity.FallScreeningEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FallScreeningRepository extends JpaRepository<FallScreeningEntity, Long> {
    Optional<FallScreeningEntity> findFirstByResidentIdOrderByIdDesc(Long residentId);
}
