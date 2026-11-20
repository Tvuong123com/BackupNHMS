import React, { useState } from "react";
import { FallbackNotice } from "./fallback-notice";
import { Link } from "react-router";
import {
  websiteAiService,
  type ScreeningRequest,
  type ScreeningResponse,
} from "../../services/website-ai-service";

const MOBILITY_OPTIONS = [
  { id: "INDEPENDENT", label: "Walks independently", icon: "🚶" },
  { id: "CANE_WALKER", label: "Uses cane or walker", icon: "🦯" },
  { id: "WHEELCHAIR", label: "Uses wheelchair", icon: "🧑‍🦽" },
  { id: "BEDBOUND", label: "Bedbound / Immobility", icon: "🛏️" },
  { id: "UNKNOWN", label: "Not sure yet", icon: "❓" },
];

const CONDITION_OPTIONS = [
  { id: "Alzheimer's / Dementia", label: "Alzheimer's or Memory Loss", icon: "🧠" },
  { id: "Post-Stroke Recovery", label: "Post-Stroke Rehabilitation", icon: "🩺" },
  { id: "Post-Surgery / Fracture", label: "Post-Surgery / Orthopedic Recovery", icon: "🩹" },
  { id: "Diabetes Management", label: "Diabetes Care", icon: "💉" },
  { id: "Cardiac / Hypertension", label: "Heart Disease / Hypertension", icon: "❤️" },
  { id: "Chronic Pain / Arthritis", label: "Arthritis / Chronic Pain", icon: "🦴" },
  { id: "Incontinence", label: "Incontinence Care", icon: "💧" },
];

const DAILY_NEEDS_OPTIONS = [
  { id: "EATING", label: "Assistance with meals & eating", icon: "🍽️" },
  { id: "BATHING", label: "Bathing and personal grooming", icon: "🛁" },
  { id: "DRESSING", label: "Getting dressed and clothing care", icon: "👕" },
  { id: "MEDICATION", label: "Medication reminders and administration", icon: "💊" },
  { id: "SUPERVISION", label: "24/7 safety supervision & wandering prevention", icon: "👁️" },
  { id: "COMPANIONSHIP", label: "Social companionship & mental stimulation", icon: "🤝" },
];

export const AiEligibilityScreener: React.FC = () => {
  const [step, setStep] = useState<number>(1);
  const [age, setAge] = useState<string>("");
  const [gender, setGender] = useState<string>("");
  const [mobility, setMobility] = useState<string>("");
  const [conditions, setConditions] = useState<string[]>([]);
  const [dailyNeeds, setDailyNeeds] = useState<string[]>([]);
  const [notes, setNotes] = useState<string>("");

  const [loading, setLoading] = useState<boolean>(false);
  const [result, setResult] = useState<ScreeningResponse | null>(null);
  const [error, setError] = useState<string | null>(null);

  const toggleCondition = (id: string) => {
    setConditions((prev) =>
      prev.includes(id) ? prev.filter((item) => item !== id) : [...prev, id]
    );
  };

  const toggleNeed = (id: string) => {
    setDailyNeeds((prev) =>
      prev.includes(id) ? prev.filter((item) => item !== id) : [...prev, id]
    );
  };

  const handleSubmit = async () => {
    setLoading(true);
    setError(null);

    const payload: ScreeningRequest = {
      age: parseInt(age, 10) || undefined,
      gender,
      mobilityStatus: mobility,
      medicalConditions: conditions,
      dailyNeedsHelp: dailyNeeds,
      additionalNotes: notes.trim() || undefined,
    };

    try {
      const response = await websiteAiService.screenAdmission(payload);
      setResult(response);
      setStep(4);
    } catch {
      setError("We encountered an issue analyzing your information. Please contact our team directly.");
    } finally {
      setLoading(false);
    }
  };

  const handleReset = () => {
    setStep(1);
    setResult(null);
    setError(null);
    setNotes("");
    setAge("");
    setGender("");
    setMobility("");
    setConditions([]);
    setDailyNeeds([]);
  };

  return (
    <div className="bg-gradient-to-br from-white to-blue-50/40 rounded-3xl border border-blue-100 shadow-xl overflow-hidden p-6 lg:p-10">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-6 border-b border-gray-100">
        <div className="flex items-center gap-3.5">
          <div className="w-12 h-12 rounded-2xl bg-gradient-to-br from-[#1e3a8a] to-[#3B5BDB] flex items-center justify-center text-white shadow-md">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="24"
              height="24"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
            >
              <path d="M12 2v4M12 18v4M4.93 4.93l2.83 2.83M16.24 16.24l2.83 2.83M2 12h4M18 12h4M4.93 19.07l2.83-2.83M16.24 7.76l2.83-2.83" />
            </svg>
          </div>
          <div>
            <div className="flex items-center gap-2">
              <span className="text-xs font-bold uppercase tracking-wider bg-blue-100 text-[#3B5BDB] px-2.5 py-0.5 rounded-full">
                AI Powered
              </span>
              <span className="text-xs text-gray-400">Takes 2 minutes</span>
            </div>
            <h3 className="text-xl font-bold text-[#0f1a3e] mt-0.5">
              Admission Eligibility Pre-Screener
            </h3>
          </div>
        </div>

        {/* Step Indicator */}
        {step <= 3 && (
          <div className="flex items-center gap-2">
            {[1, 2, 3].map((s) => (
              <div key={s} className="flex items-center gap-1.5">
                <div
                  className={`w-7 h-7 rounded-full flex items-center justify-center text-xs font-bold transition-all ${
                    step === s
                      ? "bg-[#3B5BDB] text-white shadow-sm"
                      : step > s
                      ? "bg-green-500 text-white"
                      : "bg-gray-100 text-gray-400"
                  }`}
                >
                  {step > s ? "✓" : s}
                </div>
                {s < 3 && <div className="w-4 h-0.5 bg-gray-200"></div>}
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Step 1: Basic Info & Mobility */}
      {step === 1 && (
        <div className="py-6 space-y-6">
          <div>
            <h4 className="text-base font-semibold text-[#0f1a3e] mb-1">
              Step 1 of 3: Loved One's General Profile
            </h4>
            <p className="text-sm text-gray-500">
              Please tell us about your family member to help us customize our evaluation.
            </p>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold text-gray-600 mb-2">
                Approximate Age
              </label>
              <input
                type="number"
                min="40"
                max="115"
                value={age}
                onChange={(e) => setAge(e.target.value)}
                className="w-full px-4 py-3 rounded-xl border border-gray-200 focus:border-[#3B5BDB] focus:ring-2 focus:ring-blue-100 outline-none text-sm bg-white"
                placeholder="e.g. 78"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-gray-600 mb-2">
                Gender
              </label>
              <div className="grid grid-cols-3 gap-2">
                {["Female", "Male", "Other"].map((g) => (
                  <button
                    key={g}
                    type="button"
                    onClick={() => setGender(g)}
                    className={`py-3 rounded-xl border text-sm font-medium transition-all ${
                      gender === g
                        ? "border-[#3B5BDB] bg-blue-50 text-[#3B5BDB] font-semibold"
                        : "border-gray-200 hover:border-gray-300 text-gray-600 bg-white"
                    }`}
                  >
                    {g}
                  </button>
                ))}
              </div>
            </div>
          </div>

          <div>
            <label className="block text-xs font-semibold text-gray-600 mb-2">
              Mobility & Movement Ability
            </label>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              {MOBILITY_OPTIONS.map((m) => (
                <button
                  key={m.id}
                  type="button"
                  onClick={() => setMobility(m.id)}
                  className={`flex items-center gap-3 p-3.5 rounded-2xl border text-left transition-all ${
                    mobility === m.id
                      ? "border-[#3B5BDB] bg-blue-50 shadow-sm"
                      : "border-gray-200 hover:border-gray-300 bg-white"
                  }`}
                >
                  <span className="text-2xl">{m.icon}</span>
                  <div className="flex-1">
                    <p
                      className={`text-sm font-semibold ${
                        mobility === m.id ? "text-[#3B5BDB]" : "text-gray-800"
                      }`}
                    >
                      {m.label}
                    </p>
                  </div>
                  {mobility === m.id && (
                    <span className="text-[#3B5BDB] font-bold text-lg">✓</span>
                  )}
                </button>
              ))}
            </div>
          </div>

          <div className="flex justify-end pt-4">
            <button
              onClick={() => setStep(2)}
              disabled={!mobility}
              className="px-6 py-3 bg-[#3B5BDB] hover:bg-[#2f4cb5] text-white font-semibold rounded-xl transition-all shadow-md flex items-center gap-2 disabled:opacity-50"
            >
              Next: Health Conditions
              <span>→</span>
            </button>
          </div>
        </div>
      )}

      {/* Step 2: Health Conditions */}
      {step === 2 && (
        <div className="py-6 space-y-6">
          <div>
            <h4 className="text-base font-semibold text-[#0f1a3e] mb-1">
              Step 2 of 3: Known Medical Conditions
            </h4>
            <p className="text-sm text-gray-500">
              Select all conditions that apply. This helps determine specialized programs like Memory Care or Physical Therapy.
            </p>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
            {CONDITION_OPTIONS.map((c) => {
              const checked = conditions.includes(c.id);
              return (
                <button
                  key={c.id}
                  type="button"
                  onClick={() => toggleCondition(c.id)}
                  className={`flex items-center gap-3 p-3.5 rounded-2xl border text-left transition-all ${
                    checked
                      ? "border-[#3B5BDB] bg-blue-50/70 shadow-sm"
                      : "border-gray-200 hover:border-gray-300 bg-white"
                  }`}
                >
                  <span className="text-2xl">{c.icon}</span>
                  <p
                    className={`text-sm font-medium flex-1 ${
                      checked ? "text-[#3B5BDB] font-semibold" : "text-gray-700"
                    }`}
                  >
                    {c.label}
                  </p>
                  <div
                    className={`w-5 h-5 rounded-md border flex items-center justify-center text-xs ${
                      checked
                        ? "bg-[#3B5BDB] border-[#3B5BDB] text-white"
                        : "border-gray-300"
                    }`}
                  >
                    {checked && "✓"}
                  </div>
                </button>
              );
            })}
          </div>

          <div className="flex items-center justify-between pt-4">
            <button
              type="button"
              onClick={() => setStep(1)}
              className="px-5 py-2.5 border border-gray-200 text-gray-600 rounded-xl hover:bg-gray-50 text-sm font-medium transition-all"
            >
              ← Back
            </button>
            <button
              type="button"
              onClick={() => setStep(3)}
              className="px-6 py-3 bg-[#3B5BDB] hover:bg-[#2f4cb5] text-white font-semibold rounded-xl transition-all shadow-md flex items-center gap-2"
            >
              Next: Daily Support Needs
              <span>→</span>
            </button>
          </div>
        </div>
      )}

      {/* Step 3: Daily Needs & Notes */}
      {step === 3 && (
        <div className="py-6 space-y-6">
          <div>
            <h4 className="text-base font-semibold text-[#0f1a3e] mb-1">
              Step 3 of 3: Daily Assistance & Care Needs
            </h4>
            <p className="text-sm text-gray-500">
              Which activities of daily living does your loved one need assistance with?
            </p>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
            {DAILY_NEEDS_OPTIONS.map((d) => {
              const checked = dailyNeeds.includes(d.id);
              return (
                <button
                  key={d.id}
                  type="button"
                  onClick={() => toggleNeed(d.id)}
                  className={`flex items-center gap-3 p-3.5 rounded-2xl border text-left transition-all ${
                    checked
                      ? "border-[#3B5BDB] bg-blue-50/70 shadow-sm"
                      : "border-gray-200 hover:border-gray-300 bg-white"
                  }`}
                >
                  <span className="text-2xl">{d.icon}</span>
                  <p
                    className={`text-sm font-medium flex-1 ${
                      checked ? "text-[#3B5BDB] font-semibold" : "text-gray-700"
                    }`}
                  >
                    {d.label}
                  </p>
                  <div
                    className={`w-5 h-5 rounded-md border flex items-center justify-center text-xs ${
                      checked
                        ? "bg-[#3B5BDB] border-[#3B5BDB] text-white"
                        : "border-gray-300"
                    }`}
                  >
                    {checked && "✓"}
                  </div>
                </button>
              );
            })}
          </div>

          <div>
            <label className="block text-xs font-semibold text-gray-600 mb-1.5">
              Additional Details or Special Considerations (Optional)
            </label>
            <textarea
              rows={3}
              value={notes}
              onChange={(e) => setNotes(e.target.value)}
              placeholder="e.g. Needs assistance with evening routine, gets agitated at sundown, uses hearing aid..."
              className="w-full px-4 py-3 rounded-xl border border-gray-200 focus:border-[#3B5BDB] focus:ring-2 focus:ring-blue-100 outline-none text-sm bg-white"
            />
          </div>

          {error && (
            <div className="p-3 bg-red-50 border border-red-200 rounded-xl text-xs text-red-600">
              {error}
            </div>
          )}

          <div className="flex items-center justify-between pt-4">
            <button
              type="button"
              onClick={() => setStep(2)}
              disabled={loading}
              className="px-5 py-2.5 border border-gray-200 text-gray-600 rounded-xl hover:bg-gray-50 text-sm font-medium transition-all"
            >
              ← Back
            </button>
            <button
              type="button"
              onClick={handleSubmit}
              disabled={loading}
              className="px-8 py-3 bg-gradient-to-r from-[#1e3a8a] to-[#3B5BDB] hover:from-[#172554] hover:to-[#2f4cb5] text-white font-bold rounded-xl transition-all shadow-lg flex items-center gap-2 disabled:opacity-50"
            >
              {loading ? (
                <>
                  <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></div>
                  Analyzing with AI...
                </>
              ) : (
                <>
                  <span>✨</span>
                  Analyze & Recommend Care
                </>
              )}
            </button>
          </div>
        </div>
      )}

      {/* Step 4: AI Recommendations Result */}
      {step === 4 && result && (
        <div className="py-6 space-y-6">
          {result.fallback && <FallbackNotice />}
          <div className="bg-gradient-to-r from-blue-600 to-[#1e3a8a] text-white rounded-2xl p-6 shadow-md relative overflow-hidden">
            <div className="relative z-10">
              <span className="text-xs uppercase font-bold tracking-wider text-blue-200 bg-white/10 px-3 py-1 rounded-full border border-white/20">
                Program to discuss
              </span>
              <h4 className="text-2xl font-bold mt-2">{result.recommendedService}</h4>
              <p className="text-blue-100 text-sm mt-1">
                Care level assessment:{" "}
                <span className="font-semibold text-white">
                  {result.suggestedCareLevel}
                </span>
              </p>
            </div>
          </div>

          {/* Details Grid */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="bg-white rounded-2xl border border-gray-100 p-5 shadow-sm">
              <h5 className="text-xs font-bold uppercase tracking-wider text-gray-400 mb-2 flex items-center gap-2">
                <span>📋</span> Why this suggestion came up
              </h5>
              <p className="text-sm text-gray-700 leading-relaxed">
                {result.rationale}
              </p>
            </div>

            <div className="bg-white rounded-2xl border border-gray-100 p-5 shadow-sm">
              <h5 className="text-xs font-bold uppercase tracking-wider text-gray-400 mb-2 flex items-center gap-2">
                <span>⚡</span> Estimated Timeline
              </h5>
              <p className="text-2xl font-bold text-[#0f1a3e]">
                {result.estimatedTimeline}
              </p>
              <p className="text-xs text-gray-500 mt-1">
                Typical processing time; confirm timing with Admissions.
              </p>
            </div>
          </div>

          {/* Additional Services */}
          {result.additionalServices && result.additionalServices.length > 0 && (
            <div className="bg-white rounded-2xl border border-gray-100 p-5 shadow-sm">
              <h5 className="text-xs font-bold uppercase tracking-wider text-gray-400 mb-3 flex items-center gap-2">
                <span>➕</span> Suggested Complementary Services
              </h5>
              <div className="flex flex-wrap gap-2">
                {result.additionalServices.map((svc, i) => (
                  <span
                    key={i}
                    className="text-xs font-medium bg-blue-50 text-[#3B5BDB] border border-blue-100 px-3 py-1.5 rounded-full"
                  >
                    {svc}
                  </span>
                ))}
              </div>
            </div>
          )}

          {/* Next Steps Card */}
          <div className="bg-amber-50/60 border border-amber-200/80 rounded-2xl p-5">
            <h5 className="text-xs font-bold uppercase tracking-wider text-amber-800 mb-1 flex items-center gap-2">
              <span>💡</span> Recommended Next Step
            </h5>
            <p className="text-sm text-amber-900 leading-relaxed">
              {result.nextStep}
            </p>
          </div>

          {/* Clinical Disclaimer */}
          <div className="p-4 bg-gray-50 rounded-xl border border-gray-200 text-[11px] text-gray-500 leading-relaxed">
            <strong className="text-gray-700">Disclaimer:</strong> This pre-screening is an AI-assisted informational estimate and does not constitute a formal medical evaluation or admission guarantee. Final Level of Care (LOC) and admission eligibility are determined through a comprehensive in-person evaluation conducted by our Director of Nursing (DON).
          </div>

          {/* Action Buttons */}
          <div className="flex flex-col sm:flex-row items-center justify-between gap-4 pt-2">
            <button
              onClick={handleReset}
              className="text-sm text-gray-500 hover:text-gray-700 underline font-medium"
            >
              ← Retake Assessment
            </button>
            <div className="flex flex-wrap gap-3 w-full sm:w-auto">
              <a
                href="tel:9165559000"
                className="flex-1 sm:flex-initial px-5 py-2.5 border border-gray-300 text-gray-700 font-semibold rounded-xl hover:bg-gray-50 text-sm text-center transition-all"
              >
                Call (916) 555-9000
              </a>
              <Link
                to="/contact"
                className="flex-1 sm:flex-initial px-6 py-2.5 bg-[#3B5BDB] hover:bg-[#2f4cb5] text-white font-bold rounded-xl text-sm text-center transition-all shadow-md"
              >
                Request a Consultation →
              </Link>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
