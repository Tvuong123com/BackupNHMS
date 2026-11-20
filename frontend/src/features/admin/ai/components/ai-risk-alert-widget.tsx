import { useEffect, useId, useState } from "react";
import { Activity, AlertTriangle, ChevronDown, RefreshCw, Pill, ShieldAlert, ArrowUpRight, CheckCircle2, UserCheck, Stethoscope } from "lucide-react";
import { Link } from "react-router";
import { aiService } from "../services/ai-service";
import type { ResidentReviewPriority } from "../types/ai.types";

export const AiRiskAlertWidget = ({ className = "" }: { className?: string }) => {
  const [items, setItems] = useState<ResidentReviewPriority[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [expanded, setExpanded] = useState(false);
  const contentId = useId();

  const refresh = async () => {
    setLoading(true);
    setError(false);
    try {
      const data = await aiService.getReviewPriorities();
      setItems(Array.isArray(data) ? data : []);
    } catch {
      setError(true);
      setItems([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { void refresh(); }, []);

  const safeItems = Array.isArray(items) ? items : [];
  const highRiskCount = safeItems.filter(i => i.fallRiskLevel === "HIGH" || i.priority === "HIGH").length;

  return (
    <section className={`rounded-2xl border border-slate-200 dark:border-zinc-800 bg-white dark:bg-zinc-900 p-5 shadow-xs ${className}`}>
      <div className="flex items-center justify-between gap-3">
        <div>
          <h2 className="font-semibold text-base flex items-center gap-2 text-slate-900 dark:text-zinc-100">
            <Activity className="size-4 text-rose-600 animate-pulse" />
            Resident Review & Dynamic Fall Risk Monitor (DFRS 2.0)
          </h2>
          <p className="text-xs text-slate-500 mt-1">
            Real-time multi-dimensional fall risk engine synthesizing EHR 90-day incident velocity, active FRID medication burden, and ADL mobility indicators (Khademi et al., PLOS ONE 2020).
          </p>
        </div>
        <div className="flex shrink-0 items-center gap-3">
          <button
            type="button"
            onClick={() => void refresh()}
            disabled={loading}
            className="text-xs flex items-center gap-1.5 text-blue-600 hover:text-blue-700 font-medium"
            aria-label="Refresh review priorities"
          >
            <RefreshCw className={`size-3.5 ${loading ? "animate-spin" : ""}`} /> Refresh
          </button>
          <button
            type="button"
            onClick={() => setExpanded((value) => !value)}
            aria-expanded={expanded}
            aria-controls={contentId}
            className="text-xs font-semibold px-3 py-1.5 rounded-lg border border-slate-200 dark:border-zinc-700 bg-slate-50 hover:bg-slate-100 dark:bg-zinc-800 dark:hover:bg-zinc-700 text-slate-700 dark:text-zinc-200 transition-colors flex items-center gap-1.5"
          >
            <ChevronDown className={`size-3.5 transition-transform ${expanded ? "rotate-180" : ""}`} />
            {expanded ? "Collapse Analysis" : "Expand Full Analysis"}
          </button>
        </div>
      </div>

      {loading && <p className="text-xs text-slate-500 mt-4">Evaluating real-time EHR clinical indicators and risk dimensions…</p>}
      {error && <p className="text-xs text-red-600 mt-4">Could not load risk assessment data. Please check clinical server connection.</p>}
      {!loading && !error && safeItems.length === 0 && <p className="text-xs text-slate-500 mt-4">No active resident charts found.</p>}

      {!loading && !error && !expanded && safeItems.length > 0 && (
        <div className="mt-4 p-3 rounded-xl bg-slate-50/70 dark:bg-zinc-800/40 border border-slate-200/80 dark:border-zinc-800 flex items-center justify-between text-xs">
          <span className="text-slate-600 dark:text-zinc-300 font-medium">
            <b>{safeItems.length}</b> active resident charts continuously monitored by DFRS engine.
          </span>
          <span className="font-bold text-rose-600 dark:text-rose-400 flex items-center gap-1.5">
            <AlertTriangle className="size-3.5" />
            {highRiskCount} resident(s) flagged for priority nursing intervention
          </span>
        </div>
      )}

      {/* Expanded Multi-dimensional Rich Cards */}
      <div id={contentId} hidden={!expanded}>
        {!loading && !error && safeItems.length > 0 && (
          <div className="grid gap-4 mt-5 md:grid-cols-2 xl:grid-cols-3">
            {safeItems.map((item) => {
              const isHigh = item.fallRiskLevel === "HIGH" || item.priority === "HIGH";
              const isModerate = item.fallRiskLevel === "MODERATE";

              return (
                <div
                  key={item.residentId}
                  className={`rounded-2xl border p-4.5 transition-all flex flex-col justify-between ${
                    isHigh
                      ? "border-rose-200 bg-rose-50/25 dark:border-rose-900/40 dark:bg-rose-950/10 shadow-2xs hover:shadow-xs"
                      : isModerate
                      ? "border-amber-200 bg-amber-50/20 dark:border-amber-900/30 dark:bg-amber-950/10 shadow-2xs hover:shadow-xs"
                      : "border-slate-200 bg-white dark:border-zinc-800 dark:bg-zinc-900 shadow-2xs"
                  }`}
                >
                  <div className="space-y-3.5">
                    {/* Header */}
                    <div className="flex justify-between items-start gap-2">
                      <div>
                        <Link
                          to={`/admin/residents/${item.residentId}`}
                          className="font-bold text-sm text-blue-700 dark:text-blue-400 hover:underline flex items-center gap-1"
                        >
                          {item.residentName}
                          <ArrowUpRight className="size-3" />
                        </Link>
                        <p className="text-[11px] text-slate-500 mt-0.5">
                          Chart #{item.residentId} · {item.assessmentStatus === "CONFIRMED" ? "Confirmed ADL" : "Clinical Intake"}
                        </p>
                      </div>

                      {item.dfrsScore !== undefined ? (
                        <span
                          className={`text-xs font-bold px-2.5 py-1 rounded-full border shadow-2xs shrink-0 ${
                            isHigh
                              ? "bg-rose-100 text-rose-800 border-rose-300 dark:bg-rose-950/60 dark:text-rose-300"
                              : isModerate
                              ? "bg-amber-100 text-amber-800 border-amber-300 dark:bg-amber-950/60 dark:text-amber-300"
                              : "bg-emerald-100 text-emerald-800 border-emerald-300 dark:bg-emerald-950/60 dark:text-emerald-300"
                          }`}
                        >
                          DFRS {item.dfrsScore}/100 · {item.fallRiskLevel || item.priority}
                        </span>
                      ) : (
                        <span className="text-xs font-semibold px-2 py-0.5 rounded bg-amber-100 text-amber-800">
                          Priority {item.priorityScore}/10
                        </span>
                      )}
                    </div>

                    {/* 4-Dimension Clinical Metric Scorecard */}
                    <div className="grid grid-cols-4 gap-1.5 text-center text-xs">
                      <div className="p-2 rounded-lg bg-white/90 dark:bg-zinc-800/80 border border-slate-200/70 dark:border-zinc-700/60">
                        <span className="text-[9px] uppercase font-bold text-slate-400 block">Incident</span>
                        <span className="font-bold text-rose-700 text-xs">
                          {item.incidentSubscore !== undefined ? `${item.incidentSubscore}p` : `${item.recentIncidentCount} rec`}
                        </span>
                      </div>
                      <div className="p-2 rounded-lg bg-white/90 dark:bg-zinc-800/80 border border-slate-200/70 dark:border-zinc-700/60">
                        <span className="text-[9px] uppercase font-bold text-slate-400 block">FRID Meds</span>
                        <span className="font-bold text-amber-700 text-xs">
                          {item.medicationSubscore !== undefined ? `${item.medicationSubscore}p` : "eMAR"}
                        </span>
                      </div>
                      <div className="p-2 rounded-lg bg-white/90 dark:bg-zinc-800/80 border border-slate-200/70 dark:border-zinc-700/60">
                        <span className="text-[9px] uppercase font-bold text-slate-400 block">ADL Mobility</span>
                        <span className="font-bold text-blue-700 text-xs">
                          {item.adlSubscore !== undefined ? `${item.adlSubscore}p` : `${item.currentAdlScore ?? "N/A"}`}
                        </span>
                      </div>
                      <div className="p-2 rounded-lg bg-white/90 dark:bg-zinc-800/80 border border-slate-200/70 dark:border-zinc-700/60">
                        <span className="text-[9px] uppercase font-bold text-slate-400 block">Vitals</span>
                        <span className="font-bold text-purple-700 text-xs">
                          {item.vitalSubscore !== undefined ? `${item.vitalSubscore}p` : "Stable"}
                        </span>
                      </div>
                    </div>

                    {/* Active FRID Medication Badges (If any) */}
                    {item.activeFridNames && item.activeFridNames.length > 0 && (
                      <div className="space-y-1">
                        <span className="text-[10px] font-bold uppercase tracking-wider text-slate-500 flex items-center gap-1">
                          <Pill className="size-3 text-rose-500" /> Detected FRID Drugs:
                        </span>
                        <div className="flex flex-wrap gap-1">
                          {item.activeFridNames.map((drug, i) => (
                            <span key={i} className="px-2 py-0.5 rounded text-[10px] font-bold bg-rose-100 text-rose-800 dark:bg-rose-950/60 dark:text-rose-300">
                              {drug}
                            </span>
                          ))}
                        </div>
                      </div>
                    )}

                    {/* Contributing Clinical Factors */}
                    <div className="space-y-1">
                      <span className="text-[10px] font-bold uppercase tracking-wider text-slate-500 block">
                        Clinical Risk Factors:
                      </span>
                      <ul className="space-y-1">
                        {item.factors.slice(0, 3).map((factor, idx) => (
                          <li key={idx} className="text-xs flex gap-1.5 text-slate-700 dark:text-zinc-300">
                            <span className="text-rose-500 font-bold shrink-0">•</span>
                            <span className="line-clamp-2">{factor}</span>
                          </li>
                        ))}
                      </ul>
                    </div>

                    {/* Actionable Fall Interventions */}
                    {item.fallInterventions && item.fallInterventions.length > 0 && (
                      <div className="pt-2 border-t border-slate-200/60 dark:border-zinc-800 space-y-1">
                        <span className="text-[10px] font-bold uppercase tracking-wider text-slate-500 block">
                          Proactive Interventions:
                        </span>
                        <div className="space-y-1">
                          {item.fallInterventions.slice(0, 2).map((action, i) => (
                            <div
                              key={i}
                              className="text-[11px] p-1.5 px-2 rounded-lg bg-blue-50/80 dark:bg-zinc-800/80 text-blue-900 dark:text-blue-200 font-medium"
                            >
                              🛡️ {action}
                            </div>
                          ))}
                        </div>
                      </div>
                    )}
                  </div>

                  {/* Actions & Data Lineage Footer */}
                  <div className="mt-4 pt-3 border-t border-slate-200/60 dark:border-zinc-800 flex items-center justify-between">
                    <Link
                      to={`/admin/residents/${item.residentId}`}
                      className="text-xs font-semibold text-blue-600 dark:text-blue-400 hover:underline flex items-center gap-1"
                    >
                      View Chart & DFRS Review →
                    </Link>

                    <span className="text-[10px] text-slate-400 font-mono">
                      {item.methodVersion}
                    </span>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>
    </section>
  );
};
