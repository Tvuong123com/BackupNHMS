package com.eldercare.modules.ai.medication.catalog;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Knowledge catalog based on the American Geriatrics Society (AGS) 2023 Updated Beers Criteria®
 * for Potentially Inappropriate Medication (PIM) Use in Older Adults.
 * Reference: 2023 AGS Beers Criteria® Update Expert Panel. J Am Geriatr Soc. 2023;71(7):2052-2081.
 */
public final class BeersCriteriaCatalog {

    public record BeersPimEntry(
            String drugKey,
            String genericName,
            String category,
            String severity, // HIGH, MODERATE, CAUTION
            String rationale,
            String recommendation,
            String saferAlternative,
            String evidenceLevel // Strong, Moderate
    ) {}

    private static final List<BeersPimEntry> CATALOG = List.of(
            // Anticholinergics / First-generation Antihistamines
            new BeersPimEntry(
                    "diphenhydramine",
                    "Diphenhydramine (Benadryl)",
                    "Anticholinergic / 1st Gen Antihistamine",
                    "HIGH",
                    "Highly anticholinergic; causes acute confusion, sedation, urinary retention, and increased fall risk.",
                    "Avoid as hypnotic or allergy treatment in older adults.",
                    "Second-generation antihistamines (Cetirizine, Loratadine) or non-pharmacological sleep hygiene",
                    "Strong"
            ),
            new BeersPimEntry(
                    "chlorpheniramine",
                    "Chlorpheniramine",
                    "Anticholinergic / 1st Gen Antihistamine",
                    "HIGH",
                    "Strong anticholinergic properties; high clearance impairment in elderly.",
                    "Avoid use for allergy or sleep.",
                    "Loratadine, Fexofenadine, or saline nasal spray",
                    "Strong"
            ),
            new BeersPimEntry(
                    "hydroxyzine",
                    "Hydroxyzine",
                    "Anticholinergic / 1st Gen Antihistamine",
                    "HIGH",
                    "Potent central anticholinergic and sedative effects; risk of confusion and falls.",
                    "Avoid use for anxiety or pruritus in older adults.",
                    "Buspirone, SSRIs for anxiety; topical emollients or topical corticosteroids for pruritus",
                    "Strong"
            ),
            new BeersPimEntry(
                    "promethazine",
                    "Promethazine (Phenergan)",
                    "Anticholinergic / Phenothiazine",
                    "HIGH",
                    "Pronounced anticholinergic and extrapyramidal symptoms; dangerous sedation and delirium risk.",
                    "Avoid use for nausea or sedation.",
                    "Ondansetron (oral) or non-pharmacological measures",
                    "Strong"
            ),

            // Antispasmodics / Urinary Incontinence
            new BeersPimEntry(
                    "oxybutynin",
                    "Oxybutynin",
                    "Urinary Antispasmodic",
                    "MODERATE",
                    "High central anticholinergic burden; significant worsening of cognitive decline and dry mouth.",
                    "Avoid oral immediate-release formulation, especially in residents with cognitive impairment or dementia.",
                    "Mirabegron (beta-3 agonist) or pelvic floor exercises / scheduled toileting",
                    "Moderate"
            ),

            // Tricyclic Antidepressants (TCAs)
            new BeersPimEntry(
                    "amitriptyline",
                    "Amitriptyline",
                    "Tricyclic Antidepressant",
                    "HIGH",
                    "Highly anticholinergic, sedating; causes orthostatic hypotension, cardiac conduction delays, and falls.",
                    "Avoid for depression, insomnia, or neuropathic pain in older adults.",
                    "SSRIs (Sertraline, Escitalopram), SNRIs (Duloxetine for neuropathic pain), or Gabapentinoids (low dose)",
                    "Strong"
            ),
            new BeersPimEntry(
                    "imipramine",
                    "Imipramine",
                    "Tricyclic Antidepressant",
                    "HIGH",
                    "Substantial anticholinergic and cardiotoxic potential.",
                    "Avoid use in geriatric population.",
                    "Sertraline, Escitalopram, or Mirtazapine (for depression with insomnia)",
                    "Strong"
            ),
            new BeersPimEntry(
                    "nortriptyline",
                    "Nortriptyline",
                    "Tricyclic Antidepressant",
                    "MODERATE",
                    "Moderately anticholinergic; requires close monitoring if selected for refractory neuropathic pain.",
                    "Avoid as first-line agent. Keep at lowest effective dose if unavoidable.",
                    "Duloxetine, Gabapentin (with renal dose adjustment), or Topical Lidocaine",
                    "Moderate"
            ),

            // Benzodiazepines & Z-Drugs (Sedatives)
            new BeersPimEntry(
                    "diazepam",
                    "Diazepam (Valium)",
                    "Long-acting Benzodiazepine",
                    "HIGH",
                    "Long half-life active metabolites; high risk of prolonged sedation, ataxia, falls, fractures, and delirium.",
                    "Avoid for insomnia, agitation, or anxiety.",
                    "Gradual deprescribing taper; non-drug anxiety management, SSRIs/Buspirone",
                    "Strong"
            ),
            new BeersPimEntry(
                    "clonazepam",
                    "Clonazepam (Klonopin)",
                    "Long-acting Benzodiazepine",
                    "HIGH",
                    "Marked accumulation, cognitive blunting, severe motor impairment and fall liability.",
                    "Avoid chronic use for insomnia or anxiety.",
                    "Structured tapering protocol; Cognitive Behavioral Therapy for Insomnia (CBT-I)",
                    "Strong"
            ),
            new BeersPimEntry(
                    "alprazolam",
                    "Alprazolam (Xanax)",
                    "Short-acting Benzodiazepine",
                    "HIGH",
                    "Rapid tolerance, high abuse potential, rebound anxiety, cognitive worsening, and fall risk.",
                    "Avoid in older adults. If prescribed chronically, plan slow tapering.",
                    "SSRI, SNRI, or non-pharmacological behavioral therapy",
                    "Strong"
            ),
            new BeersPimEntry(
                    "lorazepam",
                    "Lorazepam (Ativan)",
                    "Intermediate Benzodiazepine",
                    "MODERATE",
                    "Increased sensitivity in older adults; causes sedation, delirium, and impaired gait.",
                    "Avoid for insomnia or routine agitation. Limit to acute emergency seizure/palliative situations.",
                    "Low-dose Melatonin, CBT-I, or environmental calming strategies",
                    "Moderate"
            ),
            new BeersPimEntry(
                    "zolpidem",
                    "Zolpidem (Ambien)",
                    "Non-benzodiazepine Hypnotic (Z-drug)",
                    "HIGH",
                    "Adverse event profile comparable to benzodiazepines: nocturnal delirium, sleepwalking, falls, and fractures.",
                    "Avoid for sleep maintenance or initiation.",
                    "Sleep hygiene protocol, Melatonin 1-3mg, or low-dose Trazodone if necessary",
                    "Strong"
            ),
            new BeersPimEntry(
                    "eszopiclone",
                    "Eszopiclone (Lunesta)",
                    "Non-benzodiazepine Hypnotic (Z-drug)",
                    "HIGH",
                    "Fall and delirium risk with minimal clinically meaningful improvement in sleep latency.",
                    "Avoid for chronic insomnia.",
                    "Sleep restriction therapy, stimulus control, sleep hygiene",
                    "Strong"
            ),

            // Antipsychotics in Dementia
            new BeersPimEntry(
                    "haloperidol",
                    "Haloperidol (Haldol)",
                    "First-generation Antipsychotic",
                    "HIGH",
                    "FDA Black Box Warning: Increased mortality and cerebrovascular events (stroke) in elderly with dementia; high extrapyramidal symptoms.",
                    "Avoid for behavioral and psychological symptoms of dementia (BPSD) unless non-pharmacological interventions fail and severe harm is imminent.",
                    "DICE approach (Describe, Investigate, Create, Evaluate behavioral triggers); music therapy, sensory stimulation",
                    "Strong"
            ),
            new BeersPimEntry(
                    "olanzapine",
                    "Olanzapine (Zyprexa)",
                    "Second-generation Antipsychotic",
                    "HIGH",
                    "Anticholinergic activity, metabolic syndrome, stroke risk, and increased mortality in dementia.",
                    "Avoid as routine management of agitation or wandering. Attempt dose reduction after 3 months.",
                    "Environmental adjustments, pain assessment, meaningful engagement activities",
                    "Strong"
            ),
            new BeersPimEntry(
                    "quetiapine",
                    "Quetiapine (Seroquel)",
                    "Second-generation Antipsychotic",
                    "MODERATE",
                    "Frequently overused off-label for insomnia; causes orthostatic hypotension, falls, and metabolic changes.",
                    "Avoid for simple insomnia. Use lowest effective dose if treating refractory psychosis.",
                    "Melatonin, sleep routine optimization, or Trazodone low-dose",
                    "Moderate"
            ),
            new BeersPimEntry(
                    "risperidone",
                    "Risperidone (Risperdal)",
                    "Second-generation Antipsychotic",
                    "HIGH",
                    "Stroke risk, Parkinsonism, orthostatic hypotension, and increased mortality in elderly dementia patients.",
                    "Reserve exclusively for acute psychosis with imminent risk of physical harm. Re-evaluate regularly for deprescribing.",
                    "Structured behavioral support, pain alleviation, non-pharmacological soothing",
                    "Strong"
            ),

            // Cardiovascular Medications
            new BeersPimEntry(
                    "digoxin",
                    "Digoxin (Lanoxin)",
                    "Cardiac Glycoside",
                    "MODERATE",
                    "Narrow therapeutic window; decreased renal clearance in aging leads to digitalis toxicity, arrhythmias, and anorexia.",
                    "Avoid as first-line for atrial fibrillation or heart failure. If used, do not exceed 0.125 mg/day.",
                    "Beta-blockers (e.g., Metoprolol succinate, Bisoprolol) or CCBs for rate control",
                    "Strong"
            ),
            new BeersPimEntry(
                    "clonidine",
                    "Clonidine (Catapres)",
                    "Central Alpha-2 Agonist",
                    "HIGH",
                    "High rate of central adverse effects: sedation, orthostatic hypotension, bradycardia, and rebound hypertension.",
                    "Avoid as routine antihypertensive treatment.",
                    "ACE inhibitors (Lisinopril), ARBs (Losartan), or Dihydropyridine CCBs (Amlodipine)",
                    "Strong"
            ),
            new BeersPimEntry(
                    "amiodarone",
                    "Amiodarone (Cordarone)",
                    "Antiarrhythmic Class III",
                    "HIGH",
                    "Substantial multi-organ toxicity: pulmonary fibrosis, thyroid dysfunction, hepatotoxicity, and QT prolongation.",
                    "Avoid as first-line therapy for atrial fibrillation unless heart failure with reduced ejection fraction.",
                    "Rate control strategies (Beta-blockers) or anticoagulation",
                    "Strong"
            ),
            new BeersPimEntry(
                    "nifedipine",
                    "Nifedipine (Immediate-release)",
                    "Calcium Channel Blocker (DHP)",
                    "HIGH",
                    "Potential for precipitous hypotension and reflex tachycardia; myocardial ischemia risk.",
                    "Avoid immediate-release formulation.",
                    "Amlodipine or sustained-release formulations",
                    "Strong"
            ),

            // Endocrine / Diabetes
            new BeersPimEntry(
                    "glibenclamide",
                    "Glibenclamide / Glyburide",
                    "Long-acting Sulfonylurea",
                    "HIGH",
                    "Prolonged half-life in elderly with impaired renal clearance; high incidence of severe and fatal hypoglycemia.",
                    "Avoid in older adults; replace with safer glucose-lowering agents.",
                    "Metformin (if eGFR permits), DPP-4 inhibitors (Linagliptin, Sitagliptin), or SGLT2 inhibitors",
                    "Strong"
            ),
            new BeersPimEntry(
                    "glimepiride",
                    "Glimepiride (Amaryl)",
                    "Long-acting Sulfonylurea",
                    "HIGH",
                    "Prolonged hypoglycemia risk, especially with fluctuating nutritional intake in nursing home residents.",
                    "Avoid or replace with short-acting or non-hypoglycemic antidiabetic classes.",
                    "DPP-4 inhibitors (Linagliptin does not require renal adjustment) or Glipizide (with caution)",
                    "Strong"
            ),

            // Gastrointestinal
            new BeersPimEntry(
                    "omeprazole",
                    "Omeprazole (Prilosec) - Chronic >8 wks",
                    "Proton Pump Inhibitor (PPI)",
                    "CAUTION",
                    "Chronic use > 8 weeks without valid indication is associated with Clostridioides difficile infection, hip fractures, and hypomagnesemia.",
                    "Avoid scheduled use beyond 8 weeks unless Barrett esophagus, erosive esophagitis, or chronic NSAID use.",
                    "Attempt taper to PRN, H2 receptor antagonist (Famotidine), or lifestyle modifications",
                    "Strong"
            ),
            new BeersPimEntry(
                    "pantoprazole",
                    "Pantoprazole (Protonix) - Chronic >8 wks",
                    "Proton Pump Inhibitor (PPI)",
                    "CAUTION",
                    "Overprescribed without periodic re-evaluation; bone loss and enteric infection risk.",
                    "Review indication every 3-6 months. Deprescribe if symptom-free.",
                    "Dose step-down or Famotidine",
                    "Moderate"
            ),
            new BeersPimEntry(
                    "metoclopramide",
                    "Metoclopramide (Reglan)",
                    "Prokinetic / Antiemetic",
                    "HIGH",
                    "Can cause irreversible tardive dyskinesia and extrapyramidal side effects.",
                    "Avoid unless for gastroparesis; duration should not exceed 12 weeks.",
                    "Domperidone (where available) or non-pharmacological dietary spacing",
                    "Strong"
            ),

            // Pain & Musculoskeletal (NSAIDs & Muscle Relaxants)
            new BeersPimEntry(
                    "indomethacin",
                    "Indomethacin",
                    "Non-selective NSAID",
                    "HIGH",
                    "Highest rate of central nervous system adverse effects (headache, confusion) among all NSAIDs, plus severe GI ulceration.",
                    "Avoid in older adults.",
                    "Acetaminophen (up to 2-3g/day), Topical NSAIDs (Voltaren gel), or non-pharmacological physical therapy",
                    "Strong"
            ),
            new BeersPimEntry(
                    "ketorolac",
                    "Ketorolac (Toradol)",
                    "Non-selective NSAID",
                    "HIGH",
                    "Marked risk of gastrointestinal bleeding and acute renal failure even with short courses.",
                    "Avoid oral or parenteral use in older adults.",
                    "Acetaminophen, topical analgesics, low-dose short-acting opioids if severe acute pain",
                    "Strong"
            ),
            new BeersPimEntry(
                    "ibuprofen",
                    "Ibuprofen (Chronic / High-dose)",
                    "Non-selective NSAID",
                    "MODERATE",
                    "Risk of GI bleeding, ulceration, hypertension exacerbation, acute kidney injury, and fluid retention in heart failure.",
                    "Avoid chronic oral use without gastroprotection (PPI or Misoprostol). Prefer topical formulations.",
                    "Topical Diclofenac gel, Acetaminophen, heat/cold therapy",
                    "Moderate"
            ),
            new BeersPimEntry(
                    "naproxen",
                    "Naproxen (Aleve)",
                    "Non-selective NSAID",
                    "MODERATE",
                    "GI hemorrhage and renal toxicity risk; fluid retention.",
                    "Avoid scheduled chronic daily use. Keep to lowest dose and shortest duration with PPI co-prescription.",
                    "Topical NSAIDs, Acetaminophen, physical rehabilitation",
                    "Moderate"
            ),
            new BeersPimEntry(
                    "meloxicam",
                    "Meloxicam (Mobic)",
                    "Preferential COX-2 NSAID",
                    "MODERATE",
                    "GI bleeding and cardiovascular risk in prolonged therapy.",
                    "Avoid long-term daily use in frail residents.",
                    "Topical NSAID gel, physical modalities",
                    "Moderate"
            ),
            new BeersPimEntry(
                    "cyclobenzaprine",
                    "Cyclobenzaprine (Flexeril)",
                    "Skeletal Muscle Relaxant",
                    "HIGH",
                    "Poorly tolerated in elderly due to strong anticholinergic effects, sedation, and high fracture risk.",
                    "Avoid all skeletal muscle relaxants in older adults.",
                    "Gentle stretching, physical therapy, heat pads, topical analgesics",
                    "Strong"
            ),
            new BeersPimEntry(
                    "methocarbamol",
                    "Methocarbamol (Robaxin)",
                    "Skeletal Muscle Relaxant",
                    "HIGH",
                    "Sedation, confusion, and fall risk without proven superiority over non-drug modalities.",
                    "Avoid in geriatric care.",
                    "Physical therapy, gentle exercise, topical lidocaine patch",
                    "Strong"
            )
    );

    private BeersCriteriaCatalog() {}

    public static List<BeersPimEntry> getAllEntries() {
        return CATALOG;
    }

    public static Optional<BeersPimEntry> findPim(String medicationName) {
        if (medicationName == null || medicationName.isBlank()) return Optional.empty();
        String normalized = normalize(medicationName);
        for (var entry : CATALOG) {
            if (matches(normalized, entry.drugKey()) || matches(normalized, entry.genericName())) {
                return Optional.of(entry);
            }
        }
        return Optional.empty();
    }

    public static List<BeersPimEntry> findPimsInList(List<String> medicationNames) {
        if (medicationNames == null || medicationNames.isEmpty()) return List.of();
        return medicationNames.stream()
                .map(BeersCriteriaCatalog::findPim)
                .flatMap(Optional::stream)
                .distinct()
                .toList();
    }

    private static boolean matches(String text, String target) {
        String pat = "(?iu)(^|[^\\p{L}\\p{N}])" + Pattern.quote(normalize(target)) + "([^\\p{L}\\p{N}]|$)";
        return Pattern.compile(pat).matcher(text).find() || text.contains(normalize(target));
    }

    private static String normalize(String str) {
        return str.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }
}
