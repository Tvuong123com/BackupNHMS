package com.eldercare.modules.ai.summary.dto;

import java.time.OffsetDateTime;
import java.util.List;

public final class HandoverTypes {
    private HandoverTypes() {}
    public record SourceRecord(String reference, Long residentId, String residentName, String description) {}
    public record Suggestion(Long residentId, String description, String taskType, List<String> sourceRefs) {}
    public record Action(String id, Long residentId, String description, String taskType, List<String> sourceRefs,
                         Long careTaskId, String assignedBy, String assignedAt) {}
    public record Context(List<SourceRecord> sources, List<Action> actions) {}
    public record Review(String title, String narrativeSummary, List<Suggestion> actions) {}
    public record Assignment(Long assignedCnaId, OffsetDateTime scheduledTime) {}
    public record ActionView(Action action, String residentName, String taskStatus,
                             Long assignedCnaId, String assignedCnaName, OffsetDateTime scheduledTime) {}
    public record Detail(Long id, String status, String createdBy, String approvedBy, OffsetDateTime approvedAt,
                         OffsetDateTime start, OffsetDateTime end, SummaryResponse summary,
                         List<SourceRecord> sources, List<ActionView> actions, boolean canApprove) {}
    public record ResidentOption(Long id, String name) {}
}
