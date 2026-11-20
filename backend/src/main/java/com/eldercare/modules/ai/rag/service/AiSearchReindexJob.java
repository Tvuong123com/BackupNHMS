package com.eldercare.modules.ai.rag.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiSearchReindexJob {
    private final SemanticSearchService searchService;

    @Scheduled(cron = "${eldercare.ai.search.reindex-cron:0 0 2 * * *}")
    public void refreshDaily() {
        try {
            log.info("Refreshed AI search index with {} documents", searchService.reindex());
        } catch (Exception e) {
            log.warn("AI search index refresh failed: {}", e.getClass().getSimpleName());
        }
    }
}
