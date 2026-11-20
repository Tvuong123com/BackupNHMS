package com.eldercare.modules.clinical.vitals_sign;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "vital_signs")
@Getter
@Setter
public class VitalSignEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "resident_id", nullable = false)
    private Long residentId;
    @Column(name = "recorded_by", nullable = false)
    private Long recordedBy;
    @Column(name = "blood_pressure_systolic")
    private Short bloodPressureSystolic;
    @Column(name = "blood_pressure_diastolic")
    private Short bloodPressureDiastolic;
    @Column(name = "heart_rate_bpm")
    private Short heartRateBpm;
    @Column(name = "respiratory_rate")
    private Short respiratoryRate;
    @Column(name = "temperature_fahrenheit", precision = 4, scale = 1)
    private BigDecimal temperatureFahrenheit;
    @Column(name = "spo2_percentage")
    private Short spo2Percentage;
    @Column(name = "pain_scale")
    private Short painScale;
    @Column(name = "notes", columnDefinition = "NVARCHAR(MAX)")
    private String notes;
    @Column(name = "recorded_at", nullable = false)
    private OffsetDateTime recordedAt = OffsetDateTime.now();
}
