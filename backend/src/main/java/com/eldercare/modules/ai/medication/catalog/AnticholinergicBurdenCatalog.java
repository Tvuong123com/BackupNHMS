package com.eldercare.modules.ai.medication.catalog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Anticholinergic Cognitive Burden (ACB) Scale implementation based on:
 * Boustani M, et al. "Impact of anticholinergics on cognitive decline in older adults."
 * Aging & Mental Health, 2008; Campbell N, et al. J Am Geriatr Soc, 2013.
 *
 * Scoring:
 * 1 = Mild / potential anticholinergic effect
 * 2 = Moderate anticholinergic effect
 * 3 = Severe anticholinergic effect
 * Total score >= 3 is associated with a clinically significant increase in delirium,
 * dementia progression, falls, and all-cause mortality in nursing home populations.
 */
public final class AnticholinergicBurdenCatalog {

    public record AcbDrugScore(
            String drugKey,
            String genericName,
            int score,
            String therapeuticClass,
            String clinicalAlternative
    ) {}

    public record AcbAssessmentResult(
            int totalAcbScore,
            String riskTier, // MINIMAL (0), MODERATE (1-2), HIGH (>=3)
            String clinicalInterpretation,
            List<AcbDrugScore> contributingMedications,
            List<String> clinicalRecommendations
    ) {}

    private static final List<AcbDrugScore> ACB_CATALOG = List.of(
            // --- ACB SCORE 3: Severe Anticholinergic Burden ---
            new AcbDrugScore("amitriptyline", "Amitriptyline", 3, "TCA Antidepressant", "Sertraline, Escitalopram, or Duloxetine"),
            new AcbDrugScore("atropine", "Atropine", 3, "Anticholinergic / Antimuscarinic", "Non-anticholinergic alternatives"),
            new AcbDrugScore("chlorpheniramine", "Chlorpheniramine", 3, "1st Gen Antihistamine", "Cetirizine, Loratadine"),
            new AcbDrugScore("clomipramine", "Clomipramine", 3, "TCA Antidepressant", "SSRI or SNRI"),
            new AcbDrugScore("cyproheptadine", "Cyproheptadine", 3, "Antihistamine / Serotonin Antagonist", "Mirtazapine or Megestrol (short term)"),
            new AcbDrugScore("diphenhydramine", "Diphenhydramine", 3, "1st Gen Antihistamine", "Loratadine, Cetirizine, Sleep hygiene"),
            new AcbDrugScore("doxepin", "Doxepin", 3, "TCA Antidepressant / Hypnotic", "Melatonin, CBT-I"),
            new AcbDrugScore("hydroxyzine", "Hydroxyzine", 3, "1st Gen Antihistamine / Anxiolytic", "Buspirone, SSRIs"),
            new AcbDrugScore("hyoscyamine", "Hyoscyamine", 3, "Antispasmodic", "Dietary fiber, peppermint oil, hydration"),
            new AcbDrugScore("imipramine", "Imipramine", 3, "TCA Antidepressant", "Sertraline, Escitalopram"),
            new AcbDrugScore("nortriptyline", "Nortriptyline", 3, "TCA Antidepressant", "Duloxetine or Gabapentin (dose-adjusted)"),
            new AcbDrugScore("olanzapine", "Olanzapine", 3, "Atypical Antipsychotic", "Non-pharmacological de-escalation, low-dose Trazodone"),
            new AcbDrugScore("oxybutynin", "Oxybutynin", 3, "Urinary Antispasmodic", "Mirabegron, scheduled bladder training"),
            new AcbDrugScore("paroxetine", "Paroxetine", 3, "SSRI (High Anticholinergic)", "Sertraline, Citalopram, Escitalopram"),
            new AcbDrugScore("promethazine", "Promethazine", 3, "Phenothiazine Antiemetic", "Ondansetron"),
            new AcbDrugScore("scopolamine", "Scopolamine", 3, "Antimuscarinic", "Ondansetron, acupressure bands"),
            new AcbDrugScore("tolterodine", "Tolterodine", 3, "Urinary Antispasmodic", "Mirabegron or behavioral management"),
            new AcbDrugScore("trihexyphenidyl", "Trihexyphenidyl", 3, "Antiparkinsonian Anticholinergic", "Levodopa/Carbidopa optimization"),

            // --- ACB SCORE 2: Moderate Anticholinergic Burden ---
            new AcbDrugScore("amantadine", "Amantadine", 2, "Antiviral / Antiparkinson", "Levodopa optimization"),
            new AcbDrugScore("carbamazepine", "Carbamazepine", 2, "Anticonvulsant", "Levetiracetam, Lamotrigine"),
            new AcbDrugScore("cyclobenzaprine", "Cyclobenzaprine", 2, "Muscle Relaxant", "Physical therapy, gentle exercise"),
            new AcbDrugScore("loxapine", "Loxapine", 2, "First-gen Antipsychotic", "Atypical antipsychotic or behavioral support"),
            new AcbDrugScore("meperidine", "Meperidine (Demerol)", 2, "Opioid Analgesic", "Acetaminophen, low-dose Morphine/Oxycodone"),
            new AcbDrugScore("oxcarbazepine", "Oxcarbazepine", 2, "Anticonvulsant", "Levetiracetam"),

            // --- ACB SCORE 1: Mild / Potential Anticholinergic Burden ---
            new AcbDrugScore("alprazolam", "Alprazolam", 1, "Benzodiazepine", "Gradual deprescribing taper"),
            new AcbDrugScore("atenolol", "Atenolol", 1, "Beta-blocker", "Metoprolol, Bisoprolol"),
            new AcbDrugScore("captopril", "Captopril", 1, "ACE Inhibitor", "Lisinopril, Enalapril"),
            new AcbDrugScore("cetirizine", "Cetirizine", 1, "2nd Gen Antihistamine", "Fexofenadine (minimal central penetration)"),
            new AcbDrugScore("cimetidine", "Cimetidine", 1, "H2 Blocker", "Famotidine"),
            new AcbDrugScore("clorazepate", "Clorazepate", 1, "Benzodiazepine", "Gradual taper"),
            new AcbDrugScore("diazepam", "Diazepam", 1, "Benzodiazepine", "Gradual deprescribing taper"),
            new AcbDrugScore("digoxin", "Digoxin", 1, "Cardiac Glycoside", "Beta-blocker for AF rate control"),
            new AcbDrugScore("diltiazem", "Diltiazem", 1, "Calcium Channel Blocker (non-DHP)", "Amlodipine or monitor"),
            new AcbDrugScore("dipyridamole", "Dipyridamole", 1, "Antiplatelet", "Aspirin, Clopidogrel"),
            new AcbDrugScore("fentanyl", "Fentanyl", 1, "Opioid Analgesic", "Acetaminophen base, multimodal analgesia"),
            new AcbDrugScore("furosemide", "Furosemide", 1, "Loop Diuretic", "Titrate to lowest effective eGFR-guided dose"),
            new AcbDrugScore("haloperidol", "Haloperidol", 1, "Antipsychotic", "Non-pharmacological dementia care"),
            new AcbDrugScore("hydrochlorothiazide", "Hydrochlorothiazide", 1, "Thiazide Diuretic", "Chlorthalidone or monitor electrolytes"),
            new AcbDrugScore("metoprolol", "Metoprolol", 1, "Beta-blocker", "Continue if indicated; monitor heart rate"),
            new AcbDrugScore("nifedipine", "Nifedipine", 1, "Calcium Channel Blocker", "Amlodipine sustained-release"),
            new AcbDrugScore("prednisone", "Prednisone", 1, "Corticosteroid", "Taper to lowest physiologic dose"),
            new AcbDrugScore("quetiapine", "Quetiapine", 1, "Atypical Antipsychotic", "Behavioral calming, sleep hygiene"),
            new AcbDrugScore("ranitidine", "Ranitidine", 1, "H2 Blocker", "Famotidine"),
            new AcbDrugScore("risperidone", "Risperidone", 1, "Atypical Antipsychotic", "Behavioral trigger alleviation"),
            new AcbDrugScore("theophylline", "Theophylline", 1, "Methylxanthine Bronchodilator", "Inhaled bronchodilators (LAMA/LABA)"),
            new AcbDrugScore("warfarin", "Warfarin", 1, "Anticoagulant", "DOAC (Apixaban) if non-valvular AF and indicated")
    );

    private AnticholinergicBurdenCatalog() {}

    public static Optional<AcbDrugScore> findAcbScore(String medicationName) {
        if (medicationName == null || medicationName.isBlank()) return Optional.empty();
        String normalized = normalize(medicationName);
        for (var entry : ACB_CATALOG) {
            if (matches(normalized, entry.drugKey()) || matches(normalized, entry.genericName())) {
                return Optional.of(entry);
            }
        }
        return Optional.empty();
    }

    public static AcbAssessmentResult evaluateMedications(List<String> medicationNames) {
        if (medicationNames == null || medicationNames.isEmpty()) {
            return new AcbAssessmentResult(
                    0,
                    "MINIMAL",
                    "Cumulative ACB score is 0. Low anticholinergic-related cognitive or fall liability.",
                    List.of(),
                    List.of("Continue routine clinical surveillance.")
            );
        }

        List<AcbDrugScore> hits = new ArrayList<>();
        int totalScore = 0;
        for (String med : medicationNames) {
            var match = findAcbScore(med);
            if (match.isPresent()) {
                hits.add(match.get());
                totalScore += match.get().score();
            }
        }

        String tier;
        String interpretation;
        List<String> recommendations = new ArrayList<>();

        if (totalScore >= 3) {
            tier = "HIGH";
            interpretation = "Severe Anticholinergic Cognitive Burden (Score: " + totalScore + " >= 3). "
                    + "Campbell et al. & Boustani et al. demonstrate that ACB >= 3 increases delirium incidence by >50%, "
                    + "accelerates cognitive decline, and significantly increases fall and fracture risks.";
            recommendations.add("Conduct immediate interdisciplinary medication review for anticholinergic deprescribing.");
            recommendations.add("Prioritize tapering or switching medications with Score 3 (e.g., Oxybutynin, Diphenhydramine, TCAs).");
            recommendations.add("Implement enhanced fall precautions and daily hydration/delirium monitoring.");
        } else if (totalScore >= 1) {
            tier = "MODERATE";
            interpretation = "Moderate Anticholinergic Burden (Score: " + totalScore + "). "
                    + "May contribute to dry mouth, constipation, urinary hesitancy, blurred vision, and subtle cognitive slowing.";
            recommendations.add("Monitor for anticholinergic adverse effects (dry mouth, constipation, blurred vision, confusion).");
            recommendations.add("Avoid adding any further drugs with anticholinergic activity.");
        } else {
            tier = "MINIMAL";
            interpretation = "Cumulative ACB score is 0. No significant anticholinergic cognitive burden detected from listed medications.";
            recommendations.add("Maintain periodic reconciliation when new orders are initiated.");
        }

        return new AcbAssessmentResult(totalScore, tier, interpretation, hits, recommendations);
    }

    private static boolean matches(String text, String target) {
        String pat = "(?iu)(^|[^\\p{L}\\p{N}])" + Pattern.quote(normalize(target)) + "([^\\p{L}\\p{N}]|$)";
        return Pattern.compile(pat).matcher(text).find() || text.contains(normalize(target));
    }

    private static String normalize(String str) {
        return str.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }
}
