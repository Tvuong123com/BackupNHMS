package com.eldercare.modules.ai.voice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;

@Entity
@Table(name = "ai_voice_observations")
@Getter
@Setter
public class AiVoiceObservationEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "resident_id", nullable = false)
    private Long residentId;
    @Column(name = "task_id")
    private Long taskId;
    @Column(name = "confirmed_by", nullable = false, length = 255)
    private String confirmedBy;
    @Column(nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String transcript;
    @Column(name = "parsed_json", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String parsedJson;
    @Column(name = "confirmed_at", nullable = false)
    private OffsetDateTime confirmedAt = OffsetDateTime.now();
}
