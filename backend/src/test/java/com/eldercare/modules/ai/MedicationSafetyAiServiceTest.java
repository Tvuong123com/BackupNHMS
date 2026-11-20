package com.eldercare.modules.ai;

import com.eldercare.modules.ai.medication.dto.MedicationCheckRequest;
import com.eldercare.modules.ai.medication.dto.MedicationCheckResponse;
import com.eldercare.modules.ai.medication.service.MedicationSafetyAiService;
import com.eldercare.modules.clinical.clinical_record.ClinicalRecordEntity;
import com.eldercare.modules.emar_medication.physician_orders.MedicationOrderEntity;
import com.eldercare.modules.emar_medication.physician_orders.MedicationOrderRepository;
import com.eldercare.modules.resident_intake.resident.repository.ClinicalRecordRepository;
import com.eldercare.modules.resident_intake.resident.repository.ResidentRepository;
import com.eldercare.modules.resident_intake.resident_profile.ResidentEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MedicationSafetyAiServiceTest {

    private ResidentRepository residentRepository;
    private ClinicalRecordRepository clinicalRecordRepository;
    private MedicationOrderRepository medicationOrderRepository;
    private MedicationSafetyAiService service;

    @BeforeEach
    void setUp() {
        residentRepository = mock(ResidentRepository.class);
        clinicalRecordRepository = mock(ClinicalRecordRepository.class);
        medicationOrderRepository = mock(MedicationOrderRepository.class);
        service = new MedicationSafetyAiService(residentRepository, clinicalRecordRepository, medicationOrderRepository);

        ResidentEntity resident = ResidentEntity.builder().id(10L).firstName("John").lastName("Doe").build();
        when(residentRepository.findByIdAndIsDeletedFalse(10L)).thenReturn(Optional.of(resident));
    }

    @Test
    void rejectsWhenResidentNotFound() {
        when(residentRepository.findByIdAndIsDeletedFalse(99L)).thenReturn(Optional.empty());
        var request = MedicationCheckRequest.builder().residentId(99L).newMedication("Aspirin").build();
        assertThrows(IllegalArgumentException.class, () -> service.checkSafety(request));
    }

    @Test
    void detectsKnownAllergyAsContraindication() {
        ClinicalRecordEntity allergy = new ClinicalRecordEntity();
        allergy.setId(50L);
        allergy.setDescription("Severe rash to Penicillin");
        when(clinicalRecordRepository.findByResidentIdAndRecordTypeAndIsDeletedFalse(10L, "ALLERGY"))
                .thenReturn(List.of(allergy));
        when(clinicalRecordRepository.findByResidentIdAndRecordTypeAndIsDeletedFalse(10L, "DIAGNOSIS"))
                .thenReturn(List.of());
        when(medicationOrderRepository.findByResidentIdAndIsDeletedFalseAndStatusIgnoreCase(10L, "ACTIVE"))
                .thenReturn(List.of());

        var request = MedicationCheckRequest.builder()
                .residentId(10L)
                .newMedication("Penicillin")
                .build();

        var response = service.checkSafety(request);

        assertEquals("CONTRAINDICATED", response.getRiskLevel());
        assertFalse(response.getIsSafeToAdminister());
        assertTrue(response.getSummaryWarning().contains("CONTRAINDICATION"));
        assertTrue(response.getRecordChecks().stream().anyMatch(c -> c.contains("CRITICAL ALLERGY ALERT")));
    }

    @Test
    void detectsBeersPimAndHighAnticholinergicBurden() {
        when(clinicalRecordRepository.findByResidentIdAndRecordTypeAndIsDeletedFalse(10L, "ALLERGY"))
                .thenReturn(List.of());
        when(clinicalRecordRepository.findByResidentIdAndRecordTypeAndIsDeletedFalse(10L, "DIAGNOSIS"))
                .thenReturn(List.of());
        when(medicationOrderRepository.findByResidentIdAndIsDeletedFalseAndStatusIgnoreCase(10L, "ACTIVE"))
                .thenReturn(List.of());

        // Test Diphenhydramine (Beers PIM + ACB Score 3)
        var request = MedicationCheckRequest.builder()
                .residentId(10L)
                .newMedication("Diphenhydramine")
                .build();

        var response = service.checkSafety(request);

        assertEquals("HIGH_RISK_PIM", response.getRiskLevel());
        assertFalse(response.getIsSafeToAdminister());
        assertTrue(response.getTotalAcbScore() >= 3);
        assertEquals("HIGH", response.getAcbRiskTier());
        assertFalse(response.getBeersCriteriaAlerts().isEmpty());
        assertFalse(response.getDeprescribingGuidance().isEmpty());
        assertEquals("ACTIVE_AGS_BEERS_2023_STOPP_V3", response.getInteractionReferenceStatus());
    }

    @Test
    void detectsStoppDrugDrugAntagonismWithDementiaTherapy() {
        when(clinicalRecordRepository.findByResidentIdAndRecordTypeAndIsDeletedFalse(10L, "ALLERGY"))
                .thenReturn(List.of());
        when(clinicalRecordRepository.findByResidentIdAndRecordTypeAndIsDeletedFalse(10L, "DIAGNOSIS"))
                .thenReturn(List.of());

        // Resident already on Donepezil (AChEI for Alzheimer's)
        MedicationOrderEntity activeOrder = new MedicationOrderEntity();
        activeOrder.setId(101L);
        activeOrder.setDrugName("Donepezil 10mg daily");
        when(medicationOrderRepository.findByResidentIdAndIsDeletedFalseAndStatusIgnoreCase(10L, "ACTIVE"))
                .thenReturn(List.of(activeOrder));

        // Prescriber proposes Oxybutynin (Urinary antispasmodic with potent anticholinergic effect)
        var request = MedicationCheckRequest.builder()
                .residentId(10L)
                .newMedication("Oxybutynin 5mg")
                .build();

        var response = service.checkSafety(request);

        assertNotNull(response.getStoppAlerts());
        assertTrue(response.getStoppAlerts().stream().anyMatch(a -> a.getRuleCode().equals("STOPP-K1")));
        assertTrue(response.getStoppAlerts().stream().anyMatch(a -> a.getInteractionType().equals("DRUG_DRUG_ANTAGONISM")));
        assertTrue(response.getTotalAcbScore() >= 3);
    }

    @Test
    void detectsCriticalSedativeOpioidSynergism() {
        when(clinicalRecordRepository.findByResidentIdAndRecordTypeAndIsDeletedFalse(10L, "ALLERGY"))
                .thenReturn(List.of());
        when(clinicalRecordRepository.findByResidentIdAndRecordTypeAndIsDeletedFalse(10L, "DIAGNOSIS"))
                .thenReturn(List.of());

        // Resident on Tramadol for chronic pain
        MedicationOrderEntity activeOrder = new MedicationOrderEntity();
        activeOrder.setId(102L);
        activeOrder.setDrugName("Tramadol 50mg TID");
        when(medicationOrderRepository.findByResidentIdAndIsDeletedFalseAndStatusIgnoreCase(10L, "ACTIVE"))
                .thenReturn(List.of(activeOrder));

        // Prescriber adds Diazepam for muscle spasms
        var request = MedicationCheckRequest.builder()
                .residentId(10L)
                .newMedication("Diazepam 5mg")
                .build();

        var response = service.checkSafety(request);

        assertEquals("HIGH_RISK_PIM", response.getRiskLevel());
        assertFalse(response.getIsSafeToAdminister());
        assertTrue(response.getStoppAlerts().stream().anyMatch(a -> a.getSeverity().equals("CRITICAL")));
        assertTrue(response.getStoppAlerts().stream().anyMatch(a -> a.getRuleCode().equals("STOPP-D5")));
    }

    @Test
    void clearsSafeGeriatricMedicationWithMonitoring() {
        when(clinicalRecordRepository.findByResidentIdAndRecordTypeAndIsDeletedFalse(10L, "ALLERGY"))
                .thenReturn(List.of());
        when(clinicalRecordRepository.findByResidentIdAndRecordTypeAndIsDeletedFalse(10L, "DIAGNOSIS"))
                .thenReturn(List.of());
        when(medicationOrderRepository.findByResidentIdAndIsDeletedFalseAndStatusIgnoreCase(10L, "ACTIVE"))
                .thenReturn(List.of());

        // Low-risk medication: Vitamin D3 (Cholecalciferol)
        var request = MedicationCheckRequest.builder()
                .residentId(10L)
                .newMedication("Cholecalciferol (Vitamin D3) 1000 IU")
                .build();

        var response = service.checkSafety(request);

        assertEquals("MONITORED_SAFE", response.getRiskLevel());
        assertTrue(response.getIsSafeToAdminister());
        assertEquals(0, response.getTotalAcbScore());
        assertEquals("MINIMAL", response.getAcbRiskTier());
        assertTrue(response.getBeersCriteriaAlerts().isEmpty());
    }
}
