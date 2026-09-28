import React, { useState } from "react";
import { Sparkles, Loader2, CheckCircle2, AlertTriangle, Info } from "lucide-react";
import { aiService } from "../services/ai-service";
import type { IncidentAnalysisResponse } from "../types/ai.types";

interface AiAnalyzeIncidentButtonProps {
  description: string;
  residentInfo?: string;
  location?: string;
  onApplySuggestion?: (suggestion: IncidentAnalysisResponse) => void;
  className?: string;
}

export const AiAnalyzeIncidentButton: React.FC<AiAnalyzeIncidentButtonProps> = ({
  description,
  residentInfo,
  location,
  onApplySuggestion,
  className = "",
}) => {
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<IncidentAnalysisResponse | null>(null);
  const [showModal, setShowModal] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleAnalyze = async () => {
    if (!description.trim()) {
      setError("Please enter an incident narrative before requesting clinical analysis.");
      setShowModal(true);
      return;
    }

    setLoading(true);
    setError(null);
    setShowModal(true);

    try {
      const data = await aiService.classifyIncident({
        description,
        residentInfo,
        location,
      });
      setResult(data);
    } catch {
      setError("Unable to connect to incident assessment service. Please verify local Ollama service.");
    } finally {
      setLoading(false);
    }
  };

  const getSeverityBadgeClass = (severity: string) => {
    switch (severity?.toUpperCase()) {
      case "EMERGENCY":
        return "bg-red-500/15 text-red-700 dark:text-red-400 border-red-500/30";
      case "CRITICAL":
        return "bg-rose-500/15 text-rose-700 dark:text-rose-400 border-rose-500/30";
      case "HIGH":
        return "bg-orange-500/15 text-orange-700 dark:text-orange-400 border-orange-500/30";
      case "MEDIUM":
        return "bg-amber-500/15 text-amber-700 dark:text-amber-400 border-amber-500/30";
      default:
        return "bg-emerald-500/15 text-emerald-700 dark:text-emerald-400 border-emerald-500/30";
    }
  };

  return (
    <>
      <button
        type="button"
        onClick={handleAnalyze}
        disabled={loading}
        className={`inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-semibold bg-indigo-600 hover:bg-indigo-700 text-white shadow-xs transition-all duration-150 disabled:opacity-50 ${className}`}
      >
        {loading ? (
          <Loader2 className="w-3.5 h-3.5 animate-spin" />
        ) : (
          <Sparkles className="w-3.5 h-3.5 text-indigo-200" />
        )}
        <span>Clinical AI Assessment</span>
      </button>

      {/* Suggestion Modal */}
      {showModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-xs animate-in fade-in duration-150">
          <div className="bg-white dark:bg-zinc-900 border border-zinc-200 dark:border-zinc-800 rounded-2xl max-w-md w-full p-5 shadow-2xl space-y-4">
            <div className="flex items-center justify-between border-b border-zinc-100 dark:border-zinc-800 pb-3">
              <div className="flex items-center space-x-2">
                <div className="p-1.5 rounded-lg bg-indigo-50 dark:bg-indigo-950/60 text-indigo-600 dark:text-indigo-400">
                  <Sparkles className="w-4 h-4 text-indigo-500" />
                </div>
                <h4 className="text-sm font-semibold text-zinc-900 dark:text-zinc-100">
                  Clinical Incident Severity Assessment
                </h4>
              </div>
              <button
                onClick={() => setShowModal(false)}
                className="text-zinc-400 hover:text-zinc-600 dark:hover:text-zinc-200 text-sm font-bold"
              >
                ✕
              </button>
            </div>

            {loading && (
              <div className="py-8 flex flex-col items-center justify-center space-y-2 text-zinc-500">
                <Loader2 className="w-7 h-7 animate-spin text-indigo-600" />
                <p className="text-xs">Evaluating clinical narrative and classifying severity tier...</p>
              </div>
            )}

            {error && (
              <div className="p-3 bg-red-50 dark:bg-red-950/30 border border-red-200 dark:border-red-900/50 rounded-xl text-red-700 dark:text-red-400 text-xs flex items-start gap-2">
                <AlertTriangle className="w-4 h-4 shrink-0 mt-0.5" />
                <span>{error}</span>
              </div>
            )}

            {result && !loading && (
              <div className="space-y-3.5 text-xs">
                <div className="grid grid-cols-2 gap-2">
                  <div className="p-2.5 rounded-xl bg-zinc-50 dark:bg-zinc-800/50 border border-zinc-200 dark:border-zinc-700/60">
                    <span className="text-[10px] text-zinc-500 uppercase tracking-wider block font-semibold">
                      Suggested Severity
                    </span>
                    <span
                      className={`inline-block mt-1 px-2.5 py-0.5 rounded-md text-xs font-bold border ${getSeverityBadgeClass(
                        result.suggestedSeverity
                      )}`}
                    >
                      {result.suggestedSeverity}
                    </span>
                  </div>

                  <div className="p-2.5 rounded-xl bg-zinc-50 dark:bg-zinc-800/50 border border-zinc-200 dark:border-zinc-700/60">
                    <span className="text-[10px] text-zinc-500 uppercase tracking-wider block font-semibold">
                      Incident Type
                    </span>
                    <span className="inline-block mt-1 px-2.5 py-0.5 rounded-md text-xs font-semibold bg-zinc-100 dark:bg-zinc-700 text-zinc-800 dark:text-zinc-200">
                      {result.suggestedIncidentType}
                    </span>
                  </div>
                </div>

                <div className="p-2.5 rounded-xl bg-indigo-50/50 dark:bg-indigo-950/20 border border-indigo-100 dark:border-indigo-900/40">
                  <span className="text-[10px] text-indigo-700 dark:text-indigo-300 font-semibold uppercase tracking-wider flex items-center gap-1">
                    <Info className="w-3 h-3" /> Clinical Rationale
                  </span>
                  <p className="mt-1 text-zinc-700 dark:text-zinc-300 leading-relaxed">
                    {result.rationale}
                  </p>
                </div>

                {result.recommendedActions && result.recommendedActions.length > 0 && (
                  <div className="space-y-1.5">
                    <span className="text-[10px] text-zinc-500 font-semibold uppercase tracking-wider block">
                      Immediate Clinical Actions
                    </span>
                    <ul className="space-y-1">
                      {result.recommendedActions.map((action, i) => (
                        <li
                          key={i}
                          className="flex items-start gap-1.5 text-zinc-700 dark:text-zinc-300"
                        >
                          <CheckCircle2 className="w-3.5 h-3.5 text-emerald-500 shrink-0 mt-0.5" />
                          <span>{action}</span>
                        </li>
                      ))}
                    </ul>
                  </div>
                )}

                <div className="flex items-center justify-between text-[11px] text-zinc-400 pt-1 border-t border-zinc-100 dark:border-zinc-800">
                  <span>Confidence: {(result.confidence * 100).toFixed(0)}%</span>
                  <span className="italic">Clinical Human-in-the-Loop</span>
                </div>
              </div>
            )}

            {result && !loading && (
              <div className="flex items-center justify-end gap-2 pt-2 border-t border-zinc-100 dark:border-zinc-800">
                <button
                  type="button"
                  onClick={() => setShowModal(false)}
                  className="px-3 py-1.5 text-xs text-zinc-600 dark:text-zinc-400 hover:bg-zinc-100 dark:hover:bg-zinc-800 rounded-lg transition-colors"
                >
                  Close
                </button>
                {onApplySuggestion && (
                  <button
                    type="button"
                    onClick={() => {
                      onApplySuggestion(result);
                      setShowModal(false);
                    }}
                    className="px-3.5 py-1.5 text-xs font-medium text-white bg-indigo-600 hover:bg-indigo-700 rounded-lg shadow-sm transition-colors"
                  >
                    Apply to Report Form
                  </button>
                )}
              </div>
            )}
          </div>
        </div>
      )}
    </>
  );
};
