package com.eldercare.modules.ai.voice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoiceCareParseResponse {
    private String residentIdentifier;
    private String roomNumber;
    private Integer dietaryIntakePercentage; // % khẩu phần ăn (0 - 100)
    private Integer bloodPressureSystolic;   // Huyết áp tâm thu
    private Integer bloodPressureDiastolic;  // Huyết áp tâm trương
    private Integer heartRateBpm;            // Nhịp tim
    private Double bodyTemperatureCelsius;   // Thân nhiệt
    private Boolean medicationTaken;         // Đã uống thuốc hay chưa
    private String extractedNotes;           // Ghi chú chăm sóc tóm tắt
    private Double confidence;               // Độ chính xác bóc tách (0.0 - 1.0)
}
