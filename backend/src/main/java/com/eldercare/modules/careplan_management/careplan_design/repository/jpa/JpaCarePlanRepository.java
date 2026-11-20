package com.eldercare.modules.careplan_management.careplan_design.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.eldercare.modules.careplan_management.careplan_design.repository.database_schema.CarePlanSchema;

import java.util.Optional;

public interface JpaCarePlanRepository
        extends JpaRepository<CarePlanSchema, Long>, JpaSpecificationExecutor<CarePlanSchema> {
    Optional<CarePlanSchema> findFirstByResident_IdAndIsDeletedFalseOrderByCreatedAtDesc(Long residentId);
    Optional<CarePlanSchema> findFirstByResident_IdAndStatusIgnoreCaseAndIsDeletedFalseOrderByCreatedAtDesc(Long residentId, String status);
}
