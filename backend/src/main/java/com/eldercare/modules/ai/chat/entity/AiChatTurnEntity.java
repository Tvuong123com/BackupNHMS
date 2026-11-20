package com.eldercare.modules.ai.chat.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;

@Entity
@Table(name = "ai_chat_turns")
@Getter
@Setter
public class AiChatTurnEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "conversation_id", nullable = false, length = 36)
    private String conversationId;
    @Column(name = "owner_email", nullable = false, length = 255)
    private String ownerEmail;
    @Column(name = "role", nullable = false, length = 12)
    private String role;
    @Column(name = "content", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String content;
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();
}
