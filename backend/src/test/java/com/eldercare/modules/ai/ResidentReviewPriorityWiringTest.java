package com.eldercare.modules.ai;

import com.eldercare.modules.ai.risk.repository.FallScreeningRepository;
import com.eldercare.modules.ai.risk.service.DynamicFallRiskService;
import com.eldercare.modules.ai.risk.service.ResidentReviewPriorityService;
import com.eldercare.modules.resident_intake.assessment.repository.AssessmentRepository;
import com.eldercare.modules.resident_intake.resident.repository.ResidentRepository;
import com.eldercare.modules.risk_incident.incident_tracking.repository.IncidentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class ResidentReviewPriorityWiringTest {
    @Test
    void springCanCreateReviewServiceWithItsRuntimeDependencies() {
        try (var context = new AnnotationConfigApplicationContext()) {
            context.registerBean(IncidentRepository.class, () -> mock(IncidentRepository.class));
            context.registerBean(AssessmentRepository.class, () -> mock(AssessmentRepository.class));
            context.registerBean(FallScreeningRepository.class, () -> mock(FallScreeningRepository.class));
            context.registerBean(ResidentRepository.class, () -> mock(ResidentRepository.class));
            context.registerBean(DynamicFallRiskService.class, () -> mock(DynamicFallRiskService.class));
            context.register(ResidentReviewPriorityService.class);
            context.refresh();

            assertNotNull(context.getBean(ResidentReviewPriorityService.class));
        }
    }
}
