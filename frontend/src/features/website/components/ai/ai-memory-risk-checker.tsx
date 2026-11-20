import React, { useState } from "react";
import { FallbackNotice } from "./fallback-notice";
import { Link } from "react-router";
import {
  websiteAiService,
  type MemoryRiskRequest,
  type MemoryRiskResponse,
} from "../../services/website-ai-service";

const SYMPTOM_CHECKLIST = [
  {
    id: "FORGETTING_RECENT",
    label: "Forgetting recently learned information & asking repetitive questions",
    icon: "🧠",
  },
  {
    id: "PLANNING_DIFFICULTY",
    label: "Difficulty following familiar recipes or keeping track of bills",
    icon: "📝",
  },
  {
    id: "CONFUSION_TIME_PLACE",
    label: "Losing track of dates, seasons, or how they arrived somewhere",
    icon: "🧭",
  },
  {
    id: "SPATIAL_VISION",
    label: "Trouble judging distance, contrast, or reading signs",
    icon: "👁️",
  },
  {
    id: "MISPLACING_THINGS",
    label: "Putting things in unusual places and inability to retrace steps",
    icon: "🔍",
  },
  {
    id: "MOOD_WITHDRAWAL",
    label: "Uncharacteristic mood swings, suspicion, fear, or social withdrawal",
    icon: "🥀",
  },
  {
    id: "WANDERING_SAFETY",
    label: "Wandering away from home, confusion at sundown (sundowning)",
    icon: "🚶",
  },
];

const DURATION_OPTIONS = [
  "Less than 3 months",
  "3 to 12 months",
  "More than 1 year",
];

export const AiMemoryRiskChecker: React.FC = () => {
  const [selectedSigns, setSelectedSigns] = useState<string[]>([
    "FORGETTING_RECENT",
    "CONFUSION_TIME_PLACE",
  ]);
  const [duration, setDuration] = useState<string>("3 to 12 months");
  const [notes, setNotes] = useState<string>("");

  const [loading, setLoading] = useState<boolean>(false);
  const [result, setResult] = useState<MemoryRiskResponse | null>(null);
  const [error, setError] = useState<string | null>(null);

  const toggleSign = (id: string) => {
    setSelectedSigns((prev) =>
      prev.includes(id) ? prev.filter((item) => item !== id) : [...prev, id]
    );
  };

  const handleAnalyze = async () => {
    if (selectedSigns.length === 0) {
      setError("Please select at least one observed sign to analyze.");
      return;
    }

    setLoading(true);
    setError(null);

    const signLabels = selectedSigns.map(
      (id) => SYMPTOM_CHECKLIST.find((s) => s.id === id)?.label || id
    );

    const payload: MemoryRiskRequest = {
      observedSigns: signLabels,
      duration,
      additionalObservations: notes.trim() || undefined,
    };

    try {
      const response = await websiteAiService.explainMemoryRisk(payload);
      setResult(response);
    } catch {
      setError("Unable to process assessment right now. Please call us at (916) 555-9000.");
    } finally {
      setLoading(false);
    }
  };

  const handleReset = () => {
    setResult(null);
    setError(null);
  };

  return (
    <div className="bg-gradient-to-br from-white via-purple-50/20 to-blue-50/30 rounded-3xl border border-purple-100 shadow-xl overflow-hidden p-6 lg:p-10">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-6 border-b border-gray-100">
        <div className="flex items-center gap-3.5">
          <div className="w-12 h-12 rounded-2xl bg-gradient-to-br from-[#7048e8] to-[#3B5BDB] flex items-center justify-center text-white text-2xl shadow-md">
            🧠
          </div>
          <div>
            <div className="flex items-center gap-2">
              <span className="text-xs font-bold uppercase tracking-wider bg-purple-100 text-[#7048e8] px-2.5 py-0.5 rounded-full">
                AI Memory Advisor
              </span>
              <span className="text-xs text-gray-400">Confidential & Educational</span>
            </div>
            <h3 className="text-xl font-bold text-[#0f1a3e] mt-0.5">
              Memory & Cognitive Symptoms Explainer
            </h3>
          </div>
        </div>
      </div>

      {!result ? (
        <div className="py-6 space-y-6">
          <div>
            <h4 className="text-base font-semibold text-[#0f1a3e] mb-1">
              Select any behaviors or changes you've noticed in your loved one:
            </h4>
            <p className="text-sm text-gray-500">
              Our AI educational assistant will help translate these observations into practical family guidance and support options.
            </p>
          </div>

          {/* Checklist */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
            {SYMPTOM_CHECKLIST.map((item) => {
              const checked = selectedSigns.includes(item.id);
              return (
                <button
                  key={item.id}
                  type="button"
                  onClick={() => toggleSign(item.id)}
                  className={`flex items-start gap-3 p-3.5 rounded-2xl border text-left transition-all ${
                    checked
                      ? "border-[#7048e8] bg-purple-50/60 shadow-sm"
                      : "border-gray-200 hover:border-gray-300 bg-white"
                  }`}
                >
                  <span className="text-xl mt-0.5">{item.icon}</span>
                  <p
                    className={`text-sm flex-1 leading-snug ${
                      checked ? "text-[#7048e8] font-semibold" : "text-gray-700"
                    }`}
                  >
                    {item.label}
                  </p>
                  <div
                    className={`w-5 h-5 rounded-md border flex items-center justify-center text-xs mt-0.5 shrink-0 ${
                      checked
                        ? "bg-[#7048e8] border-[#7048e8] text-white"
                        : "border-gray-300"
                    }`}
                  >
                    {checked && "✓"}
                  </div>
                </button>
              );
            })}
          </div>

          {/* Duration & Notes */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 pt-2">
            <div>
              <label className="block text-xs font-semibold text-gray-600 mb-2">
                Approximate Duration of Symptoms
              </label>
              <div className="grid grid-cols-3 gap-2">
                {DURATION_OPTIONS.map((d) => (
                  <button
                    key={d}
                    type="button"
                    onClick={() => setDuration(d)}
                    className={`py-2.5 px-2 rounded-xl border text-xs text-center font-medium transition-all ${
                      duration === d
                        ? "border-[#7048e8] bg-purple-50 text-[#7048e8] font-semibold"
                        : "border-gray-200 hover:border-gray-300 text-gray-600 bg-white"
                    }`}
                  >
                    {d}
                  </button>
                ))}
              </div>
            </div>

            <div>
              <label className="block text-xs font-semibold text-gray-600 mb-2">
                Specific Examples or Context (Optional)
              </label>
              <input
                type="text"
                value={notes}
                onChange={(e) => setNotes(e.target.value)}
                placeholder="e.g. Left stove on twice, struggles with recognizing distant cousins..."
                className="w-full px-4 py-2.5 rounded-xl border border-gray-200 text-sm focus:border-[#7048e8] focus:ring-2 focus:ring-purple-100 outline-none bg-white"
              />
            </div>
          </div>

          {error && (
            <div className="p-3 bg-red-50 border border-red-200 rounded-xl text-xs text-red-600">
              {error}
            </div>
          )}

          <div className="flex justify-end pt-4">
            <button
              type="button"
              onClick={handleAnalyze}
              disabled={loading}
              className="px-8 py-3 bg-gradient-to-r from-[#7048e8] to-[#3B5BDB] hover:from-[#5f3dc4] hover:to-[#2f4cb5] text-white font-bold rounded-xl transition-all shadow-lg flex items-center gap-2 disabled:opacity-50"
            >
              {loading ? (
                <>
                  <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></div>
                  Analyzing Observations...
                </>
              ) : (
                <>
                  <span>✨</span>
                  Analyze & Explain Symptoms
                </>
              )}
            </button>
          </div>
        </div>
      ) : (
        <div className="py-6 space-y-6">
          {result.fallback && <FallbackNotice />}
          {/* Result Banner */}
          <div className="bg-gradient-to-r from-[#5f3dc4] to-[#7048e8] text-white rounded-2xl p-6 shadow-lg">
            <span className="text-xs uppercase font-bold tracking-wider bg-white/20 px-3 py-1 rounded-full text-purple-100">
              Clinical Context Estimate
            </span>
            <h4 className="text-2xl font-bold mt-2">{result.estimatedCategory}</h4>
          </div>

          {/* Perspective */}
          <div className="bg-white rounded-2xl border border-gray-100 p-5 shadow-sm">
            <h5 className="text-xs font-bold uppercase tracking-wider text-purple-900 mb-2 flex items-center gap-2">
              <span>🩺</span> Educational Clinical Perspective
            </h5>
            <p className="text-sm text-gray-700 leading-relaxed">
              {result.clinicalPerspective}
            </p>
          </div>

          {/* Home Strategies */}
          {result.homeStrategies && result.homeStrategies.length > 0 && (
            <div className="bg-white rounded-2xl border border-gray-100 p-5 shadow-sm">
              <h5 className="text-xs font-bold uppercase tracking-wider text-purple-900 mb-3 flex items-center gap-2">
                <span>🏡</span> Recommended Supportive Strategies for Family Caregivers
              </h5>
              <div className="space-y-2.5">
                {result.homeStrategies.map((strat, i) => (
                  <div key={i} className="flex items-start gap-2.5 text-sm text-gray-700">
                    <span className="text-[#7048e8] font-bold mt-0.5">•</span>
                    <span>{strat}</span>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Facility Support */}
          <div className="bg-purple-50/60 rounded-2xl border border-purple-200/70 p-5">
            <h5 className="text-xs font-bold uppercase tracking-wider text-purple-900 mb-2 flex items-center gap-2">
              <span>🛡️</span> How ElderCare's Memory Care Community Can Help
            </h5>
            <p className="text-sm text-purple-950 leading-relaxed">
              {result.facilitySupportPlan}
            </p>
          </div>

          {/* Next Steps */}
          <div className="bg-amber-50 rounded-2xl border border-amber-200/80 p-5 text-xs text-amber-900 leading-relaxed">
            <strong>Recommended Next Step:</strong> {result.recommendedNextStep}
          </div>

          {/* Disclaimer */}
          <div className="p-4 bg-gray-50 rounded-xl border border-gray-200 text-[11px] text-gray-500 leading-relaxed">
            <strong className="text-gray-700">Medical Disclaimer:</strong> This tool is an educational guide and does not replace medical diagnosis by a licensed physician, neurologist, or geriatric specialist. Memory loss can have many reversible causes (such as medication side effects, vitamin deficiencies, or thyroid disorders). We strongly encourage consulting a medical professional.
          </div>

          {/* Action buttons */}
          <div className="flex flex-col sm:flex-row items-center justify-between gap-4 pt-2">
            <button
              onClick={handleReset}
              className="text-xs text-gray-500 hover:text-gray-700 underline font-medium"
            >
              ← Re-evaluate Symptoms
            </button>
            <div className="flex flex-wrap gap-3 w-full sm:w-auto">
              <a
                href="tel:9165559000"
                className="flex-1 sm:flex-initial px-5 py-2.5 border border-gray-300 text-gray-700 font-semibold rounded-xl hover:bg-gray-50 text-xs text-center transition-all"
              >
                Call Memory Care Director
              </a>
              <Link
                to="/contact"
                className="flex-1 sm:flex-initial px-6 py-2.5 bg-[#7048e8] hover:bg-[#5f3dc4] text-white font-bold rounded-xl text-xs text-center transition-all shadow-md"
              >
                Schedule Private Memory Care Tour →
              </Link>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
