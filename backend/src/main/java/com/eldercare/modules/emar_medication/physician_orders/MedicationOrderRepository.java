package com.eldercare.modules.emar_medication.physician_orders;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MedicationOrderRepository extends JpaRepository<MedicationOrderEntity, Long> {
    List<MedicationOrderEntity> findByResidentIdAndIsDeletedFalseAndStatusIgnoreCase(Long residentId, String status);
    List<MedicationOrderEntity> findByIsDeletedFalseAndStatusIgnoreCase(String status);
}
