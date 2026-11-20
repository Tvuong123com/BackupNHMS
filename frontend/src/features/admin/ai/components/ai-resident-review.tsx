import { useState, useEffect } from "react";
import { aiService } from "../services/ai-service";
import type { ElopementReviewResponse, MedicationReviewResponse, ResidentChartContext } from "../types/ai.types";
import { AiFallScreening } from "./ai-fall-screening";
import { Pill, ShieldAlert, Sparkles, UserCheck, AlertTriangle, CheckCircle2, RefreshCw, FileText } from "lucide-react";

const dailyMedSearch = (name: string) =>
  `https://dailymed.nlm.nih.gov/dailymed/search.cfm?query=${encodeURIComponent(name)}`;

export const AiResidentReview = ({ residentId }: { residentId: number }) => {
  const [behaviors, setBehaviors] = useState("");
  const [cognitiveScore, setCognitiveScore] = useState("");
  const [elopement, setElopement] = useState<ElopementReviewResponse | null>(null);
  const [medication, setMedication] = useState("");
  const [currentMeds, setCurrentMeds] = useState("");
  const [medReview, setMedReview] = useState<MedicationReviewResponse | null>(null);
  const [chartContext, setChartContext] = useState<ResidentChartContext | null>(null);
  const [loadingChart, setLoadingChart] = useState(true);
  const [busy, setBusy] = useState<"elopement" | "medication" | null>(null);
  const [error, setError] = useState("");
  const [activeModule, setActiveModule] = useState<"all" | "fall" | "polypharmacy" | "wandering">("all");

  const loadChartContext = async (active: boolean) => {
    setLoadingChart(true);
    try {
      const data = await aiService.getResidentChartContext(residentId);
      if (active) {
        setChartContext(data);
        if (data.cognitiveScore !== undefined && data.cognitiveScore !== null) {
          setCognitiveScore(String(data.cognitiveScore));
        }
        if (data.documentedBehaviors && data.documentedBehaviors.length > 0) {
          setBehaviors(data.documentedBehaviors.join("\n"));
        }
        if (data.activeMedications && data.activeMedications.length > 0) {
          const medNames = data.activeMedications.map(m => m.medicationName);
          setCurrentMeds(medNames.join(", "));
          if (!medication && medNames.length > 0) {
            setMedication(medNames[0]);
          }
        }
      }
    } catch {
      // Fallback
    } finally {
      if (active) setLoadingChart(false);
    }
  };

  useEffect(() => {
    let active = true;
    loadChartContext(active);
    return () => { active = false; };
  }, [residentId]);

  const runElopement = async () => {
    setBusy("elopement");
    setError("");
    setElopement(null);
    try {
      setElopement(
        await aiService.reviewElopement({
          residentId,
          cognitiveScore: cognitiveScore ? Number(cognitiveScore) : undefined,
          recentBehaviors: behaviors.split("\n").map((x) => x.trim()).filter(Boolean),
        })
      );
    } catch {
      setError("Could not review wandering observations. Check entered facts and connection.");
    } finally {
      setBusy(null);
    }
  };

  const runMedicationReview = async (targetMed?: string) => {
    const medToTest = (targetMed || medication).trim();
    if (!medToTest) return;
    setBusy("medication");
    setError("");
    setMedReview(null);
    try {
      setMedReview(
        await aiService.reviewMedication({
          residentId,
          newMedication: medToTest,
          currentMedications: currentMeds.split(/[,\n]/).map((x) => x.trim()).filter(Boolean),
        })
      );
    } catch {
      setError("Could not prepare medication safety review. Verify the chart connection.");
    } finally {
      setBusy(null);
    }
  };

  const getRiskBadgeColor = (riskLevel?: string) => {
    switch (riskLevel) {
      case "CONTRAINDICATED":
        return "bg-rose-700 text-white";
      case "HIGH_RISK_PIM":
        return "bg-red-600 text-white";
      case "REVIEW_REQUIRED":
        return "bg-amber-600 text-white";
      case "CAUTION_MONITOR":
        return "bg-yellow-500 text-black";
      case "MONITORED_SAFE":
        return "bg-emerald-600 text-white";
      default:
        return "bg-gray-600 text-white";
    }
  };

  return (
    <div className="space-y-6">
      {/* Sub-Navigation Tabs for Quick Discovery of AI Modules */}
      <div className="flex flex-wrap items-center gap-2 p-1.5 bg-slate-100 dark:bg-zinc-800/80 rounded-2xl border border-slate-200 dark:border-zinc-700">
        <button
          type="button"
          onClick={() => setActiveModule("all")}
          className={`px-4 py-2 rounded-xl text-xs font-bold transition-all ${
            activeModule === "all"
              ? "bg-white dark:bg-zinc-900 text-blue-600 dark:text-blue-400 shadow-xs"
              : "text-slate-600 dark:text-zinc-400 hover:text-slate-900"
          }`}
        >
          All Modules Overview
        </button>
        <button
          type="button"
          onClick={() => setActiveModule("fall")}
          className={`px-4 py-2 rounded-xl text-xs font-bold transition-all ${
            activeModule === "fall"
              ? "bg-white dark:bg-zinc-900 text-blue-600 dark:text-blue-400 shadow-xs"
              : "text-slate-600 dark:text-zinc-400 hover:text-slate-900"
          }`}
        >
          2.1 Fall Risk Engine (DFRS 2.0)
        </button>
        <button
          type="button"
          onClick={() => setActiveModule("polypharmacy")}
          className={`px-4 py-2 rounded-xl text-xs font-bold transition-all flex items-center gap-1.5 ${
            activeModule === "polypharmacy"
              ? "bg-white dark:bg-zinc-900 text-emerald-600 dark:text-emerald-400 shadow-xs"
              : "text-slate-600 dark:text-zinc-400 hover:text-slate-900"
          }`}
        >
          <Pill className="size-3.5" />
          2.2 Polypharmacy CDSS (F9)
        </button>
        <button
          type="button"
          onClick={() => setActiveModule("wandering")}
          className={`px-4 py-2 rounded-xl text-xs font-bold transition-all flex items-center gap-1.5 ${
            activeModule === "wandering"
              ? "bg-white dark:bg-zinc-900 text-amber-600 dark:text-amber-400 shadow-xs"
              : "text-slate-600 dark:text-zinc-400 hover:text-slate-900"
          }`}
        >
          <ShieldAlert className="size-3.5" />
          2.3 Wandering & Elopement (F8)
        </button>
      </div>

      <div className="grid gap-6 lg:grid-cols-2">
      {/* 2.1 Dynamic Fall Risk Engine */}
      {(activeModule === "all" || activeModule === "fall") && (
        <AiFallScreening residentId={residentId} />
      )}

      {/* 2.3 Wandering & Elopement Observation Review (F8) */}
      {(activeModule === "all" || activeModule === "wandering") && (
      <section className={`space-y-4 bg-white dark:bg-zinc-900 p-5 rounded-2xl border border-gray-200 dark:border-zinc-800 shadow-2xs ${
        activeModule === "wandering" ? "lg:col-span-2 ring-2 ring-amber-500/20" : ""
      }`}>
        <div className="flex items-center justify-between border-b border-gray-100 dark:border-zinc-800 pb-3">
          <div>
            <h3 className="font-semibold text-base text-gray-900 dark:text-zinc-100 flex items-center gap-2">
              <ShieldAlert className="size-4 text-amber-600" />
              F8 · Wandering & Elopement Observation Review
            </h3>
            <p className="text-xs text-muted-foreground mt-0.5">
              Assesses behavioral agitation, exit-seeking risk, and cognitive impairment against CMS F689 guidelines.
            </p>
          </div>
          <span className="text-xs px-2.5 py-0.5 rounded-full font-bold bg-amber-50 text-amber-700 border border-amber-200 dark:bg-amber-950/40">
            Memory Care Protocol
          </span>
        </div>

        {/* Resident Chart Behavioral Log (Solves missing 30-day logs issue) */}
        <div className="p-3.5 rounded-xl border border-amber-200/80 bg-amber-50/30 dark:bg-zinc-800/50 space-y-2 text-xs">
          <div className="flex items-center justify-between">
            <span className="font-bold text-slate-800 dark:text-zinc-200 flex items-center gap-1.5">
              <FileText className="size-3.5 text-amber-600" />
              30-Day Agitation & Elopement Incidents from Chart
            </span>
            <span className="text-[11px] font-semibold text-amber-800 dark:text-amber-300">
              {chartContext?.recentIncidents?.filter(i => ["ELOPEMENT", "BEHAVIORAL", "ALTERCATION"].includes(i.incidentType))?.length || 0} Incident(s)
            </span>
          </div>

          {loadingChart ? (
            <p className="text-slate-400">Loading chart behavioral history…</p>
          ) : chartContext?.recentIncidents && chartContext.recentIncidents.filter(i => ["ELOPEMENT", "BEHAVIORAL", "ALTERCATION"].includes(i.incidentType)).length > 0 ? (
            <div className="space-y-1.5">
              {chartContext.recentIncidents.filter(i => ["ELOPEMENT", "BEHAVIORAL", "ALTERCATION"].includes(i.incidentType)).map((inc, i) => (
                <div key={i} className="p-2 rounded bg-white dark:bg-zinc-900 border border-amber-100 dark:border-zinc-700 flex items-start gap-2">
                  <span className="px-1.5 py-0.5 rounded text-[10px] font-bold bg-amber-100 text-amber-800 shrink-0">
                    {inc.incidentType}
                  </span>
                  <div className="space-y-0.5">
                    <p className="font-medium text-slate-800 dark:text-zinc-200">{inc.description}</p>
                    <p className="text-[10px] text-slate-400">{inc.occurredAt ? new Date(inc.occurredAt).toLocaleString() : "Recent"} · Location: {inc.location || "Resident Room"}</p>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <p className="text-emerald-700 dark:text-emerald-400 flex items-center gap-1.5">
              <CheckCircle2 className="size-3.5" /> No wandering or agitation incidents recorded in chart for the last 30 days.
            </p>
          )}
        </div>

        <div className="space-y-1.5">
          <div className="flex items-center justify-between">
            <label className="block text-xs font-semibold text-gray-700 dark:text-zinc-300">
              Documented behaviors (auto-populated from EHR or enter observed findings):
            </label>
            <div className="flex items-center gap-1.5">
              <span className="text-[10px] text-slate-400">Quick tests:</span>
              <button
                type="button"
                onClick={() => setBehaviors(b => (b ? b + "\n" : "") + "Pacing corridors near exit doors during evening hours (Sundowning)")}
                className="text-[10px] px-2 py-0.5 rounded bg-amber-100 hover:bg-amber-200 text-amber-800 font-medium"
              >
                + Pacing near exits
              </button>
              <button
                type="button"
                onClick={() => setBehaviors(b => (b ? b + "\n" : "") + "Disoriented, repeatedly packing bags attempting to leave facility")}
                className="text-[10px] px-2 py-0.5 rounded bg-amber-100 hover:bg-amber-200 text-amber-800 font-medium"
              >
                + Packing to leave
              </button>
            </div>
          </div>
          <textarea
            className="block w-full border border-gray-300 dark:border-zinc-700 rounded-xl p-2.5 mt-1 text-xs focus:ring-2 focus:ring-blue-500 bg-background"
            rows={3}
            placeholder="e.g., Pacing corridors near exit doors between 17:00-19:00&#10;Attempting to open alarmed courtyard exit door&#10;Agitated and asking for deceased family members"
            value={behaviors}
            onChange={(e) => setBehaviors(e.target.value)}
          />
        </div>

        <label className="block text-xs font-semibold text-gray-700 dark:text-zinc-300">
          Documented cognitive score (MMSE / MoCA out of 30, if available in chart):
          <input
            className="block w-full border border-gray-300 dark:border-zinc-700 rounded-xl p-2 mt-1 text-xs focus:ring-2 focus:ring-blue-500 bg-background"
            type="number"
            min="0"
            max="30"
            placeholder="e.g., 18"
            value={cognitiveScore}
            onChange={(e) => setCognitiveScore(e.target.value)}
          />
        </label>

        <button
          type="button"
          className="rounded-xl bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 text-xs font-semibold transition-colors disabled:opacity-50"
          disabled={busy !== null || (!behaviors.trim() && !cognitiveScore)}
          onClick={() => void runElopement()}
        >
          {busy === "elopement" ? "Evaluating wandering observations..." : "Review Wandering & Elopement Risk"}
        </button>

        {elopement && (
          <div className="border border-amber-200 bg-amber-50/50 dark:bg-zinc-800/80 rounded-xl p-4 text-xs space-y-2">
            <div className="flex items-center justify-between">
              <span className="font-bold text-gray-900 dark:text-zinc-100">AI Risk Assessment:</span>
              <span className={`px-2.5 py-0.5 rounded text-xs font-bold ${
                elopement.riskLevel === "HIGH" ? "bg-rose-600 text-white" :
                elopement.riskLevel === "MODERATE" ? "bg-amber-600 text-white" : "bg-emerald-600 text-white"
              }`}>
                {elopement.riskLevel} ELOPEMENT RISK
              </span>
            </div>
            <p className="text-gray-700 dark:text-zinc-300">{elopement.behavioralAnalysis}</p>
            <div>
              <p className="font-semibold text-gray-800 dark:text-zinc-200 mb-1">Immediate Safeguards:</p>
              <ul className="list-disc pl-5 space-y-0.5 text-gray-700 dark:text-zinc-300">
                {(elopement.immediateSafeguards ?? []).map((item, i) => (
                  <li key={i}>{item}</li>
                ))}
              </ul>
            </div>
            <p className="text-amber-800 dark:text-amber-300 text-[11px] font-medium pt-1 border-t border-amber-200">
              * Clinician verification required before updating resident elopement watch protocol.
            </p>
          </div>
        )}
      </section>
      )}

      {/* 2.2 Geriatric Polypharmacy & Medication Safety CDSS (F9) */}
      {(activeModule === "all" || activeModule === "polypharmacy") && (
      <section id="polypharmacy-cdss" className={`space-y-4 bg-white dark:bg-zinc-900 p-5 rounded-2xl border border-gray-200 dark:border-zinc-800 shadow-2xs lg:col-span-2 ${
        activeModule === "polypharmacy" ? "ring-2 ring-emerald-500/30" : ""
      }`}>
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-2 border-b border-gray-100 dark:border-zinc-800 pb-3">
          <div>
            <h3 className="font-semibold text-base text-gray-900 dark:text-zinc-100 flex items-center gap-2">
              <Pill className="size-4 text-emerald-600" />
              F9 · Geriatric Polypharmacy & Medication Safety CDSS
            </h3>
            <p className="text-xs text-muted-foreground mt-0.5">
              Evidence-based clinical decision support integrating <b>AGS Beers Criteria® 2023</b>, <b>STOPP/START v3</b>, and the <b>Anticholinergic Cognitive Burden (ACB) Scale</b>.
            </p>
          </div>
          <span className="inline-flex items-center px-2.5 py-1 rounded text-xs font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200 dark:bg-emerald-950/40">
            Geriatric Pharmacology CDSS
          </span>
        </div>

        {/* Active Chart Medications Table (Answers user request: "không thấy bất kì đơn thuốc được phân cho người bệnh nào hiển thị") */}
        <div className="p-4 rounded-xl border border-slate-200 dark:border-zinc-800 bg-slate-50/50 dark:bg-zinc-800/40 space-y-3">
          <div className="flex flex-wrap items-center justify-between gap-2">
            <div>
              <h4 className="text-xs font-bold uppercase tracking-wider text-slate-800 dark:text-zinc-200 flex items-center gap-1.5">
                <Pill className="size-3.5 text-blue-600" />
                Active Prescribed Medications in Chart ({chartContext?.activeMedications?.length || 0})
              </h4>
              <p className="text-[11px] text-slate-500">Live medication orders retrieved from eMAR chart</p>
            </div>

            {chartContext?.activeMedications && chartContext.activeMedications.length > 0 && (
              <button
                type="button"
                onClick={() => void runMedicationReview(chartContext.activeMedications[0].medicationName)}
                disabled={busy !== null}
                className="px-3 py-1.5 rounded-lg text-xs font-semibold bg-emerald-600 hover:bg-emerald-700 text-white shadow-2xs transition-colors flex items-center gap-1.5"
              >
                <Sparkles className="size-3.5" />
                Run CDSS Check on Active Regimen
              </button>
            )}
          </div>

          {loadingChart ? (
            <p className="text-xs text-slate-400 py-3">Loading active medications from eMAR…</p>
          ) : chartContext?.activeMedications && chartContext.activeMedications.length > 0 ? (
            <div className="grid gap-2 sm:grid-cols-2 md:grid-cols-3">
              {chartContext.activeMedications.map((med) => (
                <div
                  key={med.id}
                  onClick={() => {
                    setMedication(med.medicationName);
                    void runMedicationReview(med.medicationName);
                  }}
                  className={`p-3 rounded-xl border text-xs cursor-pointer transition-all hover:shadow-xs ${
                    med.isFrid
                      ? "bg-rose-50/40 border-rose-200 dark:bg-rose-950/20 dark:border-rose-900"
                      : "bg-white dark:bg-zinc-900 border-slate-200 dark:border-zinc-700 hover:border-blue-300"
                  }`}
                >
                  <div className="flex items-start justify-between gap-1">
                    <span className="font-bold text-slate-900 dark:text-zinc-100">{med.medicationName}</span>
                    {med.isFrid && (
                      <span className="text-[10px] font-bold px-1.5 py-0.2 rounded bg-rose-100 text-rose-700 shrink-0">
                        FRID
                      </span>
                    )}
                  </div>
                  <p className="text-[11px] text-slate-500 mt-0.5">
                    {med.dosage || "Standard dose"} · {med.route || "Oral"} · {med.frequency || "Daily"}
                  </p>
                  {med.clinicalWarning ? (
                    <p className="text-[10px] text-rose-700 dark:text-rose-400 italic mt-1">{med.clinicalWarning}</p>
                  ) : (
                    <p className="text-[10px] text-blue-600 dark:text-blue-400 mt-1 hover:underline">Click to run Beers check →</p>
                  )}
                </div>
              ))}
            </div>
          ) : (
            <div className="p-4 rounded-lg bg-white dark:bg-zinc-900 border border-slate-200 dark:border-zinc-700 text-xs text-slate-500 flex items-center justify-between">
              <span>No active medication orders recorded in this chart yet.</span>
            </div>
          )}
        </div>

        {/* Inputs for proposed medication check */}
        <div className="grid gap-4 sm:grid-cols-2">
          <label className="block text-xs font-semibold text-gray-700 dark:text-zinc-300">
            Medication to review (Proposed order or chart drug)
            <input
              className="block w-full border border-gray-300 dark:border-zinc-700 rounded-xl p-2.5 mt-1 text-xs focus:ring-2 focus:ring-blue-500 bg-background"
              placeholder="e.g., Diphenhydramine, Zolpidem, Haloperidol, Furosemide..."
              value={medication}
              onChange={(e) => setMedication(e.target.value)}
            />
          </label>
          <label className="block text-xs font-semibold text-gray-700 dark:text-zinc-300">
            Current co-prescribed medications (Comma-separated)
            <textarea
              className="block w-full border border-gray-300 dark:border-zinc-700 rounded-xl p-2 mt-1 text-xs focus:ring-2 focus:ring-blue-500 bg-background"
              rows={2}
              placeholder="e.g., Metoprolol, Donepezil, Sertraline..."
              value={currentMeds}
              onChange={(e) => setCurrentMeds(e.target.value)}
            />
          </label>
        </div>

        <button
          type="button"
          className="rounded-xl bg-blue-600 hover:bg-blue-700 text-white px-4 py-2.5 text-xs font-semibold transition-colors disabled:opacity-50"
          disabled={busy !== null || !medication.trim()}
          onClick={() => void runMedicationReview()}
        >
          {busy === "medication" ? "Evaluating Pharmacology & CDSS Rules..." : "Run Geriatric Safety & Beers Check"}
        </button>

        {medReview && (
          <div className="border border-gray-200 dark:border-zinc-700 rounded-2xl p-5 text-xs space-y-4 bg-gray-50/50 dark:bg-zinc-800/60">
            {/* Header Badge */}
            <div className="flex flex-wrap items-center justify-between gap-2 border-b border-gray-200 dark:border-zinc-700 pb-3">
              <div className="flex items-center gap-2">
                <span className={`px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wider ${getRiskBadgeColor(medReview.riskLevel)}`}>
                  {medReview.riskLevel}
                </span>
                <span className="text-xs text-gray-500 dark:text-zinc-400">
                  Administration clearance: <b>{medReview.isSafeToAdminister ? "Conditioned Safe" : "HOLD / Review Required"}</b>
                </span>
              </div>
              <span className="text-xs text-gray-400 font-mono">
                {medReview.interactionReferenceStatus}
              </span>
            </div>

            <div className={`p-3.5 rounded-xl border text-xs ${
              medReview.riskLevel === "CONTRAINDICATED"
                ? "bg-rose-50 border-rose-200 text-rose-900 dark:bg-rose-950/40 dark:text-rose-200"
                : medReview.riskLevel === "HIGH_RISK_PIM"
                ? "bg-red-50 border-red-200 text-red-900 dark:bg-red-950/40 dark:text-red-200"
                : medReview.riskLevel === "REVIEW_REQUIRED"
                ? "bg-amber-50 border-amber-200 text-amber-900 dark:bg-amber-950/40 dark:text-amber-200"
                : "bg-emerald-50 border-emerald-200 text-emerald-900 dark:bg-emerald-950/40 dark:text-emerald-200"
            }`}>
              <b>Clinical Summary: </b>{medReview.summaryWarning}
            </div>

            {/* Anticholinergic Cognitive Burden (ACB) Section */}
            {medReview.totalAcbScore !== undefined && (
              <div className="bg-white dark:bg-zinc-900 rounded-xl border border-gray-200 dark:border-zinc-700 p-4 shadow-2xs space-y-2">
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-2">
                    <span className="font-semibold text-gray-900 dark:text-zinc-100 text-xs">Anticholinergic Cognitive Burden (ACB)</span>
                    <span className="text-[11px] text-gray-400">(Boustani et al., Campbell et al.)</span>
                  </div>
                  <span className={`px-2.5 py-0.5 rounded-full text-xs font-bold ${
                    medReview.totalAcbScore >= 3
                      ? "bg-red-100 text-red-800 border border-red-300"
                      : medReview.totalAcbScore >= 1
                      ? "bg-amber-100 text-amber-800 border border-amber-300"
                      : "bg-emerald-100 text-emerald-800 border border-emerald-300"
                  }`}>
                    ACB Score: {medReview.totalAcbScore} {medReview.totalAcbScore >= 3 ? "(High Delirium Risk)" : "(Low Risk)"}
                  </span>
                </div>
                {medReview.acbClinicalImpact && (
                  <p className="text-[11px] text-gray-600 dark:text-zinc-300">{medReview.acbClinicalImpact}</p>
                )}
              </div>
            )}

            {/* Clinical Precautions */}
            {medReview.clinicalPrecautions && medReview.clinicalPrecautions.length > 0 && (
              <div className="bg-white dark:bg-zinc-900 rounded-xl border border-gray-200 dark:border-zinc-700 p-4 shadow-2xs space-y-2">
                <span className="font-semibold text-gray-900 dark:text-zinc-100 text-xs">Clinical Precautions:</span>
                <ul className="list-disc pl-5 space-y-1 text-gray-700 dark:text-zinc-300">
                  {medReview.clinicalPrecautions.map((precaution, i) => (
                    <li key={i}>{precaution}</li>
                  ))}
                </ul>
              </div>
            )}

            {/* Potential Interactions */}
            {medReview.interactions && medReview.interactions.length > 0 && (
              <div className="bg-white dark:bg-zinc-900 rounded-xl border border-gray-200 dark:border-zinc-700 p-4 shadow-2xs space-y-2">
                <span className="font-semibold text-gray-900 dark:text-zinc-100 text-xs">Identified Interactions:</span>
                <div className="space-y-2">
                  {medReview.interactions.map((inter, i) => (
                    <div key={i} className="p-2.5 rounded-lg border border-amber-200/80 bg-amber-50/40 dark:bg-zinc-800 text-xs space-y-0.5">
                      <div className="flex items-center justify-between">
                        <span className="font-bold text-slate-900 dark:text-zinc-100">{inter.interactingSubstance}</span>
                        <span className="px-1.5 py-0.2 rounded text-[10px] font-bold bg-amber-100 text-amber-800">{inter.severity}</span>
                      </div>
                      <p className="text-slate-600 dark:text-zinc-300">{inter.mechanism}</p>
                      <p className="text-blue-700 dark:text-blue-400 font-medium">{inter.recommendation}</p>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        )}
      </section>
      )}
    </div>
    </div>
  );
};
