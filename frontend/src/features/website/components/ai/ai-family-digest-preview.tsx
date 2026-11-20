import React, { useState } from "react";

interface DigestSample {
  id: string;
  residentName: string;
  room: string;
  date: string;
  vitals: string;
  meals: string;
  activities: string;
  mood: string;
  aiSummary: string;
}

const SAMPLE_DIGESTS: DigestSample[] = [
  {
    id: "eleanor",
    residentName: "Eleanor Davis",
    room: "Room 104-B (Memory Care)",
    date: "Today, 6:00 PM",
    vitals: "BP: 122/78 mmHg · HR: 74 bpm · Temp: 98.4°F",
    meals: "Breakfast: 90% (Oatmeal & Berries) · Lunch: 85% · Dinner: 95%",
    activities: "Morning Sensory Garden Walk & Afternoon Watercolor Painting",
    mood: "Cheerful, highly social, and engaged with her roommate Margaret",
    aiSummary:
      "Today Eleanor had a wonderfully cheerful day! She ate nearly all of her favorite oatmeal with blueberries for breakfast and enjoyed a guided 20-minute sensory walk through our secure courtyard. In the afternoon, she created a vibrant watercolor flower painting and spent 45 minutes chatting happily with Margaret about their favorite spring gardens. Her vitals remained perfectly stable, and she received her afternoon medications on schedule. No pain or disorientation reported.",
  },
  {
    id: "robert",
    residentName: "Robert Chen",
    room: "Room 218-A (Rehabilitation)",
    date: "Today, 5:30 PM",
    vitals: "BP: 128/82 mmHg · HR: 68 bpm · SpO2: 98%",
    meals: "Breakfast: 100% · Lunch: 90% · Dinner: 80%",
    activities: "Physical Therapy (Gait Training) & Board Games Club",
    mood: "Determined, motivated, in high spirits following PT milestones",
    aiSummary:
      "Robert achieved an exciting milestone today! During morning Physical Therapy, he walked 145 feet with a rolling walker under minimal supervision — a 30% improvement from last week. He ate well throughout the day and enjoyed a spirited game of chess in the common lounge with CNA David. His physical therapist noted excellent balance recovery. He is resting comfortably this evening and eager for tomorrow's session.",
  },
];

export const AiFamilyDigestPreview: React.FC = () => {
  const [selectedId, setSelectedId] = useState<string>("eleanor");
  const selected = SAMPLE_DIGESTS.find((d) => d.id === selectedId) || SAMPLE_DIGESTS[0];

  return (
    <div className="bg-gradient-to-br from-white via-rose-50/20 to-blue-50/20 rounded-3xl border border-red-100 shadow-xl overflow-hidden p-6 lg:p-10">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-6 border-b border-gray-100">
        <div className="flex items-center gap-3.5">
          <div className="w-12 h-12 rounded-2xl bg-gradient-to-br from-[#DC2626] to-[#F43F5E] flex items-center justify-center text-white text-2xl shadow-md">
            📰
          </div>
          <div>
            <div className="flex items-center gap-2">
              <span className="text-xs font-bold uppercase tracking-wider bg-red-100 text-[#DC2626] px-2.5 py-0.5 rounded-full">
                AI Family Portal Preview
              </span>
              <span className="text-xs text-gray-400">Peace of mind, 24/7</span>
            </div>
            <h3 className="text-xl font-bold text-[#0f1a3e] mt-0.5">
              Daily Family Update Digest
            </h3>
          </div>
        </div>

        {/* Resident switcher */}
        <div className="flex items-center gap-2 bg-gray-100 p-1 rounded-2xl">
          {SAMPLE_DIGESTS.map((d) => (
            <button
              key={d.id}
              onClick={() => setSelectedId(d.id)}
              className={`px-3.5 py-1.5 rounded-xl text-xs font-semibold transition-all ${
                selectedId === d.id
                  ? "bg-white text-[#DC2626] shadow-sm"
                  : "text-gray-600 hover:text-gray-900"
              }`}
            >
              {d.residentName}
            </button>
          ))}
        </div>
      </div>

      {/* Main card */}
      <div className="py-6 space-y-6">
        <div>
          <h4 className="text-base font-semibold text-[#0f1a3e] mb-1">
            How ElderCare AI Keeps Families Informed
          </h4>
          <p className="text-sm text-gray-500">
            Every evening, our AI transforms complex clinical notes and nurse shift logs into an easy-to-read, compassionate family digest sent directly to authorized family members.
          </p>
        </div>

        {/* Digest Preview Card */}
        <div className="bg-white rounded-2xl border-2 border-red-100 p-6 shadow-md relative overflow-hidden">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between pb-4 border-b border-gray-100 gap-2">
            <div>
              <span className="text-xs text-red-600 font-semibold tracking-wide uppercase">
                Daily Care Digest
              </span>
              <h5 className="text-lg font-bold text-[#0f1a3e]">{selected.residentName}</h5>
              <p className="text-xs text-gray-400">{selected.room} · {selected.date}</p>
            </div>
            <span className="px-3 py-1 bg-green-50 text-green-700 border border-green-200 rounded-full text-xs font-semibold self-start sm:self-center">
              ✓ Good Health & Stable
            </span>
          </div>

          {/* Quick Metrics */}
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-3 my-4">
            <div className="p-3 bg-gray-50 rounded-xl">
              <span className="text-[11px] font-bold text-gray-400 uppercase tracking-wide block mb-1">
                Vital Signs
              </span>
              <span className="text-xs text-gray-800 font-medium">{selected.vitals}</span>
            </div>
            <div className="p-3 bg-gray-50 rounded-xl">
              <span className="text-[11px] font-bold text-gray-400 uppercase tracking-wide block mb-1">
                Nutrition & Meals
              </span>
              <span className="text-xs text-gray-800 font-medium">{selected.meals}</span>
            </div>
            <div className="p-3 bg-gray-50 rounded-xl">
              <span className="text-[11px] font-bold text-gray-400 uppercase tracking-wide block mb-1">
                Activities & Mood
              </span>
              <span className="text-xs text-gray-800 font-medium">{selected.mood}</span>
            </div>
          </div>

          {/* AI Narrative */}
          <div className="bg-gradient-to-br from-red-50/60 to-rose-50/40 rounded-xl p-4 border border-red-100">
            <div className="flex items-center gap-1.5 mb-2 text-xs font-bold text-[#DC2626] uppercase tracking-wide">
              <span>✨</span>
              <span>AI Warm Summary for Family</span>
            </div>
            <p className="text-sm text-gray-800 leading-relaxed italic">
              "{selected.aiSummary}"
            </p>
          </div>

          <div className="mt-4 flex items-center justify-between text-xs text-gray-400 pt-2 border-t border-gray-50">
            <span>Prepared by ElderCare Clinical AI Assistant</span>
            <span className="text-[#DC2626] font-semibold">Available via Family Portal App</span>
          </div>
        </div>
      </div>
    </div>
  );
};
