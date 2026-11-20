import React, { useState } from "react";
import { FallbackNotice } from "./fallback-notice";
import { Link } from "react-router";
import {
  websiteAiService,
  type CareQuizRequest,
  type CareQuizResponse,
} from "../../services/website-ai-service";

const RECIPIENT_OPTIONS = [
  { id: "Parent / In-Law", label: "Parent or In-Law", icon: "👵" },
  { id: "Spouse", label: "My Spouse / Partner", icon: "💑" },
  { id: "Myself", label: "Myself", icon: "🧘" },
  { id: "Relative or Friend", label: "Other Loved One", icon: "🤝" },
];

const NEED_OPTIONS = [
  {
    id: "Memory and cognitive support",
    label: "Memory Care & Dementia Support",
    desc: "Confusion, wandering, or cognitive changes requiring secured care.",
    icon: "🧠",
  },
  {
    id: "Rehabilitation and mobility support",
    label: "Rehabilitation & Physical Therapy",
    desc: "Short-term recovery aiming to regain strength, balance, and independence.",
    icon: "🩹",
  },
  {
    id: "Complex medical needs & 24/7 nursing",
    label: "24/7 Skilled Nursing Care",
    desc: "Comprehensive daily clinical support, injections, and continuous monitoring.",
    icon: "🩺",
  },
  {
    id: "Daily activities and social connection",
    label: "Daily Activities & Social Connection",
    desc: "Activities, companionship, and questions about daily support needs.",
    icon: "🎨",
  },
];

const URGENCY_OPTIONS = [
  { id: "Immediate (within 48h)", label: "Immediate Need", desc: "Urgent admission needed now", icon: "⚡" },
  { id: "Within 1 to 4 weeks", label: "Next Few Weeks", desc: "Actively researching choices", icon: "📅" },
  { id: "Planning ahead", label: "Future Planning", desc: "Gathering information for later", icon: "💭" },
];

export const AiCareQuiz: React.FC = () => {
  const [step, setStep] = useState<number>(1);
  const [recipient, setRecipient] = useState<string>("Parent / In-Law");
  const [primaryNeed, setPrimaryNeed] = useState<string>("Memory and cognitive support");
  const [urgency, setUrgency] = useState<string>("Within 1 to 4 weeks");
  const [mobility, setMobility] = useState<string>("Not sure");
  const [careGoal, setCareGoal] = useState<string>("");

  const [loading, setLoading] = useState<boolean>(false);
  const [result, setResult] = useState<CareQuizResponse | null>(null);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async () => {
    setLoading(true);
    setError(null);

    const payload: CareQuizRequest = {
      recipient,
      primaryNeed,
      careUrgency: urgency,
      mobility,
      symptomsOrGoals: careGoal.trim() ? [careGoal.trim()] : [],
    };

    try {
      const response = await websiteAiService.evaluateCareQuiz(payload);
      setResult(response);
      setStep(4);
    } catch {
      setError("Unable to process assessment right now. Please call us at (916) 555-9000.");
    } finally {
      setLoading(false);
    }
  };

  const handleReset = () => {
    setStep(1);
    setResult(null);
    setError(null);
    setMobility("Not sure");
    setCareGoal("");
  };

  return (
    <div className="bg-white rounded-3xl border border-blue-100 shadow-xl overflow-hidden p-6 lg:p-10 relative">
      {/* Glow decorations */}
      <div className="absolute top-0 right-0 w-64 h-64 bg-blue-100/50 rounded-full blur-3xl pointer-events-none -mr-20 -mt-20"></div>

      {/* Header */}
      <div className="relative z-10 flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-6 border-b border-gray-100">
        <div>
          <div className="flex items-center gap-2 mb-1">
            <span className="text-xs font-bold uppercase tracking-wider bg-blue-100 text-[#3B5BDB] px-3 py-0.5 rounded-full">
              AI Care Navigator
            </span>
            <span className="text-xs text-gray-400">Takes 60 seconds</span>
          </div>
          <h3 className="text-2xl font-bold text-[#0f1a3e]">
            Find the Right Care for Your Loved One
          </h3>
          <p className="text-sm text-gray-500 mt-1">
            Share the main need and a personal goal. AI will explain a service to discuss with our care team.
          </p>
        </div>

        {step <= 3 && (
          <div className="flex items-center gap-1.5 self-start sm:self-center">
            {[1, 2, 3].map((s) => (
              <div
                key={s}
                className={`h-2 rounded-full transition-all ${
                  step === s ? "w-8 bg-[#3B5BDB]" : step > s ? "w-4 bg-green-500" : "w-4 bg-gray-200"
                }`}
              />
            ))}
          </div>
        )}
      </div>

      {/* Step 1: Recipient */}
      {step === 1 && (
        <div className="py-6 space-y-6 relative z-10">
          <div>
            <h4 className="text-base font-semibold text-[#0f1a3e] mb-1">
              Question 1: Who are you seeking care for?
            </h4>
            <p className="text-sm text-gray-500">
              Select the family member or loved one who may need support.
            </p>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3.5">
            {RECIPIENT_OPTIONS.map((r) => (
              <button
                key={r.id}
                type="button"
                onClick={() => setRecipient(r.id)}
                className={`flex items-center gap-3.5 p-4 rounded-2xl border text-left transition-all ${
                  recipient === r.id
                    ? "border-[#3B5BDB] bg-blue-50/70 shadow-sm"
                    : "border-gray-200 hover:border-gray-300 bg-white"
                }`}
              >
                <span className="text-3xl">{r.icon}</span>
                <p
                  className={`text-sm font-semibold flex-1 ${
                    recipient === r.id ? "text-[#3B5BDB]" : "text-gray-800"
                  }`}
                >
                  {r.label}
                </p>
                {recipient === r.id && (
                  <span className="text-[#3B5BDB] font-bold">✓</span>
                )}
              </button>
            ))}
          </div>

          <div className="flex justify-end pt-4">
            <button
              onClick={() => setStep(2)}
              className="px-6 py-3 bg-[#3B5BDB] hover:bg-[#2f4cb5] text-white font-semibold rounded-xl transition-all shadow-md flex items-center gap-2"
            >
              <span>Continue to Step 2</span>
              <span aria-hidden="true">→</span>
            </button>
          </div>
        </div>
      )}

      {/* Step 2: Primary Need */}
      {step === 2 && (
        <div className="py-6 space-y-6 relative z-10">
          <div>
            <h4 className="text-base font-semibold text-[#0f1a3e] mb-1">
              Question 2: What is the primary reason you are looking for care?
            </h4>
            <p className="text-sm text-gray-500">
              Pick the situation that best mirrors what your loved one is currently experiencing.
            </p>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3.5">
            {NEED_OPTIONS.map((n) => (
              <button
                key={n.id}
                type="button"
                onClick={() => setPrimaryNeed(n.id)}
                className={`flex items-start gap-3.5 p-4 rounded-2xl border text-left transition-all ${
                  primaryNeed === n.id
                    ? "border-[#3B5BDB] bg-blue-50/70 shadow-sm"
                    : "border-gray-200 hover:border-gray-300 bg-white"
                }`}
              >
                <span className="text-3xl mt-0.5">{n.icon}</span>
                <div className="flex-1">
                  <p
                    className={`text-sm font-semibold ${
                      primaryNeed === n.id ? "text-[#3B5BDB]" : "text-gray-800"
                    }`}
                  >
                    {n.label}
                  </p>
                  <p className="text-xs text-gray-500 mt-1 leading-relaxed">
                    {n.desc}
                  </p>
                </div>
                {primaryNeed === n.id && (
                  <span className="text-[#3B5BDB] font-bold mt-0.5">✓</span>
                )}
              </button>
            ))}
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
              <span>Continue to Step 3</span>
              <span aria-hidden="true">→</span>
            </button>
          </div>
        </div>
      )}

      {/* Step 3: Urgency */}
      {step === 3 && (
        <div className="py-6 space-y-6 relative z-10">
          <div>
            <h4 className="text-base font-semibold text-[#0f1a3e] mb-1">
              Question 3: How soon are you hoping to start?
            </h4>
            <p className="text-sm text-gray-500">
              This helps us prepare the right onboarding and timeline for your family.
            </p>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-3 gap-3.5">
            {URGENCY_OPTIONS.map((u) => (
              <button
                key={u.id}
                type="button"
                onClick={() => setUrgency(u.id)}
                className={`flex flex-col items-center text-center p-5 rounded-2xl border transition-all ${
                  urgency === u.id
                    ? "border-[#3B5BDB] bg-blue-50/70 shadow-sm"
                    : "border-gray-200 hover:border-gray-300 bg-white"
                }`}
              >
                <span className="text-3xl mb-2">{u.icon}</span>
                <p
                  className={`text-sm font-semibold ${
                    urgency === u.id ? "text-[#3B5BDB]" : "text-gray-800"
                  }`}
                >
                  {u.label}
                </p>
                <p className="text-xs text-gray-500 mt-1">{u.desc}</p>
                {urgency === u.id && (
                  <span className="text-[#3B5BDB] font-bold text-xs mt-2">Selected ✓</span>
                )}
              </button>
            ))}
          </div>

          <div className="grid gap-4 sm:grid-cols-2">
            <div>
              <label htmlFor="navigator-mobility" className="block text-sm font-semibold text-gray-700 mb-2">
                Current mobility
              </label>
              <select id="navigator-mobility" value={mobility} onChange={(event) => setMobility(event.target.value)}
                className="w-full rounded-xl border border-gray-200 bg-white px-4 py-3 text-sm">
                <option>Not sure</option>
                <option>Independent</option>
                <option>Uses a walking aid</option>
                <option>Needs hands-on help</option>
                <option>Uses a wheelchair</option>
              </select>
            </div>
            <div>
              <label htmlFor="navigator-goal" className="block text-sm font-semibold text-gray-700 mb-2">
                Main concern or goal (optional)
              </label>
              <input id="navigator-goal" value={careGoal} onChange={(event) => setCareGoal(event.target.value)}
                maxLength={100} placeholder="For example: walk safely to the dining room"
                className="w-full rounded-xl border border-gray-200 bg-white px-4 py-3 text-sm" />
            </div>
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
              aria-busy={loading}
              className="px-8 py-3 bg-gradient-to-r from-[#1e3a8a] to-[#3B5BDB] hover:from-[#172554] hover:to-[#2f4cb5] text-white font-bold rounded-xl transition-all shadow-lg flex items-center gap-2 disabled:opacity-50"
            >
              {/* Stable element children survive text replacements by browser translation. */}
              <span aria-hidden="true" className="flex h-4 w-4 items-center justify-center">
                {loading ? (
                  <span className="h-4 w-4 rounded-full border-2 border-white/30 border-t-white animate-spin" />
                ) : (
                  <span>✨</span>
                )}
              </span>
              <span>{loading ? "Navigating with AI..." : "Get Recommendation"}</span>
            </button>
          </div>
        </div>
      )}

      {/* Step 4: Result */}
      {step === 4 && result && (
        <div className="py-6 space-y-6 relative z-10">
          {result.fallback && <FallbackNotice />}
          <div className="bg-gradient-to-r from-[#0f1a3e] via-[#1e3a8a] to-[#3B5BDB] text-white rounded-2xl p-6 shadow-lg">
            <span className="text-xs font-bold uppercase tracking-wider bg-white/20 px-3 py-1 rounded-full text-blue-100">
              {result.badge || "Recommended Match"}
            </span>
            <h4 className="text-2xl font-bold mt-3">{result.recommendedService}</h4>
            <p className="text-blue-100 text-sm mt-1">{result.headline}</p>
          </div>

          <div className="bg-[#f8f9ff] rounded-2xl p-5 border border-blue-100">
            <h5 className="text-xs font-bold uppercase tracking-wider text-gray-400 mb-2">
              Why this program fits
            </h5>
            <p className="text-sm text-gray-700 leading-relaxed">{result.summary}</p>
          </div>

          {result.keyBenefits && result.keyBenefits.length > 0 && (
            <div>
              <h5 className="text-xs font-bold uppercase tracking-wider text-gray-400 mb-3">
                Key Program Features
              </h5>
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
                {result.keyBenefits.map((benefit, i) => (
                  <div
                    key={i}
                    className="p-3 bg-white rounded-xl border border-gray-100 shadow-sm flex items-start gap-2"
                  >
                    <span className="text-green-500 font-bold text-sm">✓</span>
                    <span className="text-xs text-gray-700 font-medium">{benefit}</span>
                  </div>
                ))}
              </div>
            </div>
          )}

          {result.questionsForTeam && result.questionsForTeam.length > 0 && (
            <div className="rounded-2xl border border-blue-100 bg-blue-50/50 p-5">
              <h5 className="mb-2 text-xs font-bold uppercase tracking-wider text-blue-800">Questions to ask the care team</h5>
              <ul className="list-disc space-y-1 pl-5 text-sm text-gray-700">
                {result.questionsForTeam.map((question, index) => <li key={index}>{question}</li>)}
              </ul>
            </div>
          )}

          <div className="bg-amber-50 rounded-xl p-4 border border-amber-200/60 text-xs text-amber-900 leading-relaxed">
            <strong>Next Step:</strong>{" "}<span>{result.recommendedAction}</span>
          </div>

          <div className="flex flex-col sm:flex-row items-center justify-between gap-4 pt-2">
            <button
              onClick={handleReset}
              className="text-xs text-gray-500 hover:text-gray-700 underline font-medium"
            >
              ← Retake Quiz
            </button>

            <div className="flex flex-wrap gap-3 w-full sm:w-auto">
              <Link
                to={result.serviceUrl || "/services"}
                className="flex-1 sm:flex-initial px-5 py-2.5 border border-gray-300 text-gray-700 font-semibold rounded-xl hover:bg-gray-50 text-xs text-center transition-all"
              >
                <span>{`View ${result.recommendedService} Page`}</span>
              </Link>
              <Link
                to="/admissions"
                className="flex-1 sm:flex-initial px-6 py-2.5 bg-[#3B5BDB] hover:bg-[#2f4cb5] text-white font-bold rounded-xl text-xs text-center transition-all shadow-md"
              >
                Start Pre-Screening →
              </Link>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
