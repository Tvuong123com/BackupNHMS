package com.eldercare.modules.ai.summary.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;

@Entity
@Table(name = "ai_summary_drafts")
@Getter
@Setter
public class AiSummaryDraftEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 16)
    private String audience;
    @Column(name = "resident_id")
    private Long residentId;
    @Column(name = "created_by", nullable = false, length = 255)
    private String createdBy;
    @Column(name = "shift_start", nullable = false)
    private OffsetDateTime shiftStart;
    @Column(name = "shift_end", nullable = false)
    private OffsetDateTime shiftEnd;
    @Column(name = "source_events_json", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String sourceEventsJson;
    @Column(name = "draft_json", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String draftJson;
    @Column(name = "handover_json", columnDefinition = "NVARCHAR(MAX)")
    private String handoverJson;
    @Column(name = "approval_status", nullable = false, length = 16)
    private String approvalStatus = "DRAFT";
    @Column(name = "approved_by", length = 255)
    private String approvedBy;
    @Column(name = "approved_at")
    private OffsetDateTime approvedAt;
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();
}
