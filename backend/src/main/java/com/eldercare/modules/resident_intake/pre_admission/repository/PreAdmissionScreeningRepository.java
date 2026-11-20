package com.eldercare.modules.resident_intake.pre_admission.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.eldercare.modules.resident_intake.pre_admission.PreAdmissionScreeningEntity;

public interface PreAdmissionScreeningRepository extends JpaRepository<PreAdmissionScreeningEntity, Long> {
  Page<PreAdmissionScreeningEntity> findAll(Pageable pageable);

  List<PreAdmissionScreeningEntity> findByStatusAndIsCurrentTrue(String status);

  Optional<PreAdmissionScreeningEntity> findByResidentIdAndIsCurrentTrue(Long residentId);
  @Query("select p from PreAdmissionScreeningEntity p join fetch p.resident r "
      + "where p.status = 'COMPLETED' and p.isCurrent = true and r.isDeleted = false and upper(r.status) = 'PENDING' "
      + "and not exists (select a.id from AdmissionEntity a where a.resident.id = r.id and a.isCurrent = true and a.dischargeDate is null)")
  List<PreAdmissionScreeningEntity> findEligibleForAdmission();
}
