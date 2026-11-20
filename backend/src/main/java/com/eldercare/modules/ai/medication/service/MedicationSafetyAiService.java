package com.eldercare.modules.ai.medication.service;

import com.eldercare.modules.ai.medication.catalog.AnticholinergicBurdenCatalog;
import com.eldercare.modules.ai.medication.catalog.BeersCriteriaCatalog;
import com.eldercare.modules.ai.medication.catalog.StoppStartCatalog;
import com.eldercare.modules.ai.medication.dto.MedicationCheckRequest;
import com.eldercare.modules.ai.medication.dto.MedicationCheckResponse;
import com.eldercare.modules.resident_intake.resident.repository.ResidentRepository;
import com.eldercare.modules.resident_intake.resident.repository.ClinicalRecordRepository;
import com.eldercare.modules.emar_medication.physician_orders.MedicationOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.time.OffsetDateTime;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Geriatric Clinical Decision Support System (CDSS) for Medication Safety and Polypharmacy.
 * Integrates:
 * 1. AGS Beers Criteria® 2023 (Potentially Inappropriate Medications in Older Adults)
 * 2. STOPP/START v3 Criteria (Geriatric Drug-Drug & Drug-Disease Interactions)
 * 3. Anticholinergic Cognitive Burden (ACB) Scale (Boustani et al. 2008 / Campbell et al. 2013)
 */
@Service
@RequiredArgsConstructor
public class MedicationSafetyAiService {
    private final ResidentRepository residentRepository;
    private final ClinicalRecordRepository clinicalRecordRepository;
    private final MedicationOrderRepository medicationOrderRepository;

    public MedicationCheckResponse checkSafety(MedicationCheckRequest request) {
        if (request.getResidentId() == null) {
            throw new IllegalArgumentException("Resident ID is required");
        }
        residentRepository.findByIdAndIsDeletedFalse(request.getResidentId())
                .orElseThrow(() -> new IllegalArgumentException("Resident not found"));

        var allergyRecords = clinicalRecordRepository
                .findByResidentIdAndRecordTypeAndIsDeletedFalse(request.getResidentId(), "ALLERGY");
        List<String> chartAllergies = allergyRecords
                .stream().map(a -> a.getDescription()).filter(value -> value != null && !value.isBlank()).toList();

        var diagnosisRecords = clinicalRecordRepository
                .findByResidentIdAndRecordTypeAndIsDeletedFalse(request.getResidentId(), "DIAGNOSIS");
        List<String> chartConditions = diagnosisRecords
                .stream().map(d -> d.getDescription()).filter(value -> value != null && !value.isBlank()).toList();

        if (request.getNewMedication() == null || request.getNewMedication().isBlank()) {
            throw new IllegalArgumentException("Medication name is required");
        }
        String newMedication = request.getNewMedication().trim();
        if (newMedication.length() > 200) {
            throw new IllegalArgumentException("Medication name is too long");
        }

        var activeOrders = medicationOrderRepository
                .findByResidentIdAndIsDeletedFalseAndStatusIgnoreCase(request.getResidentId(), "ACTIVE");
        List<String> chartMedications = activeOrders
                .stream().map(order -> order.getDrugName()).filter(name -> name != null && !name.isBlank())
                .distinct().toList();

        List<String> manualMedications = request.getCurrentMedications() == null ? List.of()
                : request.getCurrentMedications().stream().filter(name -> name != null && !name.isBlank())
                .limit(30).toList();

        List<String> allCurrentMeds = Stream.concat(chartMedications.stream(), manualMedications.stream())
                .distinct().toList();

        List<String> recordChecks = new ArrayList<>();
        List<String> dataGaps = new ArrayList<>();
        List<String> clinicalPrecautions = new ArrayList<>();

        if (chartMedications.isEmpty()) {
            recordChecks.add("No active medication orders found in this chart; reconcile with the current MAR.");
            dataGaps.add("NO_ACTIVE_CHART_ORDERS");
        }
        if (chartAllergies.isEmpty()) {
            recordChecks.add("No allergy entries found in this chart; verify the current allergy history before administration.");
            dataGaps.add("ALLERGY_HISTORY_NOT_RECORDED");
        }
        if (chartConditions.isEmpty()) {
            recordChecks.add("No diagnosis entries found in this chart; verify relevant conditions with the clinician.");
            dataGaps.add("DIAGNOSES_NOT_RECORDED");
        }

        // Duplicate order check
        if (allCurrentMeds.stream().anyMatch(name -> normalize(name).equals(normalize(newMedication)))) {
            recordChecks.add("Exact medication name is already present; verify whether this is a duplicate order.");
        }

        // Allergy check
        boolean hasAllergyMatch = false;
        String drugPattern = "(?iu)(^|[^\\p{L}\\p{N}])" + Pattern.quote(newMedication) + "([^\\p{L}\\p{N}]|$)";
        if (newMedication.length() >= 3 && chartAllergies.stream()
                .anyMatch(allergy -> allergy != null && Pattern.compile(drugPattern).matcher(allergy).find())) {
            hasAllergyMatch = true;
            recordChecks.add("CRITICAL ALLERGY ALERT: Exact medication name appears in the recorded allergy text; STOP and verify with prescriber.");
        }

        // --- 1. Evaluate AGS Beers Criteria® 2023 ---
        List<MedicationCheckResponse.BeersAlertDTO> beersAlerts = new ArrayList<>();
        List<MedicationCheckResponse.DeprescribingPlanDTO> deprescribingPlans = new ArrayList<>();

        // Check new medication
        var newMedBeers = BeersCriteriaCatalog.findPim(newMedication);
        if (newMedBeers.isPresent()) {
            var pim = newMedBeers.get();
            beersAlerts.add(MedicationCheckResponse.BeersAlertDTO.builder()
                    .medicationName(newMedication)
                    .category(pim.category())
                    .severity(pim.severity())
                    .rationale(pim.rationale())
                    .recommendation(pim.recommendation())
                    .saferAlternative(pim.saferAlternative())
                    .evidenceLevel(pim.evidenceLevel())
                    .build());

            deprescribingPlans.add(MedicationCheckResponse.DeprescribingPlanDTO.builder()
                    .targetMedication(newMedication)
                    .indicationReview("Verify clinical indication against Beers 2023 guidelines.")
                    .proposedAction("SWITCH_SAFER_AGENT")
                    .alternativeMedication(pim.saferAlternative())
                    .clinicalMonitoringNotes("Avoid initiating high-risk PIM in geriatric resident unless strict clinical rationale is documented.")
                    .build());
        }

        // Check existing chart & manual medications for existing PIMs
        for (String currentMed : allCurrentMeds) {
            var pimOpt = BeersCriteriaCatalog.findPim(currentMed);
            if (pimOpt.isPresent()) {
                var pim = pimOpt.get();
                beersAlerts.add(MedicationCheckResponse.BeersAlertDTO.builder()
                        .medicationName(currentMed)
                        .category(pim.category())
                        .severity(pim.severity())
                        .rationale(pim.rationale())
                        .recommendation(pim.recommendation())
                        .saferAlternative(pim.saferAlternative())
                        .evidenceLevel(pim.evidenceLevel())
                        .build());

                deprescribingPlans.add(MedicationCheckResponse.DeprescribingPlanDTO.builder()
                        .targetMedication(currentMed)
                        .indicationReview("Current active medication flagged under AGS Beers 2023 criteria.")
                        .proposedAction(pim.severity().equals("HIGH") ? "TAPER_DISCONTINUE" : "DOSE_REDUCTION")
                        .alternativeMedication(pim.saferAlternative())
                        .clinicalMonitoringNotes("Review for potential deprescribing or dose reduction during next interdisciplinary care conference.")
                        .build());
            }
        }

        // --- 2. Evaluate Anticholinergic Cognitive Burden (ACB) Scale ---
        List<String> combinedMedications = new ArrayList<>(allCurrentMeds);
        combinedMedications.add(newMedication);
        var acbAssessment = AnticholinergicBurdenCatalog.evaluateMedications(combinedMedications);

        List<MedicationCheckResponse.AcbScoreDetailDTO> acbDetails = acbAssessment.contributingMedications().stream()
                .map(item -> MedicationCheckResponse.AcbScoreDetailDTO.builder()
                        .medicationName(item.genericName())
                        .score(item.score())
                        .therapeuticClass(item.therapeuticClass())
                        .alternativeOption(item.clinicalAlternative())
                        .build())
                .toList();

        // --- 3. Evaluate STOPP/START v3 Criteria Interactions ---
        var stoppRawAlerts = StoppStartCatalog.detectInteractions(allCurrentMeds, newMedication);
        List<MedicationCheckResponse.StoppAlertDTO> stoppAlerts = stoppRawAlerts.stream()
                .map(alert -> MedicationCheckResponse.StoppAlertDTO.builder()
                        .ruleCode(alert.ruleCode())
                        .primaryMedication(alert.primaryMedication())
                        .secondaryMedication(alert.secondaryMedication())
                        .interactionType(alert.interactionType())
                        .severity(alert.severity())
                        .clinicalExplanation(alert.clinicalExplanation())
                        .actionRecommendation(alert.actionRecommendation())
                        .build())
                .toList();

        // Construct standard interaction details for UI display & backward compatibility
        List<MedicationCheckResponse.InteractionDetailDTO> interactionsList = new ArrayList<>();
        for (var alert : stoppRawAlerts) {
            interactionsList.add(MedicationCheckResponse.InteractionDetailDTO.builder()
                    .interactingSubstance(alert.secondaryMedication())
                    .severity(alert.severity())
                    .mechanism(alert.clinicalExplanation())
                    .recommendation(alert.actionRecommendation())
                    .build());
        }

        // Also add pair questions for other medications
        for (String currentMed : allCurrentMeds) {
            if (!normalize(currentMed).equals(normalize(newMedication))
                    && interactionsList.stream().noneMatch(i -> normalize(i.getInteractingSubstance()).contains(normalize(currentMed)))) {
                var acbMatch = AnticholinergicBurdenCatalog.findAcbScore(currentMed);
                String recommendation = acbMatch.isPresent()
                        ? "Active medication carries ACB score of " + acbMatch.get().score() + ". Reconcile cumulative burden before adding " + newMedication + "."
                        : "Verify whether " + newMedication + " and " + currentMed + " require dose titration or schedule separation.";
                interactionsList.add(MedicationCheckResponse.InteractionDetailDTO.builder()
                        .interactingSubstance(currentMed)
                        .severity(acbMatch.isPresent() ? "MODERATE" : "MONITOR")
                        .mechanism(acbMatch.isPresent() ? "Anticholinergic accumulation" : "Routine clinical surveillance")
                        .recommendation(recommendation)
                        .build());
            }
        }

        // --- Determine Final Risk Level & Safety Clearance ---
        String riskLevel;
        boolean isSafeToAdminister;
        String summaryWarning;

        if (hasAllergyMatch) {
            riskLevel = "CONTRAINDICATED";
            isSafeToAdminister = false;
            summaryWarning = "CONTRAINDICATION: Resident allergy record contains a match for " + newMedication + ". DO NOT ADMINISTER without explicit prescriber override.";
            clinicalPrecautions.add("Allergy verification required immediately with pharmacy and attending physician.");
        } else if (stoppRawAlerts.stream().anyMatch(a -> "CRITICAL".equalsIgnoreCase(a.severity()))) {
            riskLevel = "HIGH_RISK_PIM";
            isSafeToAdminister = false;
            summaryWarning = "CRITICAL DRUG INTERACTION DETECTED (STOPP v3): Severe synergistic risk identified (e.g. Sedative + Opioid or GI hemorrhage liability). Prescriber review mandatory.";
            clinicalPrecautions.add("Withhold administration until prescriber confirms clinical safety and monitoring protocol.");
        } else if (newMedBeers.isPresent() && "HIGH".equalsIgnoreCase(newMedBeers.get().severity())) {
            riskLevel = "HIGH_RISK_PIM";
            isSafeToAdminister = false;
            summaryWarning = "AGS BEERS CRITERIA® 2023 ALERT: " + newMedication + " is classified as a Potentially Inappropriate Medication for older adults. High risk of falls, delirium, or severe adverse events.";
            clinicalPrecautions.add("Check if safer alternative (" + newMedBeers.get().saferAlternative() + ") can be prescribed.");
        } else if (acbAssessment.totalAcbScore() >= 3) {
            riskLevel = "REVIEW_REQUIRED";
            isSafeToAdminister = false;
            summaryWarning = "HIGH ANTICHOLINERGIC COGNITIVE BURDEN (ACB Score: " + acbAssessment.totalAcbScore() + " >= 3): Proven >50% increase in delirium and cognitive decline risk. Interdisciplinary pharmacist review required.";
            clinicalPrecautions.add("Implement enhanced fall precautions and hydration monitoring.");
        } else if (!beersAlerts.isEmpty() || !stoppAlerts.isEmpty() || acbAssessment.totalAcbScore() >= 1) {
            riskLevel = "CAUTION_MONITOR";
            isSafeToAdminister = true;
            summaryWarning = "CAUTION: Moderate geriatric prescribing considerations detected (ACB Score: " + acbAssessment.totalAcbScore() + "). Proceed with clinical observation and MAR documentation.";
            clinicalPrecautions.add("Monitor resident for dizziness, daytime sedation, or anticholinergic symptoms.");
        } else {
            riskLevel = "MONITORED_SAFE";
            isSafeToAdminister = true;
            summaryWarning = "Medication reconciliation completed. No AGS Beers PIMs or high-risk STOPP interactions detected for " + newMedication + ".";
            clinicalPrecautions.add("Standard 5-Rights medication administration precautions apply.");
        }

        return MedicationCheckResponse.builder()
                .riskLevel(riskLevel)
                .isSafeToAdminister(isSafeToAdminister)
                .summaryWarning(summaryWarning)
                .interactions(interactionsList)
                .clinicalPrecautions(clinicalPrecautions)
                .chartMedications(chartMedications)
                .manuallyReportedMedications(manualMedications)
                .chartAllergies(chartAllergies)
                .chartConditions(chartConditions)
                .recordChecks(recordChecks)
                .generatedAt(OffsetDateTime.now())
                .methodVersion("geriatric-cdss-beers2023-stopp3-v1")
                .interactionReferenceStatus("ACTIVE_AGS_BEERS_2023_STOPP_V3")
                .sourceOrderIds(activeOrders.stream().map(order -> order.getId())
                        .filter(id -> id != null).toList())
                .sourceAllergyRecordIds(allergyRecords.stream().map(record -> record.getId())
                        .filter(id -> id != null).toList())
                .sourceDiagnosisRecordIds(diagnosisRecords.stream().map(record -> record.getId())
                        .filter(id -> id != null).toList())
                .dataGaps(dataGaps)
                .totalAcbScore(acbAssessment.totalAcbScore())
                .acbRiskTier(acbAssessment.riskTier())
                .acbClinicalImpact(acbAssessment.clinicalInterpretation())
                .acbContributingMedications(acbDetails)
                .beersCriteriaAlerts(beersAlerts)
                .stoppAlerts(stoppAlerts)
                .deprescribingGuidance(deprescribingPlans)
                .build();
    }

    private static String normalize(String value) {
        return value.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }
}
