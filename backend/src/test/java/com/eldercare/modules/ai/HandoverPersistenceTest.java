package com.eldercare.modules.ai;

import com.eldercare.modules.ai.engine.AiExecutionService;
import com.eldercare.modules.ai.summary.dto.HandoverTypes.*;
import com.eldercare.modules.ai.summary.service.*;
import com.eldercare.modules.admin.user_management.*;
import com.eldercare.modules.resident_intake.resident.repository.ResidentRepository;
import com.eldercare.modules.resident_intake.resident_profile.ResidentEntity;
import com.eldercare.modules.careplan_management.careplan_design.repository.database_schema.CarePlanSchema;
import com.eldercare.modules.careplan_management.careplan_design.repository.jpa.JpaCarePlanRepository;
import com.eldercare.modules.careplan_management.cna_daily_tasks.repository.CareTaskRepository;
import com.eldercare.modules.careplan_management.cna_daily_tasks.service.CareTaskGroupedService;
import com.eldercare.modules.careplan_management.cna_daily_tasks.dto.request.GroupedTaskQuery;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:handover-flow;DB_CLOSE_DELAY=-1;MODE=MSSQLServer",
        "spring.datasource.driver-class-name=org.h2.Driver", "spring.datasource.username=sa", "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop", "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.jpa.show-sql=false", "logging.level.org.hibernate.tool.schema=ERROR"
})
@ActiveProfiles("test")
@Transactional
class HandoverPersistenceTest {
    @Autowired EntityManager em;
    @Autowired ResidentRepository residents;
    @Autowired UserRepository users;
    @Autowired JpaCarePlanRepository plans;
    @Autowired CareTaskRepository tasks;
    @Autowired ShiftSummaryDraftService draftService;
    @Autowired HandoverWorkflowService workflow;
    @Autowired CareTaskGroupedService taskViews;
    @Autowired com.eldercare.modules.careplan_management.careplan_design.service.CarePlanCreationService creation;
    @Autowired com.eldercare.modules.careplan_management.careplan_design.service.ICarePlanService planDetails;
    @Autowired com.eldercare.modules.resident_intake.pre_admission.repository.PreAdmissionScreeningRepository screenings;
    @Autowired com.eldercare.modules.resident_intake.admission_ledger.repository.AdmissionRepository admissions;
    @MockitoBean AiExecutionService ai;
    // The legacy incident DDL uses SQL Server-only defaults; this persistence test exercises care tasks and notes.
    @MockitoBean com.eldercare.modules.risk_incident.incident_tracking.repository.IncidentRepository incidents;

    @Test void newCarePlanPersistsUnicodeGoalsInterventionsAndAuthenticatedAuthor() {
        RoleEntity role = RoleEntity.builder().roleName("NURSE").build(); em.persist(role);
        UserEntity author = new UserEntity(); author.setRole(role); author.setEmployeeCode("PLAN-FIXTURE"); author.setEmail("plan-fixture@example.test");
        author.setPasswordHash("test-only"); author.setFirstName("Fixture"); author.setLastName("Nurse"); users.saveAndFlush(author);
        var resident = residents.saveAndFlush(ResidentEntity.builder().firstName("Fixture").lastName("Resident").dateOfBirth(LocalDate.of(1940, 1, 1)).status("ACTIVE").build());
        var instruction = new com.eldercare.modules.careplan_management.careplan_design.dto.createCarePlanDTO.CreateCarePlanRequestDTO.CreateCarePlanCareInterventionRequestDTO("Duy trì đèn ngủ và dọn chướng ngại vật.", "CNA");
        var goal = new com.eldercare.modules.careplan_management.careplan_design.dto.createCarePlanDTO.CreateCarePlanRequestDTO.CreateCarePlanCareGoalRequestDTO("Phòng ngừa té ngã", "Mục tiêu được nhân viên xác minh.", List.of(instruction));
        var request = new com.eldercare.modules.careplan_management.careplan_design.dto.createCarePlanDTO.CreateCarePlanRequestDTO(resident.getId().intValue(), List.of(goal));
        var saved = creation.create(request, "DRAFT", author.getEmail());
        em.flush(); em.clear();
        var row = plans.findById((long) saved.id).orElseThrow();
        assertEquals(author.getId(), row.getCreatedBy().getId()); assertEquals("DRAFT", row.getStatus());
        assertEquals("Phòng ngừa té ngã", row.getListCareGoal().get(0).getTitle());
        assertEquals("Duy trì đèn ngủ và dọn chướng ngại vật.", row.getListCareGoal().get(0).getListCareIntervention().get(0).getTitle());
        var lookup = new com.eldercare.modules.careplan_management.careplan_design.dto.getCarePlanDetailDTO.GetCarePlanDetailRequestDTO(); lookup.id = saved.id;
        assertEquals("Mục tiêu được nhân viên xác minh.", planDetails.getCarePlanDetail(lookup).goals.get(0).goalDescription);
        assertThrows(org.springframework.web.server.ResponseStatusException.class, () -> creation.create(request, "ACTIVE", author.getEmail()));
    }

    @Test void intakeLookupsRespectScreeningStateAdmissionWindowAndAssessmentDrafts() {
        var resident = residents.saveAndFlush(ResidentEntity.builder().firstName("Pending").lastName("Fixture").dateOfBirth(LocalDate.of(1940, 1, 1)).status("PENDING").build());
        assertTrue(residents.findEligibleForScreening().stream().anyMatch(r -> r.getId().equals(resident.getId())));
        var screening = new com.eldercare.modules.resident_intake.pre_admission.PreAdmissionScreeningEntity();
        screening.setResident(resident); screening.setStatus("DRAFT"); screening.setIsCurrent(true); screening.setCreatedAt(OffsetDateTime.now()); screenings.saveAndFlush(screening);
        assertTrue(residents.findEligibleForScreening().stream().noneMatch(r -> r.getId().equals(resident.getId())));
        screening.setStatus("COMPLETED"); screenings.saveAndFlush(screening);
        assertTrue(screenings.findEligibleForAdmission().stream().anyMatch(s -> s.getId().equals(screening.getId())));
        var admission = new com.eldercare.modules.resident_intake.admission_ledger.AdmissionEntity();
        var facility = com.eldercare.modules.admin.facility_setup.facility.facility_profile.entity.FacilityEntity.builder()
                .facilityCode("INTAKE-FIXTURE").name("Fixture Facility").licenseNumber("FIXTURE").targetState("CA").build();
        em.persist(facility); admission.setFacility(facility);
        admission.setResident(resident); admission.setPreAdmissionScreening(screening); admission.setIsCurrent(true); admission.setAdmissionDate(LocalDate.now().minusDays(14)); admission.setCreatedAt(OffsetDateTime.now()); admissions.saveAndFlush(admission);
        assertTrue(screenings.findEligibleForAdmission().stream().noneMatch(s -> s.getId().equals(screening.getId())));
        assertTrue(admissions.findEligibleForAssessment(LocalDate.now().minusDays(14), LocalDate.now()).stream().anyMatch(a -> a.getId().equals(admission.getId())));
        admission.setAdmissionDate(LocalDate.now().minusDays(15)); admissions.saveAndFlush(admission);
        assertTrue(admissions.findEligibleForAssessment(LocalDate.now().minusDays(14), LocalDate.now()).isEmpty());
        admission.setAdmissionDate(LocalDate.now()); admissions.saveAndFlush(admission);
        var assessment = new com.eldercare.modules.resident_intake.assessment.AssessmentEntity(); assessment.setResident(resident); assessment.setAdmission(admission); assessment.setStatus("DRAFT"); assessment.setIsCurrent(true); em.persist(assessment); em.flush();
        assertTrue(admissions.findEligibleForAssessment(LocalDate.now().minusDays(14), LocalDate.now()).isEmpty());
        var metric = new com.eldercare.modules.resident_intake.assessment_metric.AssessmentMetricEntity();
        metric.setCategory("ADL"); metric.setMetricName("Fixture metric"); em.persist(metric);
        var detail = new com.eldercare.modules.resident_intake.assessment_detail.AssessmentDetailEntity();
        detail.setAssessment(assessment); detail.setMetric(metric); detail.setScore(1); detail.setNotes("Cần hỗ trợ khi chuyển tư thế."); em.persist(detail);
        em.flush(); em.clear();
        assertEquals("Cần hỗ trợ khi chuyển tư thế.", em.find(com.eldercare.modules.resident_intake.assessment_detail.AssessmentDetailEntity.class, detail.getId()).getNotes());
    }

    @Test void approvedAiHandoverPersistsAndAppearsInResidentAndCnaTaskViews() {
        RoleEntity role = RoleEntity.builder().roleName("CNA").build(); em.persist(role);
        UserEntity cna = new UserEntity(); cna.setRole(role); cna.setEmployeeCode("HANDOVER-FIXTURE");
        cna.setEmail("handover-fixture@example.test"); cna.setPasswordHash("test-only"); cna.setFirstName("Fixture"); cna.setLastName("CNA");
        cna = users.saveAndFlush(cna);
        var resident = residents.saveAndFlush(ResidentEntity.builder().firstName("Fixture").lastName("Resident")
                .dateOfBirth(LocalDate.of(1940, 1, 1)).status("ACTIVE").build());
        CarePlanSchema plan = new CarePlanSchema(); plan.setResident(resident); plan.setCreatedBy(cna); plan.setStatus("ACTIVE");
        plan.setCreatedAt(OffsetDateTime.now()); plan.setUpdatedAt(OffsetDateTime.now()); plans.saveAndFlush(plan);
        when(ai.execute(anyString(), anyString())).thenReturn("{\"title\":\"Fixture AI handover\",\"narrativeSummary\":\"Review verified mobility support.\",\"proposedActions\":[{\"residentId\":"
                + resident.getId() + ",\"description\":\"Review mobility support with the assigned CNA\",\"taskType\":\"MOBILITY\",\"sourceRefs\":[\"NOTE:1\"]}]}");
        var end = OffsetDateTime.now().minusMinutes(1);
        var created = draftService.createDraft(new ShiftSummaryDraftService.DraftRequest(end.minusHours(8), end, resident.getId(), "STAFF", "Verified mobility support needs follow-up"), "nurse@example.test");
        assertEquals("AI", created.draft().getGenerationMethod());
        var approved = workflow.approve(created.draftId(), new Review(created.draft().getTitle(), created.draft().getNarrativeSummary(), created.draft().getProposedActions()), "nurse@example.test");
        assertTrue(workflow.shared("don@example.test").stream().anyMatch(d -> d.id().equals(created.draftId())));
        long before = tasks.count();
        var assigned = workflow.assign(created.draftId(), approved.actions().get(0).action().id(), new Assignment(cna.getId(), OffsetDateTime.now().plusHours(2)), "don@example.test");
        var taskId = assigned.actions().get(0).action().careTaskId();
        em.flush(); em.clear();
        assertEquals(before + 1, tasks.count());
        var query = new GroupedTaskQuery(); query.setResidentId(resident.getId());
        assertTrue(taskViews.getTasksByResident(query).getData().stream().flatMap(g -> g.getTasks().stream()).anyMatch(t -> t.getId().equals(taskId)));
        query.setAssignedCnaId(cna.getId());
        assertTrue(taskViews.getTasksByCna(query).getData().stream().flatMap(g -> g.getTasks().stream()).anyMatch(t -> t.getId().equals(taskId)));
        workflow.assign(created.draftId(), approved.actions().get(0).action().id(), new Assignment(cna.getId(), OffsetDateTime.now().plusHours(3)), "other@example.test");
        assertEquals(before + 1, tasks.count());
        taskViews.completeTask(taskId, null);
        assertEquals("COMPLETED", workflow.get(created.draftId(), "don@example.test").actions().get(0).taskStatus());
    }
}
