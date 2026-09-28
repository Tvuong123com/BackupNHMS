package com.eldercare.modules.ai.careplan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarePlanSuggestionRequest {
    private String residentName;
    private Integer age;
    private String gender;
    private String careLevel; // e.g. "Skilled Nursing", "Memory Care", "Assisted Living"
    private Integer adlScore; // Điểm ADL (Activities of Daily Living)
    private List<String> diagnoses; // Bệnh lý chẩn đoán
    private List<String> recentIncidents; // Lịch sử sự cố gần đây
    private List<String> existingGoals; // Các mục tiêu hiện có
}
