package com.eldercare.modules.ai.chat.repository;

import com.eldercare.modules.ai.chat.entity.AiChatTurnEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AiChatTurnRepository extends JpaRepository<AiChatTurnEntity, Long> {
    List<AiChatTurnEntity> findTop12ByConversationIdAndOwnerEmailOrderByIdDesc(
            String conversationId, String ownerEmail);
}
