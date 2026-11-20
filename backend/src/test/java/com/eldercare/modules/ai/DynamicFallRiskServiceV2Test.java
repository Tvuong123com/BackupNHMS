package com.eldercare.modules.ai;

import com.eldercare.modules.admin.user_management.UserEntity;
import com.eldercare.modules.admin.user_management.UserRepository;
import com.eldercare.modules.ai.risk.catalog.FridMedicationCatalog;
import com.eldercare.modules.ai.risk.dto.ApplyFallInterventionRequest;
import com.eldercare.modules.ai.risk.dto.ApplyFallInterventionResponse;
import com.eldercare.modules.ai.risk.dto.DynamicFallRiskDto;
import com.eldercare.modules.ai.risk.dto.FallRiskHistoryDto;
import com.eldercare.modules.ai.risk.entity.FallRiskSnapshotEntity;
import com.eldercare.modules.ai.risk.repository.FallRiskSnapshotRepository;
import com.eldercare.modules.ai.risk.repository.FallScreeningRepository;
import com.eldercare.modules.ai.risk.service.DynamicFallRiskService;
import com.eldercare.modules.careplan_management.careplan_design.repository.database_schema.CareGoalSchema;
import com.eldercare.modules.careplan_management.careplan_design.repository.database_schema.CareInterventionSchema;
import com.eldercare.modules.careplan_management.careplan_design.repository.database_schema.CarePlanSchema;
import com.eldercare.modules.careplan_management.careplan_design.repository.jpa.JpaCareGoalRepository;
import com.eldercare.modules.careplan_management.careplan_design.repository.jpa.JpaCareInterventionRepository;
import com.eldercare.modules.careplan_management.careplan_design.repository.jpa.JpaCarePlanRepository;
import com.eldercare.modules.careplan_management.cna_daily_tasks.entity.CareTaskEntity;
import com.eldercare.modules.careplan_management.cna_daily_tasks.enums.TaskStatus;
import com.eldercare.modules.careplan_management.cna_daily_tasks.repository.CareTaskRepository;
import com.eldercare.modules.emar_medication.physician_orders.MedicationOrderEntity;
import com.eldercare.modules.emar_medication.physician_orders.MedicationOrderRepository;
import com.eldercare.modules.resident_intake.assessment.repository.AssessmentRepository;
import com.eldercare.modules.resident_intake.resident.repository.ResidentRepository;
import com.eldercare.modules.resident_intake.resident_profile.ResidentEntity;
import com.eldercare.modules.risk_incident.incident_tracking.entity.IncidentEntity;
import com.eldercare.modules.risk_incident.incident_tracking.entity.IncidentType;
import com.eldercare.modules.risk_incident.incident_tracking.repository.IncidentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class DynamicFallRiskServiceV2Test {

    private ResidentRepository residentRepository;
    private IncidentRepository incidentRepository;
    private MedicationOrderRepository medicationOrderRepository;
    private AssessmentRepository assessmentRepository;
    private FallScreeningRepository fallScreeningRepository;
    private FridMedicationCatalog fridCatalog;
    private FallRiskSnapshotRepository fallRiskSnapshotRepository;
    private JpaCarePlanRepository jpaCarePlanRepository;
    private JpaCareGoalRepository jpaCareGoalRepository;
    private JpaCareInterventionRepository jpaCareInterventionRepository;
    private CareTaskRepository careTaskRepository;
    private UserRepository userRepository;

    private DynamicFallRiskService service;

    @BeforeEach
    void setUp() {
        residentRepository = mock(ResidentRepository.class);
        incidentRepository = mock(IncidentRepository.class);
        medicationOrderRepository = mock(MedicationOrderRepository.class);
        assessmentRepository = mock(AssessmentRepository.class);
        fallScreeningRepository = mock(FallScreeningRepository.class);
        fridCatalog = new FridMedicationCatalog();
        fallRiskSnapshotRepository = mock(FallRiskSnapshotRepository.class);
        jpaCarePlanRepository = mock(JpaCarePlanRepository.class);
        jpaCareGoalRepository = mock(JpaCareGoalRepository.class);
        jpaCareInterventionRepository = mock(JpaCareInterventionRepository.class);
        careTaskRepository = mock(CareTaskRepository.class);
        userRepository = mock(UserRepository.class);

        service = new DynamicFallRiskService(
                residentRepository,
                incidentRepository,
                medicationOrderRepository,
                assessmentRepository,
                fallScreeningRepository,
                fridCatalog,
                fallRiskSnapshotRepository,
                jpaCarePlanRepository,
                jpaCareGoalRepository,
                jpaCareInterventionRepository,
                careTaskRepository,
                userRepository
        );
    }

    @Test
    void bulkCalculationEvaluatesAllResidentsWithoutNPlusOneErrors() {
        ResidentEntity resident1 = new ResidentEntity();
        resident1.setId(101L);
        resident1.setFirstName("Thi");
        resident1.setLastName("Nguyen");

        ResidentEntity resident2 = new ResidentEntity();
        resident2.setId(102L);
        resident2.setFirstName("Van");
        resident2.setLastName("Tran");

        when(residentRepository.findByStatusIgnoreCaseAndIsDeletedFalse("ACTIVE"))
                .thenReturn(List.of(resident1, resident2));

        IncidentEntity fallIncident = new IncidentEntity();
        fallIncident.setId(501L);
        fallIncident.setResident(resident1);
        fallIncident.setIncidentType(IncidentType.FALL);
        fallIncident.setOccurredAt(OffsetDateTime.now().minusDays(5));

        when(incidentRepository.findRecentWithResident(any())).thenReturn(List.of(fallIncident));

        MedicationOrderEntity diazepam = new MedicationOrderEntity();
        diazepam.setId(701L);
        diazepam.setResidentId(101L);
        diazepam.setDrugName("Diazepam 5mg");

        when(medicationOrderRepository.findByIsDeletedFalseAndStatusIgnoreCase("ACTIVE"))
                .thenReturn(List.of(diazepam));

        List<DynamicFallRiskDto> results = service.calculateAllActiveResidents();

        assertEquals(2, results.size());
        // Resident 1 has fall incident and Diazepam -> Should have higher score and appear first
        assertEquals(101L, results.get(0).getResidentId());
        assertTrue(results.get(0).getDfrsScore() >= 40);
        assertTrue(results.get(0).getContributingFactors().stream().anyMatch(f -> f.contains("fall incident")));
        assertTrue(results.get(0).getContributingFactors().stream().anyMatch(f -> f.contains("Diazepam")));
    }

    @Test
    void snapshotRecordingAndTrendCalculation() {
        ResidentEntity resident = new ResidentEntity();
        resident.setId(201L);
        resident.setFirstName("Hoa");
        resident.setLastName("Le");

        when(residentRepository.findByIdAndIsDeletedFalse(201L)).thenReturn(Optional.of(resident));
        when(incidentRepository.findRecentWithResident(any())).thenReturn(List.of());
        when(medicationOrderRepository.findByResidentIdAndIsDeletedFalseAndStatusIgnoreCase(eq(201L), eq("ACTIVE")))
                .thenReturn(List.of());

        FallRiskSnapshotEntity s1 = FallRiskSnapshotEntity.builder()
                .id(1L)
                .residentId(201L)
                .dfrsScore(25)
                .riskLevel("LOW")
                .incidentSubscore(0)
                .medicationSubscore(0)
                .adlSubscore(5)
                .vitalSubscore(0)
                .recentFallsCount(0)
                .activeFridCount(0)
                .assessedAt(OffsetDateTime.now())
                .assessedBy("NURSE_MAI")
                .build();

        FallRiskSnapshotEntity s2 = FallRiskSnapshotEntity.builder()
                .id(2L)
                .residentId(201L)
                .dfrsScore(55) // Prior score was 55, now 25 -> IMPROVING!
                .riskLevel("MODERATE")
                .incidentSubscore(25)
                .medicationSubscore(15)
                .adlSubscore(15)
                .vitalSubscore(0)
                .recentFallsCount(1)
                .activeFridCount(1)
                .assessedAt(OffsetDateTime.now().minusDays(14))
                .assessedBy("NURSE_LAN")
                .build();

        when(fallRiskSnapshotRepository.findByResidentIdOrderByAssessedAtDesc(201L))
                .thenReturn(List.of(s1, s2));

        FallRiskHistoryDto history = service.getResidentRiskHistory(201L, 10);

        assertNotNull(history);
        assertEquals(201L, history.getResidentId());
        assertEquals("IMPROVING", history.getTrendDirection());
        assertEquals(2, history.getSnapshots().size());
    }

    @Test
    void applyInterventionCreatesGoalInterventionAndCareTask() {
        ResidentEntity resident = new ResidentEntity();
        resident.setId(301L);
        resident.setFirstName("Binh");
        resident.setLastName("Pham");

        when(residentRepository.findByIdAndIsDeletedFalse(301L)).thenReturn(Optional.of(resident));

        CarePlanSchema carePlan = new CarePlanSchema();
        carePlan.setId(901L);
        carePlan.setResident(resident);
        carePlan.setListCareGoal(new ArrayList<>());

        when(jpaCarePlanRepository.findFirstByResident_IdAndStatusIgnoreCaseAndIsDeletedFalseOrderByCreatedAtDesc(eq(301L), any()))
                .thenReturn(Optional.of(carePlan));

        when(jpaCareGoalRepository.save(any(CareGoalSchema.class))).thenAnswer(i -> {
            CareGoalSchema g = i.getArgument(0);
            g.setId(401L);
            return g;
        });

        when(jpaCareInterventionRepository.save(any(CareInterventionSchema.class))).thenAnswer(i -> {
            CareInterventionSchema cv = i.getArgument(0);
            cv.setId(601L);
            return cv;
        });

        when(careTaskRepository.save(any(CareTaskEntity.class))).thenAnswer(i -> {
            CareTaskEntity t = i.getArgument(0);
            t.setId(801L);
            return t;
        });

        ApplyFallInterventionRequest request = ApplyFallInterventionRequest.builder()
                .residentId(301L)
                .role("CNA")
                .action("Kích hoạt cảm biến áp lực giường (Bed Sensor Pad) ca đêm")
                .rationale("Ngăn ngừa ngã lúc nửa đêm")
                .priority("IMMEDIATE")
                .build();

        ApplyFallInterventionResponse response = service.applyInterventionToCarePlan(request, "HEAD_NURSE");

        assertTrue(response.isSuccess());
        assertEquals(901L, response.getCarePlanId());
        assertEquals(401L, response.getCareGoalId());
        assertEquals(601L, response.getCareInterventionId());
        assertEquals(801L, response.getCareTaskId());
        assertEquals("CNA", response.getAssignedRole());
        assertEquals("PENDING", response.getTaskStatus());

        verify(careTaskRepository, times(1)).save(any(CareTaskEntity.class));
    }
}
