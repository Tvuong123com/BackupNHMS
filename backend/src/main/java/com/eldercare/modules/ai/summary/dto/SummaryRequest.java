package com.eldercare.modules.ai.summary.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummaryRequest {
    private String type; // "SHIFT_HANDOVER", "INCIDENT_REPORT", "FAMILY_UPDATE"
    private String tone; // "CLINICAL", "FAMILY_FRIENDLY", "EXECUTIVE"
    private String shiftName; // e.g. "Ca sáng (06:00 - 14:00)"
    private List<String> events; // Danh sách các sự kiện / ghi chú ghi nhận
    private String residentName;
    private String extraNotes;
}
