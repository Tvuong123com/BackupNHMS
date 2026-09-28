package com.eldercare.modules.ai.elopement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElopementRiskRequest {
    private String residentName;
    private String roomNumber;
    private Integer cognitiveScore; // Ví dụ: Mini-Mental State Examination (MMSE) / 30
    private Boolean hasDementiaDiagnosis;
    private List<String> recentBehaviors; // VD: "Tìm chìa khóa", "Đứng trước cửa ra vào", "Kích động về đêm"
    private String timeOfDay; // "NIGHT", "DAY"
}
