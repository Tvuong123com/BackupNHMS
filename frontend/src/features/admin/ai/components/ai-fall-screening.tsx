import { useEffect, useState } from "react";
import { aiService } from "../services/ai-service";
import type {
  FallScreeningRequest,
  FallScreeningResult,
  DynamicFallRiskResult,
  FallRiskHistoryResponse,
  FallIntervention,
  ResidentChartContext,
} from "../types/ai.types";
import { ChevronDown, Pill, ShieldAlert, CheckCircle2, Camera, UserCheck, Stethoscope, Dumbbell, Activity } from "lucide-react";

const items = [
  { key: "historyOfFalling", label: "History of falling", options: [[0, "No"], [25, "Yes"]] },
  { key: "secondaryDiagnosis", label: "Two or more diagnoses", options: [[0, "No"], [15, "Yes"]] },
  { key: "ambulatoryAid", label: "Ambulatory aid", options: [[0, "None, bedrest or nurse assist"], [15, "Crutches, cane or walker"], [30, "Furniture"]] },
  { key: "ivTherapy", label: "IV therapy or heparin lock", options: [[0, "No"], [20, "Yes"]] },
  { key: "gait", label: "Gait", options: [[0, "Normal, bedrest or wheelchair"], [10, "Weak"], [20, "Impaired"]] },
  { key: "mentalStatus", label: "Mental status", options: [[0, "Oriented to own ability"], [15, "Overestimates or forgets limitations"]] },
] as const;

type ScoreKey = typeof items[number]["key"];
const emptyScores: Record<ScoreKey, string> = {
  historyOfFalling: "", secondaryDiagnosis: "", ambulatoryAid: "",
  ivTherapy: "", gait: "", mentalStatus: "",
};

export const AiFallScreening = ({ residentId }: { residentId: number }) => {
  const [scores, setScores] = useState<Record<ScoreKey, string>>(emptyScores);
  const [latest, setLatest] = useState<FallScreeningResult | null>(null);
  const [dynamicRisk, setDynamicRisk] = useState<DynamicFallRiskResult | null>(null);
  const [history, setHistory] = useState<FallRiskHistoryResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [savingSnapshot, setSavingSnapshot] = useState(false);
  const [applyingIndex, setApplyingIndex] = useState<number | null>(null);
  const [appliedMap, setAppliedMap] = useState<Record<number, { taskId: number; time: string }>>({});
  const [showMorseAssessment, setShowMorseAssessment] = useState(false);
  const [chartContext, setChartContext] = useState<ResidentChartContext | null>(null);
  const [error, setError] = useState("");
  const [successMsg, setSuccessMsg] = useState("");

  const loadData = (active: boolean) => {
    setLoading(true);
    Promise.all([
      aiService.getLatestFallScreening(residentId).catch(() => null),
      aiService.getDynamicFallRisk(residentId).catch(() => null),
      aiService.getFallRiskHistory(residentId, 10).catch(() => null),
      aiService.getResidentChartContext(residentId).catch(() => null),
    ]).then(([screeningRes, dynamicRes, historyRes, contextRes]) => {
      if (active) {
        setLatest(screeningRes);
        setDynamicRisk(dynamicRes);
        setHistory(historyRes);
        setChartContext(contextRes);
      }
    }).finally(() => {
      if (active) setLoading(false);
    });
  };

  useEffect(() => {
    let active = true;
    loadData(active);
    return () => { active = false; };
  }, [residentId]);

  const complete = items.every(item => scores[item.key] !== "");

  const save = async () => {
    if (!complete) return;
    setSaving(true);
    setError("");
    setSuccessMsg("");
    try {
      const request = { residentId, ...Object.fromEntries(items.map(item => [item.key, Number(scores[item.key])])) } as FallScreeningRequest;
      const result = await aiService.recordFallScreening(request);
      setLatest(result);
      setScores({ ...emptyScores });
      const [refreshedRisk, refreshedHistory] = await Promise.all([
        aiService.getDynamicFallRisk(residentId).catch(() => null),
        aiService.getFallRiskHistory(residentId, 10).catch(() => null),
      ]);
      if (refreshedRisk) setDynamicRisk(refreshedRisk);
      if (refreshedHistory) setHistory(refreshedHistory);
      setSuccessMsg("Manual Morse screening recorded and dynamic DFRS updated.");
      setShowMorseAssessment(false);
    } catch {
      setError("Could not record the screening. Verify user role permissions.");
    } finally {
      setSaving(false);
    }
  };

  const handleTakeSnapshot = async () => {
    setSavingSnapshot(true);
    setError("");
    setSuccessMsg("");
    try {
      await aiService.recordFallSnapshot(residentId);
      const refreshedHistory = await aiService.getFallRiskHistory(residentId, 10).catch(() => null);
      if (refreshedHistory) setHistory(refreshedHistory);
      setSuccessMsg("Fall risk snapshot saved to longitudinal chart.");
    } catch {
      setError("Could not save snapshot. Check clinical chart connection.");
    } finally {
      setSavingSnapshot(false);
    }
  };

  const handleApplyIntervention = async (item: FallIntervention, index: number) => {
    setApplyingIndex(index);
    setError("");
    setSuccessMsg("");
    try {
      const res = await aiService.applyFallIntervention({
        residentId,
        action: item.action,
        role: item.role,
        priority: item.priority,
        rationale: item.rationale,
      });

      if (res.success) {
        setAppliedMap(prev => ({
          ...prev,
          [index]: { taskId: res.careTaskId, time: new Date().toLocaleTimeString() }
        }));
        setSuccessMsg(`✓ Shift task created for ${item.role}: "${item.action}" (Task #${res.careTaskId})`);
      }
    } catch {
      setError("Could not create shift task. Verify nurse authorization.");
    } finally {
      setApplyingIndex(null);
    }
  };

  const roleBadgeStyles: Record<string, string> = {
    CNA: "bg-blue-100 text-blue-800 dark:bg-blue-950 dark:text-blue-300",
    NURSE: "bg-emerald-100 text-emerald-800 dark:bg-emerald-950 dark:text-emerald-300",
    PT: "bg-amber-100 text-amber-800 dark:bg-amber-950 dark:text-amber-300",
    DOCTOR: "bg-rose-100 text-rose-800 dark:bg-rose-950 dark:text-rose-300",
  };

  return (
    <section className="space-y-4 lg:col-span-2">
      {/* Title & Engine Header */}
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h3 className="font-semibold text-base flex items-center gap-2.5">
            <span>F4 · Dynamic Fall Risk Engine (DFRS 2.0)</span>
            {dynamicRisk && (
              <span className={`text-xs px-2.5 py-0.5 rounded-full font-bold border ${
                dynamicRisk.riskLevel === "HIGH" ? "bg-rose-100 text-rose-800 border-rose-300 dark:bg-rose-950/40" :
                dynamicRisk.riskLevel === "MODERATE" ? "bg-amber-100 text-amber-800 border-amber-300 dark:bg-amber-950/40" :
                "bg-emerald-100 text-emerald-800 border-emerald-300 dark:bg-emerald-950/40"
              }`}>
                DFRS {dynamicRisk.dfrsScore}/100 · {dynamicRisk.riskLevel} RISK
              </span>
            )}
          </h3>
          <p className="text-xs text-muted-foreground mt-0.5">
            Multi-dimensional real-time fall risk score integrating 90-day incident velocity, active FRID medications, and ADL mobility.
          </p>
        </div>

        <div className="flex items-center gap-2">
          {history && (
            <span className={`text-xs px-2.5 py-1 rounded-lg font-bold border flex items-center gap-1.5 ${
              history.trendDirection === "IMPROVING" ? "bg-emerald-50 text-emerald-700 border-emerald-200" :
              history.trendDirection === "WORSENING" ? "bg-rose-50 text-rose-700 border-rose-200" :
              "bg-gray-50 text-gray-700 border-gray-200"
            }`}>
              {history.trendDirection === "IMPROVING" && "📉 Trend: Improving"}
              {history.trendDirection === "WORSENING" && "📈 Trend: Increasing Risk"}
              {history.trendDirection === "STABLE" && "⚖️ Trend: Stable"}
            </span>
          )}

          <button
            type="button"
            onClick={handleTakeSnapshot}
            disabled={savingSnapshot}
            className="text-xs font-semibold px-3 py-1.5 rounded-lg border border-gray-300 bg-white hover:bg-gray-50 text-gray-700 dark:bg-zinc-800 dark:border-zinc-700 dark:text-zinc-200 disabled:opacity-50 transition-colors flex items-center gap-1.5"
          >
            <Camera className="size-3.5" />
            {savingSnapshot ? "Saving..." : "Record Snapshot"}
          </button>
        </div>
      </div>

      {successMsg && (
        <div className="p-3 text-xs bg-emerald-50 border border-emerald-200 text-emerald-800 rounded-xl flex items-center justify-between">
          <span>{successMsg}</span>
          <button type="button" onClick={() => setSuccessMsg("")} className="font-bold text-emerald-900 ml-2">×</button>
        </div>
      )}

      {error && (
        <div className="p-3 text-xs bg-rose-50 border border-rose-200 text-rose-800 rounded-xl flex items-center justify-between">
          <span>{error}</span>
          <button type="button" onClick={() => setError("")} className="font-bold text-rose-900 ml-2">×</button>
        </div>
      )}

      {/* Real-time Dynamic Risk & Multi-dimensional Breakdown */}
      {dynamicRisk && (
        <div className={`p-4 rounded-2xl border text-sm space-y-4 ${
          dynamicRisk.riskLevel === "HIGH" ? "bg-rose-50/40 border-rose-200 dark:bg-rose-950/20" :
          dynamicRisk.riskLevel === "MODERATE" ? "bg-amber-50/30 border-amber-200 dark:bg-amber-950/20" :
          "bg-emerald-50/20 border-emerald-200 dark:bg-emerald-950/20"
        }`}>
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-2 text-center">
            <div className="bg-white/90 dark:bg-zinc-800/90 p-2.5 rounded-xl border border-gray-200/80 shadow-2xs">
              <span className="text-[10px] uppercase font-bold text-gray-500 block">Incident History (90d)</span>
              <span className="text-sm font-bold text-rose-700">{dynamicRisk.incidentSubscore}/35 pts</span>
              <span className="text-[10px] text-gray-400 block mt-0.5">{dynamicRisk.recentFallsCount} fall(s) in 30d</span>
            </div>
            <div className="bg-white/90 dark:bg-zinc-800/90 p-2.5 rounded-xl border border-gray-200/80 shadow-2xs">
              <span className="text-[10px] uppercase font-bold text-gray-500 block">FRID Medications</span>
              <span className="text-sm font-bold text-amber-700">{dynamicRisk.medicationSubscore}/30 pts</span>
              <span className="text-[10px] text-gray-400 block mt-0.5">{dynamicRisk.activeFridCount} high-risk drugs</span>
            </div>
            <div className="bg-white/90 dark:bg-zinc-800/90 p-2.5 rounded-xl border border-gray-200/80 shadow-2xs">
              <span className="text-[10px] uppercase font-bold text-gray-500 block">ADL Mobility & Gait</span>
              <span className="text-sm font-bold text-blue-700">{dynamicRisk.adlSubscore}/25 pts</span>
              <span className="text-[10px] text-gray-400 block mt-0.5">Transfer independence</span>
            </div>
            <div className="bg-white/90 dark:bg-zinc-800/90 p-2.5 rounded-xl border border-gray-200/80 shadow-2xs">
              <span className="text-[10px] uppercase font-bold text-gray-500 block">Vitals / Orthostasis</span>
              <span className="text-sm font-bold text-purple-700">{dynamicRisk.vitalSubscore}/10 pts</span>
              <span className="text-[10px] text-gray-400 block mt-0.5">Postural pressure stability</span>
            </div>
          </div>

          {/* Active Chart Prescriptions & FRID Screening Section */}
          <div className="bg-white/90 dark:bg-zinc-900/90 p-4 rounded-xl border border-gray-200/80 shadow-2xs space-y-3">
            <div className="flex items-center justify-between border-b border-gray-100 dark:border-zinc-800 pb-2">
              <div>
                <h4 className="text-xs font-bold uppercase tracking-wider text-slate-800 dark:text-zinc-200 flex items-center gap-1.5">
                  <Pill className="size-4 text-rose-600" />
                  Active Chart Prescriptions & Fall-Risk Increasing Drugs (FRIDs)
                </h4>
                <p className="text-[11px] text-slate-500 mt-0.5">
                  Live orders retrieved from resident's eMAR chart with continuous FRID cross-matching
                </p>
              </div>
              <span className="text-xs font-bold px-2.5 py-1 rounded-full bg-slate-100 text-slate-700 dark:bg-zinc-800 dark:text-zinc-300">
                {chartContext?.activeMedications?.length || dynamicRisk.activeFridMedications?.length || 0} Order(s)
              </span>
            </div>

            {chartContext?.activeMedications && chartContext.activeMedications.length > 0 ? (
              <div className="grid gap-2.5 sm:grid-cols-2">
                {chartContext.activeMedications.map((med) => (
                  <div
                    key={med.id}
                    className={`p-3 rounded-xl border text-xs space-y-1.5 transition-all ${
                      med.isFrid
                        ? "border-rose-200 bg-rose-50/50 dark:bg-rose-950/20 dark:border-rose-900/50"
                        : "border-slate-200 bg-slate-50/60 dark:bg-zinc-800/40 dark:border-zinc-700"
                    }`}
                  >
                    <div className="flex items-center justify-between">
                      <span className="font-bold text-sm text-slate-900 dark:text-zinc-100 flex items-center gap-1.5">
                        <Pill className={`size-3.5 ${med.isFrid ? "text-rose-600" : "text-blue-500"}`} />
                        {med.medicationName}
                      </span>
                      {med.isFrid ? (
                        <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-rose-600 text-white">
                          FRID · Risk +{dynamicRisk.activeFridMedications?.find(f => f.medicationName.toLowerCase() === med.medicationName.toLowerCase())?.riskWeight || 15}
                        </span>
                      ) : (
                        <span className="text-[10px] font-medium px-2 py-0.5 rounded-full bg-emerald-100 text-emerald-800 dark:bg-emerald-950 dark:text-emerald-300">
                          Standard Care
                        </span>
                      )}
                    </div>
                    <div className="flex items-center gap-2 text-[11px] text-slate-600 dark:text-zinc-400">
                      <span>Dosage: <strong className="text-slate-800 dark:text-zinc-200">{med.dosage || "As directed"}</strong></span>
                      <span>·</span>
                      <span>Route: <strong>{med.route || "Oral"}</strong></span>
                      <span>·</span>
                      <span>Freq: <strong>{med.frequency || "Daily"}</strong></span>
                    </div>
                    {med.isFrid ? (
                      <p className="text-[11px] text-rose-700 dark:text-rose-400 font-medium">
                        ⚠️ Category: {med.fridCategory || "High Fall Risk"} — {med.clinicalWarning || "Increases sedation, ataxia or orthostasis."}
                      </p>
                    ) : (
                      <p className="text-[11px] text-slate-500 italic">No direct fall-risk warning flagged by Beers/CDC criteria.</p>
                    )}
                  </div>
                ))}
              </div>
            ) : dynamicRisk.activeFridMedications && dynamicRisk.activeFridMedications.length > 0 ? (
              <div className="grid gap-2 sm:grid-cols-2">
                {dynamicRisk.activeFridMedications.map((frid, idx) => (
                  <div key={idx} className="p-3 rounded-lg border border-rose-200 bg-rose-50/50 text-xs space-y-1">
                    <div className="flex items-center justify-between">
                      <span className="font-bold text-slate-900">{frid.medicationName}</span>
                      <span className="text-[10px] font-bold px-1.5 py-0.5 rounded bg-rose-600 text-white">
                        Risk +{frid.riskWeight}
                      </span>
                    </div>
                    <p className="text-[11px] text-slate-600">Category: {frid.category}</p>
                    <p className="text-[11px] text-rose-700 italic">{frid.clinicalWarning}</p>
                  </div>
                ))}
              </div>
            ) : (
              <div className="p-3 rounded-lg bg-slate-50 dark:bg-zinc-800 text-xs text-slate-500">
                No active medication orders recorded in this chart yet.
              </div>
            )}
          </div>

          {/* Contributing Factors */}
          <div>
            <p className="text-xs font-semibold text-gray-700 dark:text-zinc-300 mb-1">Contributing Clinical Factors:</p>
            <ul className="text-xs space-y-1">
              {dynamicRisk.contributingFactors.map((f, i) => (
                <li key={i} className="flex items-start gap-1.5 text-gray-700 dark:text-zinc-200">
                  <span className="text-rose-500 font-bold shrink-0">•</span>
                  <span>{f}</span>
                </li>
              ))}
            </ul>
          </div>

          {/* Recommended Interventions (Human-like phrasing) */}
          {dynamicRisk.recommendedInterventions.length > 0 && (
            <div className="pt-3 border-t border-gray-200/60 dark:border-zinc-800 space-y-2">
              <div className="flex items-center justify-between">
                <p className="text-xs font-bold uppercase tracking-wider text-slate-800 dark:text-zinc-200">
                  Recommended Interventions (Assign to Shift)
                </p>
                <span className="text-[11px] text-gray-500">Click to assign actionable task to on-duty staff</span>
              </div>
              <div className="space-y-2">
                {dynamicRisk.recommendedInterventions.map((item, idx) => (
                  <div key={idx} className="bg-white/95 dark:bg-zinc-800/95 p-3 rounded-xl border border-slate-200 dark:border-zinc-700 text-xs flex flex-col sm:flex-row sm:items-center justify-between gap-2 shadow-2xs">
                    <div className="space-y-0.5">
                      <div className="flex items-center gap-2">
                        <span className={`px-2 py-0.5 rounded-md font-bold text-[10px] ${roleBadgeStyles[item.role] || "bg-gray-100 text-gray-800"}`}>
                          {item.role}
                        </span>
                        <span className={`px-1.5 py-0.2 rounded text-[10px] font-semibold ${
                          item.priority === "IMMEDIATE" ? "bg-rose-100 text-rose-700" : "bg-gray-100 text-gray-600"
                        }`}>
                          {item.priority}
                        </span>
                        <span className="font-semibold text-gray-900 dark:text-zinc-100">{item.action}</span>
                      </div>
                      {item.rationale && <p className="text-gray-500 text-[11px] italic pl-1">{item.rationale}</p>}
                    </div>

                    <button
                      type="button"
                      disabled={applyingIndex === idx || !!appliedMap[idx]}
                      onClick={() => void handleApplyIntervention(item, idx)}
                      className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition-all shrink-0 ${
                        appliedMap[idx]
                          ? "bg-emerald-100 text-emerald-800 border border-emerald-300 dark:bg-emerald-950/50 dark:text-emerald-300"
                          : "bg-blue-600 hover:bg-blue-700 text-white shadow-xs"
                      }`}
                    >
                      {applyingIndex === idx ? (
                        "Assigning..."
                      ) : appliedMap[idx] ? (
                        `✓ Assigned (#${appliedMap[idx].taskId})`
                      ) : (
                        "+ Assign to Shift"
                      )}
                    </button>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      )}

      {/* Snapshot History & Trend Section */}
      {history && history.snapshots.length > 0 && (
        <div className="p-3.5 rounded-xl border border-gray-200 bg-white dark:bg-zinc-900 dark:border-zinc-800">
          <div className="flex items-center justify-between mb-2">
            <h4 className="text-xs font-bold uppercase tracking-wider text-gray-700 dark:text-zinc-300">
              Longitudinal Fall Risk Snapshots & History
            </h4>
            <span className="text-[11px] text-gray-400">
              {history.snapshots.length} recent evaluations
            </span>
          </div>

          <div className="divide-y divide-gray-100 dark:divide-zinc-800 text-xs">
            {history.snapshots.slice(0, 5).map((snap) => (
              <div key={snap.id} className="py-2 flex items-center justify-between gap-3">
                <div className="flex items-center gap-2.5">
                  <span className={`w-2 h-2 rounded-full ${
                    snap.riskLevel === "HIGH" ? "bg-rose-500" :
                    snap.riskLevel === "MODERATE" ? "bg-amber-500" : "bg-emerald-500"
                  }`} />
                  <span className="font-semibold text-gray-800 dark:text-zinc-200">
                    DFRS {snap.dfrsScore}/100 ({snap.riskLevel})
                  </span>
                  <span className="text-gray-400 text-[11px]">
                    Incidents: {snap.incidentSubscore}p · FRIDs: {snap.medicationSubscore}p · ADL: {snap.adlSubscore}p
                  </span>
                </div>
                <div className="text-right text-[11px] text-gray-500">
                  <span>{new Date(snap.assessedAt).toLocaleString()}</span>
                  <span className="block text-[10px] text-gray-400">by {snap.assessedBy || "SYSTEM"}</span>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Collapsible Staff Manual Morse Assessment (Solves clutter) */}
      <div className="rounded-xl border border-gray-200 dark:border-zinc-800 bg-white dark:bg-zinc-900 overflow-hidden">
        <button
          type="button"
          onClick={() => setShowMorseAssessment(prev => !prev)}
          className="w-full p-3.5 flex items-center justify-between text-left hover:bg-gray-50 dark:hover:bg-zinc-800/50 transition-colors"
        >
          <div className="flex items-center gap-2.5">
            <ChevronDown className={`size-4 text-gray-400 transition-transform ${showMorseAssessment ? "rotate-180" : ""}`} />
            <div>
              <span className="text-xs font-bold uppercase tracking-wider text-gray-700 dark:text-zinc-200 block">
                Optional: Staff Manual Morse Scale (Baseline Assessment)
              </span>
              <span className="text-[11px] text-gray-400">
                Traditional 6-item nursing checklist. DFRS 2.0 already calculates risk automatically from EHR data.
              </span>
            </div>
          </div>
          {latest ? (
            <span className="text-xs px-2.5 py-1 rounded-md bg-slate-100 dark:bg-zinc-800 text-slate-700 dark:text-zinc-300 font-semibold shrink-0">
              Score: {latest.totalScore}/125
            </span>
          ) : (
            <span className="text-[11px] text-gray-400 shrink-0">Not recorded</span>
          )}
        </button>

        {showMorseAssessment && (
          <div className="p-4 pt-1 border-t border-gray-100 dark:border-zinc-800 space-y-3">
            {latest && (
              <p className="text-xs text-gray-500">
                Latest manual assessment: <b>{latest.totalScore} pts</b> on {new Date(latest.assessedAt).toLocaleString()} by {latest.assessedBy}
              </p>
            )}
            <div className="grid gap-3 md:grid-cols-2 xl:grid-cols-3">
              {items.map(item => (
                <label key={item.key} className="block text-xs font-medium text-gray-700 dark:text-zinc-300">
                  {item.label}
                  <select
                    className="block w-full border border-gray-300 dark:border-zinc-700 rounded-lg p-2 mt-1 bg-background text-xs"
                    value={scores[item.key]}
                    onChange={e => setScores(previous => ({ ...previous, [item.key]: e.target.value }))}
                  >
                    <option value="">Select observed finding</option>
                    {item.options.map(([score, label]) => (
                      <option key={score} value={score}>{label} ({score})</option>
                    ))}
                  </select>
                </label>
              ))}
            </div>
            <button
              type="button"
              className="mt-2 rounded-lg bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 text-xs font-semibold disabled:opacity-50 transition-colors"
              disabled={!complete || saving}
              onClick={() => void save()}
            >
              {saving ? "Saving…" : "Save Manual Morse Assessment"}
            </button>
          </div>
        )}
      </div>
    </section>
  );
};
