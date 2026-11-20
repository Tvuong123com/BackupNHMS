package com.eldercare.modules.ai.risk.catalog;

import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Evidence-based catalog of Fall-Risk Increasing Drugs (FRIDs)
 * compiled from CDC STEADI (Stopping Elderly Accidents, Deaths, & Injuries),
 * AGS Beers Criteria 2023, and geriatric clinical practice guidelines.
 */
@Component
public class FridMedicationCatalog {

    public enum FridCategory {
        SEDATIVE_HYPNOTIC("Sedatives & Hypnotics", 15, "High risk of nighttime ataxia, confusion, and grogginess"),
        BENZODIAZEPINE("Benzodiazepines", 15, "Significant psychomotor impairment, delayed reaction time, and balance loss"),
        ANTIPSYCHOTIC("Antipsychotics", 10, "Extrapyramidal symptoms, sedation, and orthostatic hypotension"),
        ANTIDEPRESSANT("Antidepressants / TCAs", 10, "Anticholinergic effects, sedation, and dizziness upon standing"),
        ANTIHYPERTENSIVE_DIURETIC("Antihypertensives / Diuretics", 10, "Orthostatic hypotension and volume depletion leading to syncopal falls"),
        ANTICHOLINERGIC("High Anticholinergic Burden", 10, "Blurred vision, central confusion, and acute delirium risk");

        private final String displayName;
        private final int baseWeight;
        private final String clinicalMechanism;

        FridCategory(String displayName, int baseWeight, String clinicalMechanism) {
            this.displayName = displayName;
            this.baseWeight = baseWeight;
            this.clinicalMechanism = clinicalMechanism;
        }

        public String getDisplayName() { return displayName; }
        public int getBaseWeight() { return baseWeight; }
        public String getClinicalMechanism() { return clinicalMechanism; }
    }

    @Data
    @Builder
    public static class FridRule {
        private String genericName;
        private FridCategory category;
        private List<String> brandNames;
        private String clinicalGuidance;
    }

    @Data
    @Builder
    public static class FridMatch {
        private String detectedMedication;
        private String genericName;
        private FridCategory category;
        private int riskScore;
        private String clinicalWarning;
    }

    private final List<FridRule> rules = List.of(
            // Sedatives & Hypnotics (Z-drugs)
            rule("zolpidem", FridCategory.SEDATIVE_HYPNOTIC, List.of("ambien", "intermezzo"), "Z-drug hypnotic; causes acute sleep-inertia, nighttime disorientation, and high fall risk."),
            rule("eszopiclone", FridCategory.SEDATIVE_HYPNOTIC, List.of("lunesta"), "Sedative hypnotic; prolonged morning sedation and balance instability."),
            rule("zaleplon", FridCategory.SEDATIVE_HYPNOTIC, List.of("sonata"), "Short-acting hypnotic; acute ataxia if resident ambulates before drug clearance."),
            rule("temazepam", FridCategory.SEDATIVE_HYPNOTIC, List.of("restoril"), "Intermediate-acting benzodiazepine hypnotic; high risk of nocturnal falls."),

            // Benzodiazepines
            rule("diazepam", FridCategory.BENZODIAZEPINE, List.of("valium"), "Long-acting benzodiazepine; active metabolites accumulate in elderly, severe fall risk."),
            rule("lorazepam", FridCategory.BENZODIAZEPINE, List.of("ativan"), "Intermediate benzodiazepine; high risk of motor unsteadiness and sedation."),
            rule("alprazolam", FridCategory.BENZODIAZEPINE, List.of("xanax"), "Short-acting benzodiazepine; rebound anxiety and cognitive clouding."),
            rule("clonazepam", FridCategory.BENZODIAZEPINE, List.of("klonopin"), "Long-acting anticonvulsant/anxiolytic; causes marked gait impairment."),

            // Antipsychotics
            rule("haloperidol", FridCategory.ANTIPSYCHOTIC, List.of("haldol"), "Typical antipsychotic; prominent parkinsonian gait, muscle stiffness, and falls."),
            rule("quetiapine", FridCategory.ANTIPSYCHOTIC, List.of("seroquel"), "Atypical antipsychotic; strong alpha-1 blockade causing orthostatic hypotension and somnolence."),
            rule("risperidone", FridCategory.ANTIPSYCHOTIC, List.of("risperdal"), "Atypical antipsychotic; causes dose-dependent extrapyramidal symptoms and gait instability."),
            rule("olanzapine", FridCategory.ANTIPSYCHOTIC, List.of("zyprexa"), "Atypical antipsychotic; significant sedation, weight gain, and orthostasis."),

            // Antidepressants (TCAs & High-Risk)
            rule("amitriptyline", FridCategory.ANTIDEPRESSANT, List.of("elavil"), "Tricyclic antidepressant; potent anticholinergic & anti-alpha-adrenergic effects causing severe orthostatic falls."),
            rule("nortriptyline", FridCategory.ANTIDEPRESSANT, List.of("pamelor"), "Secondary amine TCA; moderate-to-high fall risk from orthostasis and sedation."),
            rule("trazodone", FridCategory.ANTIDEPRESSANT, List.of("desyrel"), "SARI antidepressant commonly used off-label for sleep; frequent cause of orthostatic falls in older adults."),
            rule("paroxetine", FridCategory.ANTIDEPRESSANT, List.of("paxil"), "Most anticholinergic SSRI; increases confusion and unsteadiness."),

            // Antihypertensives & Diuretics
            rule("furosemide", FridCategory.ANTIHYPERTENSIVE_DIURETIC, List.of("lasix"), "Loop diuretic; causes urgent nocturia, hypovolemia, and postural hypotension."),
            rule("hydrochlorothiazide", FridCategory.ANTIHYPERTENSIVE_DIURETIC, List.of("microzide", "hctz"), "Thiazide diuretic; hyponatremia and orthostatic hypotension upon bed transfer."),
            rule("lisinopril", FridCategory.ANTIHYPERTENSIVE_DIURETIC, List.of("zestril", "prinivil"), "ACE inhibitor; monitor for orthostatic blood pressure drops upon standing."),
            rule("amlodipine", FridCategory.ANTIHYPERTENSIVE_DIURETIC, List.of("norvasc"), "Calcium channel blocker; peripheral edema, vasodilation, and dizziness."),
            rule("clonidine", FridCategory.ANTIHYPERTENSIVE_DIURETIC, List.of("catapres"), "Centrally acting alpha-2 agonist; severe bradycardia, dry mouth, and syncope risk.")
    );

    private static FridRule rule(String generic, FridCategory category, List<String> brands, String guidance) {
        return FridRule.builder()
                .genericName(generic.toLowerCase(Locale.ROOT))
                .category(category)
                .brandNames(brands.stream().map(b -> b.toLowerCase(Locale.ROOT)).toList())
                .clinicalGuidance(guidance)
                .build();
    }

    /**
     * Evaluates a list of active medication names against the FRID catalog.
     */
    public List<FridMatch> matchFridMedications(List<String> medicationNames) {
        if (medicationNames == null || medicationNames.isEmpty()) {
            return List.of();
        }

        List<FridMatch> matches = new ArrayList<>();
        Set<String> matchedGenerics = new HashSet<>();

        for (String medName : medicationNames) {
            if (medName == null || medName.isBlank()) continue;
            String normalized = medName.toLowerCase(Locale.ROOT).trim();

            for (FridRule rule : rules) {
                if (matchedGenerics.contains(rule.getGenericName())) continue;

                boolean nameMatches = matchesDrug(normalized, rule.getGenericName())
                        || rule.getBrandNames().stream().anyMatch(b -> matchesDrug(normalized, b));

                if (nameMatches) {
                    matchedGenerics.add(rule.getGenericName());
                    matches.add(FridMatch.builder()
                            .detectedMedication(medName)
                            .genericName(rule.getGenericName())
                            .category(rule.getCategory())
                            .riskScore(rule.getCategory().getBaseWeight())
                            .clinicalWarning(rule.getClinicalGuidance())
                            .build());
                    break;
                }
            }
        }
        return matches;
    }

    private static boolean matchesDrug(String medName, String target) {
        if (medName.equals(target)) return true;
        String pattern = "(?i)(^|[^a-z0-9])" + Pattern.quote(target) + "([^a-z0-9]|$)";
        return Pattern.compile(pattern).matcher(medName).find();
    }
}
