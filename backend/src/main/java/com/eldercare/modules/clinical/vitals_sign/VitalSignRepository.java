package com.eldercare.modules.clinical.vitals_sign;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VitalSignRepository extends JpaRepository<VitalSignEntity, Long> {
    List<VitalSignEntity> findTop10ByResidentIdOrderByRecordedAtDesc(Long residentId);
}
