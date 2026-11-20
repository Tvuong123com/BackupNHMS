package com.eldercare.modules.ai.risk.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "ai_fall_screenings")
@Getter
@Setter
public class FallScreeningEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "resident_id", nullable = false)
    private Long residentId;
    @Column(name = "assessed_by", nullable = false)
    private String assessedBy;
    @Column(name = "history_of_falling", nullable = false)
    private Integer historyOfFalling;
    @Column(name = "secondary_diagnosis", nullable = false)
    private Integer secondaryDiagnosis;
    @Column(name = "ambulatory_aid", nullable = false)
    private Integer ambulatoryAid;
    @Column(name = "iv_therapy", nullable = false)
    private Integer ivTherapy;
    @Column(name = "gait", nullable = false)
    private Integer gait;
    @Column(name = "mental_status", nullable = false)
    private Integer mentalStatus;
    @Column(name = "total_score", nullable = false)
    private Integer totalScore;
    @Column(name = "assessed_at", nullable = false)
    private OffsetDateTime assessedAt;

    @PrePersist
    void onCreate() {
        if (assessedAt == null) assessedAt = OffsetDateTime.now();
    }
}
