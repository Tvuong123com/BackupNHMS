import React, { useState } from "react";
import { Link } from "react-router";
import { FallbackNotice } from "./fallback-notice";
import {
  websiteAiService,
  type ScreeningRequest,
  type ScreeningResponse,
} from "../../services/website-ai-service";

interface AiCareNavigatorProps {
  className?: string;
  sourceContext?: "home" | "admissions";
}

const RECIPIENT_OPTIONS = [
  { id: "Parent / In-Law", label: "Parent / In-Law", icon: "👵" },
  { id: "Spouse", label: "Spouse / Partner", icon: "💑" },
  { id: "Myself", label: "Myself", icon: "🧘" },
  { id: "Relative or Friend", label: "Other Loved One", icon: "🤝" },
];

const TIMELINE_OPTIONS = [
  { id: "Immediate (within 48h)", label: "Immediate (within 48h)", desc: "Urgent admission needed", icon: "⚡" },
  { id: "Within 1 to 4 weeks", label: "Next 1–4 Weeks", desc: "Actively planning move", icon: "📅" },
  { id: "Planning ahead", label: "Planning Ahead", desc: "Exploring options for later", icon: "💭" },
];

const MOBILITY_OPTIONS = [
  { id: "INDEPENDENT", label: "Walks independently", desc: "No mobility devices needed", icon: "🚶" },
  { id: "CANE_WALKER", label: "Uses cane or walker", desc: "Needs steadying support", icon: "🦯" },
  { id: "WHEELCHAIR", label: "Uses wheelchair", desc: "Assistance with transfers", icon: "🧑‍🦽" },
  { id: "BEDBOUND", label: "Bedbound / Limited mobility", desc: "Full physical assistance", icon: "🛏️" },
];

const DAILY_NEEDS_OPTIONS = [
  { id: "EATING", label: "Meals & Nutrition Support", icon: "🍽️" },
  { id: "BATHING", label: "Bathing & Personal Grooming", icon: "🛁" },
  { id: "DRESSING", label: "Dressing & Hygiene", icon: "👕" },
  { id: "MEDICATION", label: "Medication Administration", icon: "💊" },
  { id: "SUPERVISION", label: "24/7 Safety & Memory Supervision", icon: "👁️" },
  { id: "COMPANIONSHIP", label: "Social Activities & Companionship", icon: "🎨" },
];

const CONDITION_OPTIONS = [
  { id: "Alzheimer's / Dementia", label: "Memory Loss / Dementia", icon: "🧠" },
  { id: "Post-Stroke Recovery", label: "Stroke / Neurological Care", icon: "🩺" },
  { id: "Post-Surgery / Fracture", label: "Post-Surgery / Rehab", icon: "🩹" },
  { id: "Diabetes Management", label: "Diabetes / Wound Care", icon: "💉" },
  { id: "Cardiac / Hypertension", label: "Heart Condition / Vitals Care", icon: "❤️" },
  { id: "General Frailty", label: "General Age-Related Frailty", icon: "🌿" },
];

export const AiCareNavigator: React.FC<AiCareNavigatorProps> = ({
  className = "",
  sourceContext = "admissions",
}) => {
  const [step, setStep] = useState<number>(1);
  const [recipient, setRecipient] = useState<string>("Parent / In-Law");
  const [urgency, setUrgency] = useState<string>("Within 1 to 4 weeks");
  const [approxAge, setApproxAge] = useState<string>("78");
  const [mobility, setMobility] = useState<string>("CANE_WALKER");
  const [dailyNeeds, setDailyNeeds] = useState<string[]>(["MEDICATION", "BATHING"]);
  const [conditions, setConditions] = useState<string[]>([]);
  const [additionalNotes, setAdditionalNotes] = useState<string>("");

  const [loading, setLoading] = useState<boolean>(false);
  const [result, setResult] = useState<ScreeningResponse | null>(null);
  const [error, setError] = useState<string | null>(null);

  const toggleNeed = (id: string) => {
    setDailyNeeds((prev) =>
      prev.includes(id) ? prev.filter((item) => item !== id) : [...prev, id]
    );
  };

  const toggleCondition = (id: string) => {
    setConditions((prev) =>
      prev.includes(id) ? prev.filter((item) => item !== id) : [...prev, id]
    );
  };

  const handleEvaluate = async () => {
    setLoading(true);
    setError(null);

    const payload: ScreeningRequest = {
      age: parseInt(approxAge, 10) || 75,
      gender: "Not specified",
      mobilityStatus: mobility,
      medicalConditions: conditions.length > 0 ? conditions : ["General senior care"],
      dailyNeedsHelp: dailyNeeds,
      additionalNotes: [
        `Recipient: ${recipient}`,
        `Timeline: ${urgency}`,
        additionalNotes.trim(),
      ]
        .filter(Boolean)
        .join("; "),
    };

    try {
      const response = await websiteAiService.screenAdmission(payload);
      setResult(response);
      setStep(4);
    } catch {
      setError("Unable to process screening at this moment. Please call our team at (916) 555-9000.");
    } finally {
      setLoading(false);
    }
  };

  const handleReset = () => {
    setStep(1);
    setResult(null);
    setError(null);
    setAdditionalNotes("");
  };

  return (
    <div
      className={`bg-white rounded-3xl border border-blue-100/80 shadow-xl overflow-hidden p-6 sm:p-10 relative ${className}`}
    >
      {/* Decorative top ambient glow */}
      <div className="absolute top-0 right-0 w-80 h-80 bg-gradient-to-br from-blue-100/40 via-indigo-50/30 to-transparent rounded-full blur-3xl pointer-events-none -mr-20 -mt-20" />

      {/* Header & Stepper */}
      <div className="relative z-10 pb-6 border-b border-gray-100 flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2">
            <span className="text-xs font-bold uppercase tracking-wider bg-blue-50 text-[#3B5BDB] px-3 py-1 rounded-full border border-blue-100">
              Interactive Care Navigator
            </span>
            <span className="text-xs text-gray-400">2-Minute AI Intake Guide</span>
          </div>
          <h3 className="text-2xl sm:text-3xl font-bold text-[#0f1a3e] mt-1.5">
            {sourceContext === "home"
              ? "Find the Best Care Plan for Your Loved One"
              : "AI Pre-Admission Care Navigator"}
          </h3>
          <p className="text-sm text-gray-500 mt-1 max-w-2xl">
            Answer a few quick questions to receive personalized program suggestions, expected support levels, and guided next steps.
          </p>
        </div>

        {/* Progress indicator */}
        <div className="flex items-center gap-2 shrink-0 self-start md:self-auto bg-gray-50 px-4 py-2 rounded-2xl border border-gray-100">
          <span className="text-xs font-semibold text-gray-500">
            Step {step} of 4
          </span>
          <div className="flex gap-1.5">
            {[1, 2, 3, 4].map((s) => (
              <span
                key={s}
                className={`w-2.5 h-2.5 rounded-full transition-all duration-300 ${
                  s === step
                    ? "bg-[#3B5BDB] w-6"
                    : s < step
                    ? "bg-blue-300"
                    : "bg-gray-200"
                }`}
              />
            ))}
          </div>
        </div>
      </div>

      {/* STEP 1: Recipient & Timeline */}
      {step === 1 && (
        <div className="py-8 space-y-7 relative z-10 animate-fade-in">
          <div>
            <label className="block text-sm font-bold text-[#0f1a3e] mb-3">
              1. Who is this care inquiry for?
            </label>
            <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
              {RECIPIENT_OPTIONS.map((opt) => (
                <button
                  key={opt.id}
                  type="button"
                  onClick={() => setRecipient(opt.id)}
                  className={`p-4 rounded-2xl border-2 text-center transition-all duration-200 flex flex-col items-center gap-2 ${
                    recipient === opt.id
                      ? "border-[#3B5BDB] bg-blue-50/50 shadow-sm"
                      : "border-gray-100 hover:border-gray-200 bg-white"
                  }`}
                >
                  <span className="text-2xl">{opt.icon}</span>
                  <span className="text-sm font-semibold text-[#0f1a3e]">{opt.label}</span>
                </button>
              ))}
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
            <div>
              <label htmlFor="approxAge" className="block text-sm font-bold text-[#0f1a3e] mb-2">
                Approximate Age
              </label>
              <input
                id="approxAge"
                type="number"
                min="50"
                max="110"
                value={approxAge}
                onChange={(e) => setApproxAge(e.target.value)}
                placeholder="e.g. 78"
                className="w-full px-4 py-3 rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-[#3B5BDB]/20 focus:border-[#3B5BDB] text-sm"
              />
              <p className="text-xs text-gray-400 mt-1">Helps align with age-tailored wellness protocols.</p>
            </div>

            <div>
              <label className="block text-sm font-bold text-[#0f1a3e] mb-2">
                When would care ideally start?
              </label>
              <div className="space-y-2">
                {TIMELINE_OPTIONS.map((opt) => (
                  <button
                    key={opt.id}
                    type="button"
                    onClick={() => setUrgency(opt.id)}
                    className={`w-full p-2.5 px-3 rounded-xl border text-left flex items-center justify-between text-xs transition-all ${
                      urgency === opt.id
                        ? "border-[#3B5BDB] bg-blue-50/60 font-semibold text-[#3B5BDB]"
                        : "border-gray-100 hover:border-gray-200 text-gray-600 bg-white"
                    }`}
                  >
                    <span>{opt.label}</span>
                    <span className="text-gray-400 text-[11px]">{opt.desc}</span>
                  </button>
                ))}
              </div>
            </div>
          </div>

          <div className="pt-4 flex justify-end">
            <button
              type="button"
              onClick={() => setStep(2)}
              className="px-8 py-3.5 bg-[#3B5BDB] hover:bg-[#2f4ac4] text-white font-bold rounded-2xl shadow-lg shadow-blue-500/20 transition-all flex items-center gap-2"
            >
              Continue to Daily Living Needs
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
              </svg>
            </button>
          </div>
        </div>
      )}

      {/* STEP 2: Mobility & Daily Living Needs */}
      {step === 2 && (
        <div className="py-8 space-y-7 relative z-10 animate-fade-in">
          <div>
            <label className="block text-sm font-bold text-[#0f1a3e] mb-3">
              2. How is their physical mobility?
            </label>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
              {MOBILITY_OPTIONS.map((opt) => (
                <button
                  key={opt.id}
                  type="button"
                  onClick={() => setMobility(opt.id)}
                  className={`p-4 rounded-2xl border-2 text-left transition-all ${
                    mobility === opt.id
                      ? "border-[#3B5BDB] bg-blue-50/50 shadow-sm"
                      : "border-gray-100 hover:border-gray-200 bg-white"
                  }`}
                >
                  <span className="text-2xl mb-1 block">{opt.icon}</span>
                  <p className="text-sm font-bold text-[#0f1a3e]">{opt.label}</p>
                  <p className="text-xs text-gray-500 mt-0.5">{opt.desc}</p>
                </button>
              ))}
            </div>
          </div>

          <div>
            <label className="block text-sm font-bold text-[#0f1a3e] mb-1">
              3. Which daily assistance areas are needed? (Select all that apply)
            </label>
            <p className="text-xs text-gray-500 mb-3">Activities of Daily Living (ADLs) guide clinical staffing levels.</p>
            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-3">
              {DAILY_NEEDS_OPTIONS.map((opt) => {
                const active = dailyNeeds.includes(opt.id);
                return (
                  <button
                    key={opt.id}
                    type="button"
                    onClick={() => toggleNeed(opt.id)}
                    className={`p-3.5 rounded-2xl border text-left flex items-center gap-3 transition-all ${
                      active
                        ? "border-[#3B5BDB] bg-blue-50/70 text-[#0f1a3e] font-semibold"
                        : "border-gray-100 hover:border-gray-200 text-gray-600 bg-white"
                    }`}
                  >
                    <span className="text-xl">{opt.icon}</span>
                    <span className="text-xs flex-1">{opt.label}</span>
                    <span
                      className={`w-4 h-4 rounded-full border flex items-center justify-center text-[10px] ${
                        active
                          ? "bg-[#3B5BDB] text-white border-[#3B5BDB]"
                          : "border-gray-300 bg-white"
                      }`}
                    >
                      {active ? "✓" : ""}
                    </span>
                  </button>
                );
              })}
            </div>
          </div>

          <div className="pt-4 flex items-center justify-between">
            <button
              type="button"
              onClick={() => setStep(1)}
              className="px-6 py-3 border border-gray-200 text-gray-600 font-semibold rounded-2xl hover:bg-gray-50 transition-all text-sm"
            >
              Back
            </button>
            <button
              type="button"
              onClick={() => setStep(3)}
              className="px-8 py-3.5 bg-[#3B5BDB] hover:bg-[#2f4ac4] text-white font-bold rounded-2xl shadow-lg shadow-blue-500/20 transition-all flex items-center gap-2 text-sm"
            >
              Next: Health Conditions
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
              </svg>
            </button>
          </div>
        </div>
      )}

      {/* STEP 3: Health Conditions & Preferences */}
      {step === 3 && (
        <div className="py-8 space-y-7 relative z-10 animate-fade-in">
          <div>
            <label className="block text-sm font-bold text-[#0f1a3e] mb-1">
              4. Any known health conditions or specific care priorities?
            </label>
            <p className="text-xs text-gray-500 mb-3">Optional. Helps identify specialized memory care or therapy programs.</p>
            <div className="grid grid-cols-2 sm:grid-cols-3 gap-3">
              {CONDITION_OPTIONS.map((opt) => {
                const active = conditions.includes(opt.id);
                return (
                  <button
                    key={opt.id}
                    type="button"
                    onClick={() => toggleCondition(opt.id)}
                    className={`p-3 rounded-2xl border text-left flex items-center gap-2.5 transition-all ${
                      active
                        ? "border-[#3B5BDB] bg-blue-50/70 text-[#0f1a3e] font-semibold"
                        : "border-gray-100 hover:border-gray-200 text-gray-600 bg-white"
                    }`}
                  >
                    <span className="text-lg">{opt.icon}</span>
                    <span className="text-xs flex-1">{opt.label}</span>
                    <span
                      className={`w-3.5 h-3.5 rounded-full border flex items-center justify-center text-[9px] ${
                        active
                          ? "bg-[#3B5BDB] text-white border-[#3B5BDB]"
                          : "border-gray-300 bg-white"
                      }`}
                    >
                      {active ? "✓" : ""}
                    </span>
                  </button>
                );
              })}
            </div>
          </div>

          <div>
            <label htmlFor="additionalNotes" className="block text-sm font-bold text-[#0f1a3e] mb-1">
              5. Any additional details or specific questions? (Optional)
            </label>
            <textarea
              id="additionalNotes"
              rows={3}
              value={additionalNotes}
              onChange={(e) => setAdditionalNotes(e.target.value)}
              placeholder="e.g. Needs physical therapy after a recent hip fracture, prefers quiet dining environment..."
              className="w-full px-4 py-3 rounded-2xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-[#3B5BDB]/20 focus:border-[#3B5BDB] text-sm resize-none"
            />
          </div>

          {error && (
            <div className="p-4 rounded-2xl bg-red-50 border border-red-200 text-xs text-red-600">
              {error}
            </div>
          )}

          <div className="pt-4 flex items-center justify-between">
            <button
              type="button"
              onClick={() => setStep(2)}
              className="px-6 py-3 border border-gray-200 text-gray-600 font-semibold rounded-2xl hover:bg-gray-50 transition-all text-sm"
            >
              Back
            </button>
            <button
              type="button"
              onClick={() => void handleEvaluate()}
              disabled={loading}
              className="px-8 py-3.5 bg-gradient-to-r from-[#3B5BDB] to-[#2563EB] hover:from-[#2f4ac4] hover:to-[#1d4ed8] text-white font-bold rounded-2xl shadow-xl shadow-blue-500/25 transition-all flex items-center gap-2 text-sm disabled:opacity-60"
            >
              {loading ? (
                <>
                  <svg className="animate-spin -ml-1 mr-2 h-4 w-4 text-white" fill="none" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
                  </svg>
                  Analyzing Care Fit...
                </>
              ) : (
                <>
                  ✨ Generate Care Recommendations
                </>
              )}
            </button>
          </div>
        </div>
      )}

      {/* STEP 4: Results & Action Plan */}
      {step === 4 && result && (
        <div className="py-8 space-y-7 relative z-10 animate-fade-in">
          {result.fallback && <FallbackNotice />}

          {/* Primary Recommendation Banner */}
          <div className="bg-gradient-to-br from-blue-500/10 via-indigo-500/5 to-white rounded-3xl border border-blue-200 p-6 sm:p-8">
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-blue-100">
              <div>
                <span className="text-xs font-bold uppercase tracking-wider text-blue-700 bg-blue-100 px-3 py-1 rounded-full">
                  Recommended Primary Service
                </span>
                <h4 className="text-2xl sm:text-3xl font-extrabold text-[#0f1a3e] mt-2">
                  {result.recommendedService}
                </h4>
              </div>
              <div className="bg-white px-4 py-2.5 rounded-2xl border border-blue-100 shadow-xs text-right">
                <span className="text-[11px] font-semibold text-gray-400 block uppercase">Suggested Care Level</span>
                <span className="text-sm font-bold text-[#3B5BDB]">{result.suggestedCareLevel}</span>
              </div>
            </div>

            <p className="text-sm text-gray-700 leading-relaxed mt-4">
              {result.rationale}
            </p>

            {/* Additional Services Tags */}
            {result.additionalServices && result.additionalServices.length > 0 && (
              <div className="mt-5 pt-4 border-t border-blue-100/70 flex flex-wrap items-center gap-2">
                <span className="text-xs font-semibold text-gray-500 mr-1">Complementary Services:</span>
                {result.additionalServices.map((serv) => (
                  <span
                    key={serv}
                    className="text-xs bg-white text-gray-700 px-3 py-1 rounded-xl border border-blue-100 shadow-2xs font-medium"
                  >
                    + {serv}
                  </span>
                ))}
              </div>
            )}
          </div>

          {/* Timeline & Next Steps Cards */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div className="bg-gray-50/80 rounded-2xl p-5 border border-gray-100">
              <div className="flex items-center gap-2 mb-2">
                <span className="text-xl">📅</span>
                <span className="text-xs font-bold uppercase tracking-wider text-gray-500">Estimated Timeline</span>
              </div>
              <p className="text-base font-bold text-[#0f1a3e]">{result.estimatedTimeline || "2–5 business days"}</p>
              <p className="text-xs text-gray-500 mt-1">Average time from initial inquiry to welcome move-in day.</p>
            </div>

            <div className="bg-gray-50/80 rounded-2xl p-5 border border-gray-100">
              <div className="flex items-center gap-2 mb-2">
                <span className="text-xl">🩺</span>
                <span className="text-xs font-bold uppercase tracking-wider text-gray-500">Recommended Next Step</span>
              </div>
              <p className="text-xs text-gray-700 leading-relaxed font-medium">{result.nextStep}</p>
            </div>
          </div>

          {/* Clinical Disclaimer */}
          <div className="rounded-2xl bg-amber-50/70 border border-amber-200/80 p-4 text-xs text-amber-900 leading-relaxed">
            <span className="font-bold block mb-0.5">⚠️ Clinical Notice & Disclaimer:</span>
            This screening is provided for informational and planning guidance only and does not constitute a formal diagnosis or legally binding care agreement. Official admission eligibility and Level of Care determination require an in-person assessment by our Director of Nursing and clinical team.
          </div>

          {/* Action CTAs */}
          <div className="pt-2 flex flex-col sm:flex-row items-center justify-between gap-4">
            <button
              type="button"
              onClick={handleReset}
              className="text-xs text-gray-500 hover:text-gray-800 font-semibold underline order-2 sm:order-1"
            >
              ← Start Over with Different Answers
            </button>

            <div className="flex flex-wrap items-center gap-3 w-full sm:w-auto order-1 sm:order-2">
              <Link
                to="/contact"
                className="w-full sm:w-auto px-7 py-3.5 bg-[#3B5BDB] hover:bg-[#2f4ac4] text-white font-bold rounded-2xl shadow-lg shadow-blue-500/20 text-center text-sm transition-all"
              >
                Schedule a Free Facility Tour
              </Link>
              <a
                href="tel:9165559000"
                className="w-full sm:w-auto px-5 py-3.5 border-2 border-[#3B5BDB]/20 bg-white hover:bg-blue-50 text-[#3B5BDB] font-bold rounded-2xl text-center text-sm transition-all"
              >
                Call (916) 555-9000
              </a>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
