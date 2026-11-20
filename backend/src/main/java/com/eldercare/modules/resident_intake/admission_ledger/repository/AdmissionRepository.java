package com.eldercare.modules.resident_intake.admission_ledger.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;

import com.eldercare.modules.resident_intake.admission_ledger.AdmissionEntity;

public interface AdmissionRepository extends JpaRepository<AdmissionEntity, Long> {
  Page<AdmissionEntity> findAll(Pageable pageable);

  Optional<AdmissionEntity> findByResidentIdAndIsCurrentTrue(Long residentId);

  List<AdmissionEntity> findByResidentId(Long residentId);

  List<AdmissionEntity> findByIsCurrentTrueAndDischargeDateIsNull();
  @Query("select a from AdmissionEntity a join fetch a.resident r where a.isCurrent = true and a.dischargeDate is null "
      + "and r.isDeleted = false and a.admissionDate between :fromDate and :toDate "
      + "and not exists (select s.id from AssessmentEntity s where s.resident.id = r.id and s.isCurrent = true and s.status = 'DRAFT')")
  List<AdmissionEntity> findEligibleForAssessment(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);
}
