package com.eldercare.modules.ai.summary.controller;

import com.eldercare.modules.ai.summary.dto.HandoverTypes.*;
import com.eldercare.modules.ai.summary.service.HandoverWorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ai/handover")
@RequiredArgsConstructor
public class HandoverController {
    private final HandoverWorkflowService service;
    @GetMapping("/residents")
    public List<ResidentOption> residents() { return service.residentOptions(); }
    @GetMapping("/shared")
    public List<Detail> shared(Authentication auth) { return service.shared(auth.getName()); }
    @GetMapping("/{id}")
    public Detail get(@PathVariable Long id, Authentication auth) { return service.get(id, auth.getName()); }
    @PostMapping("/{id}/approve")
    public Detail approve(@PathVariable Long id, @RequestBody Review request, Authentication auth) {
        return service.approve(id, request, auth.getName());
    }
    @PostMapping("/{id}/actions/{actionId}/task")
    public Detail assign(@PathVariable Long id, @PathVariable String actionId, @RequestBody Assignment request, Authentication auth) {
        return service.assign(id, actionId, request, auth.getName());
    }
}
