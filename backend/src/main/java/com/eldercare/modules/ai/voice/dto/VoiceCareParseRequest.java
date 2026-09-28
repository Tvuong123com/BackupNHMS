package com.eldercare.modules.ai.voice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoiceCareParseRequest {
    private String spokenText;
    private String expectedResidentName;
    private String expectedRoomNumber;
}
