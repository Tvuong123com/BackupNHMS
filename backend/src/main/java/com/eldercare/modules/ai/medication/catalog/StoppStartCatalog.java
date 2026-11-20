package com.eldercare.modules.ai.medication.catalog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * STOPP/START criteria v3 (Screening Tool of Older Persons' Prescriptions)
 * Reference: O'Mahony D, et al. STOPP/START criteria for potentially inappropriate
 * prescribing in older people: version 3. Eur Geriatr Med. 2023.
 */
public final class StoppStartCatalog {

    public record StoppAlert(
            String ruleCode,
            String primaryMedication,
            String secondaryMedication,
            String interactionType, // DRUG_DRUG_ANTAGONISM, SYNERGISTIC_CNS_DEPRESSION, GI_HEMORRHAGE_RISK, THERAPEUTIC_DUPLICATION
            String severity, // HIGH, CRITICAL, MODERATE
            String clinicalExplanation,
            String actionRecommendation
    ) {}

    private StoppStartCatalog() {}

    private static final List<String> ACETYLCHOLINESTERASE_INHIBITORS = List.of(
            "donepezil", "aricept", "rivastigmine", "exelon", "galantamine", "reminyl"
    );

    private static final List<String> OPIOIDS = List.of(
            "tramadol", "morphine", "codeine", "fentanyl", "oxycodone", "hydrocodone",
            "buprenorphine", "hydromorphone", "dilaudid", "duragesic", "ultram"
    );

    private static final List<String> SEDATIVE_BENZOS = List.of(
            "diazepam", "lorazepam", "alprazolam", "clonazepam", "temazepam", "chlordiazepoxide",
            "zolpidem", "eszopiclone", "zaleplon", "ambien", "ativan", "valium", "xanax"
    );

    private static final List<String> NSAIDS = List.of(
            "ibuprofen", "naproxen", "meloxicam", "indomethacin", "ketorolac", "diclofenac",
            "piroxicam", "celecoxib", "motrin", "advil", "aleve", "mobic", "toradol"
    );

    private static final List<String> SYSTEMIC_STEROIDS = List.of(
            "prednisone", "prednisolone", "methylprednisolone", "dexamethasone", "hydrocortisone", "medrol"
    );

    private static final List<String> ANTICOAGULANTS_ANTIPLATELETS = List.of(
            "warfarin", "coumadin", "apixaban", "eliquis", "rivaroxaban", "xarelto",
            "dabigatran", "pradaxa", "clopidogrel", "plavix", "enoxaparin", "lovenox"
    );

    public static List<StoppAlert> detectInteractions(List<String> currentMedications, String newMedication) {
        List<StoppAlert> alerts = new ArrayList<>();
        List<String> allMeds = new ArrayList<>();
        if (currentMedications != null) {
            allMeds.addAll(currentMedications);
        }
        if (newMedication != null && !newMedication.isBlank()) {
            allMeds.add(newMedication);
        }

        // 1. Anticholinergic + Acetylcholinesterase Inhibitor antagonism
        boolean hasAchei = allMeds.stream().anyMatch(StoppStartCatalog::isAchei);
        if (hasAchei) {
            for (String med : allMeds) {
                var acb = AnticholinergicBurdenCatalog.findAcbScore(med);
                if (acb.isPresent() && acb.get().score() >= 2) {
                    alerts.add(new StoppAlert(
                            "STOPP-K1",
                            med,
                            "Cholinesterase Inhibitor (Donepezil/Rivastigmine)",
                            "DRUG_DRUG_ANTAGONISM",
                            "HIGH",
                            "Pharmacodynamic antagonism: '" + med + "' has significant anticholinergic activity that directly opposes "
                                    + "the cognitive benefits of Alzheimer's cholinesterase inhibitors, accelerating cognitive worsening.",
                            "Avoid co-prescribing. Deprescribe or switch anticholinergic medication to a non-anticholinergic alternative."
                    ));
                }
            }
        }

        // 2. Benzodiazepine / Sedative + Opioid
        boolean hasSedative = allMeds.stream().anyMatch(StoppStartCatalog::isSedative);
        boolean hasOpioid = allMeds.stream().anyMatch(StoppStartCatalog::isOpioid);
        if (hasSedative && hasOpioid) {
            String sedativeName = allMeds.stream().filter(StoppStartCatalog::isSedative).findFirst().orElse("Sedative");
            String opioidName = allMeds.stream().filter(StoppStartCatalog::isOpioid).findFirst().orElse("Opioid");
            alerts.add(new StoppAlert(
                    "STOPP-D5",
                    sedativeName,
                    opioidName,
                    "SYNERGISTIC_CNS_DEPRESSION",
                    "CRITICAL",
                    "Concomitant sedative/benzodiazepine and opioid therapy produces severe synergistic central nervous system "
                            + "and respiratory depression, quadrupling sedation, aspiration pneumonia, falls, and fatal overdose risk.",
                    "FDA Black Box Warning. Taper benzodiazepine or lower opioid dose to the minimum necessary with close respiratory and fall monitoring."
            ));
        }

        // 3. NSAID + Systemic Corticosteroid
        boolean hasNsaid = allMeds.stream().anyMatch(StoppStartCatalog::isNsaid);
        boolean hasSteroid = allMeds.stream().anyMatch(StoppStartCatalog::isSteroid);
        if (hasNsaid && hasSteroid) {
            String nsaidName = allMeds.stream().filter(StoppStartCatalog::isNsaid).findFirst().orElse("NSAID");
            String steroidName = allMeds.stream().filter(StoppStartCatalog::isSteroid).findFirst().orElse("Corticosteroid");
            alerts.add(new StoppAlert(
                    "STOPP-A1",
                    nsaidName,
                    steroidName,
                    "GI_HEMORRHAGE_RISK",
                    "HIGH",
                    "Concurrent oral NSAID and systemic corticosteroid dramatically elevates gastroduodenal ulceration, perforation, "
                            + "and gastrointestinal hemorrhage in older adults (up to 12-15x higher risk).",
                    "Discontinue NSAID and switch to Acetaminophen / topical analgesia; if NSAID cannot be avoided, co-prescribe PPI gastroprotection."
            ));
        }

        // 4. Anticoagulant + NSAID
        boolean hasAnticoag = allMeds.stream().anyMatch(StoppStartCatalog::isAnticoagulant);
        if (hasNsaid && hasAnticoag) {
            String nsaidName = allMeds.stream().filter(StoppStartCatalog::isNsaid).findFirst().orElse("NSAID");
            String anticoagName = allMeds.stream().filter(StoppStartCatalog::isAnticoagulant).findFirst().orElse("Anticoagulant");
            alerts.add(new StoppAlert(
                    "STOPP-C2",
                    nsaidName,
                    anticoagName,
                    "GI_HEMORRHAGE_RISK",
                    "CRITICAL",
                    "NSAIDs impair platelet aggregation and damage gastric mucosa, creating a severe bleeding risk when combined with anticoagulants.",
                    "Avoid oral NSAIDs in residents on systemic anticoagulation. Use Acetaminophen or topical Diclofenac instead."
            ));
        }

        return alerts;
    }

    private static boolean isAchei(String med) {
        return matchesAny(med, ACETYLCHOLINESTERASE_INHIBITORS);
    }

    private static boolean isOpioid(String med) {
        return matchesAny(med, OPIOIDS);
    }

    private static boolean isSedative(String med) {
        return matchesAny(med, SEDATIVE_BENZOS);
    }

    private static boolean isNsaid(String med) {
        return matchesAny(med, NSAIDS);
    }

    private static boolean isSteroid(String med) {
        return matchesAny(med, SYSTEMIC_STEROIDS);
    }

    private static boolean isAnticoagulant(String med) {
        return matchesAny(med, ANTICOAGULANTS_ANTIPLATELETS);
    }

    private static boolean matchesAny(String text, List<String> targets) {
        if (text == null || text.isBlank()) return false;
        String norm = text.trim().toLowerCase(Locale.ROOT);
        for (String target : targets) {
            String pat = "(?iu)(^|[^\\p{L}\\p{N}])" + Pattern.quote(target) + "([^\\p{L}\\p{N}]|$)";
            if (Pattern.compile(pat).matcher(norm).find() || norm.contains(target)) {
                return true;
            }
        }
        return false;
    }
}
