package com.eldercare.modules.emar_medication.physician_orders;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;

@Entity
@Table(name = "medication_orders")
@Getter
@Setter
public class MedicationOrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "drug_name", nullable = false)
    private String drugName;
    @Column(nullable = false)
    private String dosage;
    @Column(nullable = false)
    private String route;
    @Column(nullable = false)
    private String frequency;
    @Column(name = "is_controlled_substance", nullable = false)
    private Boolean controlledSubstance;
    @Column(nullable = false)
    private String status;
    @Column(name = "resident_id", nullable = false)
    private Long residentId;
    @Column(name = "prescribed_by", nullable = false)
    private Long prescribedBy;
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
