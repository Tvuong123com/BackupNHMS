package com.eldercare.modules.ai;

import com.eldercare.modules.ai.elopement.dto.ElopementRiskRequest;
import com.eldercare.modules.ai.elopement.service.ElopementRiskAiService;
import com.eldercare.modules.ai.medication.dto.MedicationCheckRequest;
import com.eldercare.modules.ai.medication.service.MedicationSafetyAiService;
import com.eldercare.modules.ai.engine.AiExecutionService;
import com.eldercare.modules.resident_intake.resident.repository.ResidentRepository;
import com.eldercare.modules.resident_intake.resident.repository.ClinicalRecordRepository;
import com.eldercare.modules.resident_intake.resident_profile.ResidentEntity;
import com.eldercare.modules.emar_medication.physician_orders.MedicationOrderEntity;
import com.eldercare.modules.emar_medication.physician_orders.MedicationOrderRepository;
import com.eldercare.modules.clinical.clinical_record.ClinicalRecordEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MedicationAndElopementAiTest {
    private final AiExecutionService execution = mock(AiExecutionService.class);
    private final ResidentRepository residents = mock(ResidentRepository.class);
    private final ClinicalRecordRepository records = mock(ClinicalRecordRepository.class);
    private final MedicationOrderRepository orders = mock(MedicationOrderRepository.class);
    private final ObjectMapper mapper = new ObjectMapper();

    private void residentExists() {
        when(residents.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(
                ResidentEntity.builder().id(1L).firstName("Test").lastName("Resident").build()));
    }

    @Test
    void medicationChecklistCannotClearAdministrationOrAssertUnverifiedInteraction() {
        residentExists();
        when(records.findByResidentIdAndRecordTypeAndIsDeletedFalse(eq(1L), anyString())).thenReturn(List.of());
        when(execution.execute(anyString(), anyString())).thenReturn("""
                {"riskLevel":"SAFE","isSafeToAdminister":true,"summaryWarning":"No concern",
                 "interactions":[],"clinicalPrecautions":[]}
                """);
        var service = new MedicationSafetyAiService(residents, records, orders);
        var response = service.checkSafety(MedicationCheckRequest.builder()
                .residentId(1L).newMedication("Example medication").build());
        assertFalse(response.getIsSafeToAdminister());
        assertEquals("REVIEW_REQUIRED", response.getRiskLevel());
        assertEquals("NOT_CONNECTED", response.getInteractionReferenceStatus());
        assertEquals("medication-reconciliation-v1", response.getMethodVersion());
        assertTrue(response.getDataGaps().contains("INTERACTION_REFERENCE_NOT_CONNECTED"));
        assertTrue(response.getSummaryWarning().contains("has not been determined"));
        assertTrue(response.getRecordChecks().stream().anyMatch(check -> check.contains("No allergy entries")));
        verify(execution, never()).execute(anyString(), anyString());
    }

    @Test
    void modelCannotActivateDoorControl() {
        residentExists();
        when(records.findByResidentIdAndRecordTypeAndIsDeletedFalse(1L, "DIAGNOSIS")).thenReturn(List.of());
        when(execution.execute(anyString(), anyString())).thenReturn("""
                {"riskLevel":"HIGH","riskScore":8.5,"triggerDoorLockAlert":true,
                 "behavioralAnalysis":"Observed wandering","immediateSafeguards":[]}
                """);
        var service = new ElopementRiskAiService(execution, mapper, residents, records);
        var response = service.evaluateRisk(ElopementRiskRequest.builder()
                .residentId(1L).recentBehaviors(List.of("Approached exit twice")).build());
        assertFalse(response.getTriggerDoorLockAlert());
    }

    @Test
    void medicationReviewRequiresResident() {
        var service = new MedicationSafetyAiService(residents, records, orders);
        assertThrows(IllegalArgumentException.class, () -> service.checkSafety(
                MedicationCheckRequest.builder().newMedication("Example").build()));
    }

    @Test
    void reviewUsesActiveChartOrdersAndFlagsExactAllergyAndDuplicate() {
        residentExists();
        var allergy = ClinicalRecordEntity.builder().id(21L).description("Reported allergy to Penicillin: rash")
                .recordType("ALLERGY").build();
        when(records.findByResidentIdAndRecordTypeAndIsDeletedFalse(1L, "ALLERGY"))
                .thenReturn(List.of(allergy));
        when(records.findByResidentIdAndRecordTypeAndIsDeletedFalse(1L, "DIAGNOSIS"))
                .thenReturn(List.of());
        MedicationOrderEntity active = new MedicationOrderEntity();
        active.setId(11L);
        active.setDrugName("Penicillin");
        when(orders.findByResidentIdAndIsDeletedFalseAndStatusIgnoreCase(1L, "ACTIVE"))
                .thenReturn(List.of(active));
        var service = new MedicationSafetyAiService(residents, records, orders);

        var result = service.checkSafety(MedicationCheckRequest.builder()
                .residentId(1L).newMedication("Penicillin").build());

        assertEquals(List.of("Penicillin"), result.getChartMedications());
        assertEquals(List.of("Reported allergy to Penicillin: rash"), result.getChartAllergies());
        assertEquals(List.of(11L), result.getSourceOrderIds());
        assertEquals(List.of(21L), result.getSourceAllergyRecordIds());
        assertTrue(result.getDataGaps().contains("DIAGNOSES_NOT_RECORDED"));
        assertEquals(3, result.getRecordChecks().size());
        assertFalse(result.getIsSafeToAdminister());
    }
}
