package com.eldercare.modules.ai.risk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FallRiskHistoryDto {

    private Long residentId;
    private String residentName;
    private Integer currentScore;
    private String currentRiskLevel;
    private String trendDirection; // IMPROVING, WORSENING, STABLE
    private List<SnapshotItem> snapshots;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SnapshotItem {
        private Long id;
        private Integer dfrsScore;
        private String riskLevel;
        private Integer incidentSubscore;
        private Integer medicationSubscore;
        private Integer adlSubscore;
        private Integer vitalSubscore;
        private Integer recentFallsCount;
        private Integer activeFridCount;
        private OffsetDateTime assessedAt;
        private String assessedBy;
    }
}
