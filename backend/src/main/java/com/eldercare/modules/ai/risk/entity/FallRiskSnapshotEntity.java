package com.eldercare.modules.ai.risk.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

/**
 * Historical snapshot of Dynamic Fall Risk Score (DFRS).
 * Enables trend analysis (30-90 days), risk trajectory tracking, and clinical audits.
 */
@Entity
@Table(name = "ai_fall_risk_snapshots")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FallRiskSnapshotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "resident_id", nullable = false)
    private Long residentId;

    @Column(name = "dfrs_score", nullable = false)
    private Integer dfrsScore;

    @Column(name = "risk_level", nullable = false, length = 20)
    private String riskLevel;

    @Column(name = "incident_subscore", nullable = false)
    private Integer incidentSubscore;

    @Column(name = "medication_subscore", nullable = false)
    private Integer medicationSubscore;

    @Column(name = "adl_subscore", nullable = false)
    private Integer adlSubscore;

    @Column(name = "vital_subscore", nullable = false)
    private Integer vitalSubscore;

    @Column(name = "recent_falls_count", nullable = false)
    private Integer recentFallsCount;

    @Column(name = "active_frid_count", nullable = false)
    private Integer activeFridCount;

    @Column(name = "method_version", length = 50)
    private String methodVersion;

    @Column(name = "assessed_at", nullable = false)
    private OffsetDateTime assessedAt;

    @Column(name = "assessed_by")
    private String assessedBy;

    @PrePersist
    void onCreate() {
        if (assessedAt == null) {
            assessedAt = OffsetDateTime.now();
        }
    }
}
