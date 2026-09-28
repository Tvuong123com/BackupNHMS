import React from "react";
import { Activity, ShieldAlert, AlertTriangle, ArrowRight, User, CheckCircle2, ChevronRight } from "lucide-react";
import { Link } from "react-router";

interface RiskResident {
  id: string;
  name: string;
  room: string;
  age: number;
  careLevel: string;
  riskLevel: "HIGH" | "MODERATE" | "LOW";
  riskScore: number; // 0 - 10
  primaryFactors: string[];
  recommendedAction: string;
}

export const AiRiskAlertWidget: React.FC<{ className?: string }> = ({ className = "" }) => {
  const highRiskResidents: RiskResident[] = [
    {
      id: "res-1",
      name: "Eleanor Vance",
      room: "Room 204",
      age: 82,
      careLevel: "Skilled Nursing",
      riskLevel: "HIGH",
      riskScore: 8.8,
      primaryFactors: [
        "2 documented fall incidents in past 30 days",
        "4-point decline in functional ADL mobility score",
        "Recent titration of antihypertensive regimen",
      ],
      recommendedAction: "Increase night-shift rounding interval; schedule urgent physical therapy (PT) gait re-assessment.",
    },
    {
      id: "res-2",
      name: "Arthur Pendelton",
      room: "Room 108",
      age: 89,
      careLevel: "Memory Care",
      riskLevel: "HIGH",
      riskScore: 8.4,
      primaryFactors: [
        "Sundowning and nighttime psychomotor agitation",
        "Repeated attempts to access perimeter exit (Elopement Alert)",
      ],
      recommendedAction: "Activate RFID wander-guard system; assign 30-min structured sensory check-ins.",
    },
    {
      id: "res-3",
      name: "Mildred Hayes",
      room: "Room 315",
      age: 77,
      careLevel: "Assisted Living",
      riskLevel: "MODERATE",
      riskScore: 5.6,
      primaryFactors: [
        "Dietary intake below 50% for 3 consecutive days",
        "Unplanned weight loss of 1.2 kg over 7 days",
      ],
      recommendedAction: "Consult clinical dietitian; initiate fluid intake and meal completion tracking.",
    },
  ];

  return (
    <div
      className={`rounded-2xl border border-slate-200 dark:border-zinc-800 bg-white dark:bg-zinc-900 p-5 shadow-xs flex flex-col space-y-4 ${className}`}
    >
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 pb-3 border-b border-slate-100 dark:border-zinc-800">
        <div className="flex items-start sm:items-center space-x-3">
          <div className="p-2.5 rounded-xl bg-rose-50 dark:bg-rose-950/50 text-rose-600 dark:text-rose-400 border border-rose-100 dark:border-rose-900/40">
            <Activity className="w-5 h-5" />
          </div>
          <div>
            <div className="flex items-center space-x-2">
              <h3 className="text-sm font-semibold text-slate-900 dark:text-zinc-100 tracking-tight">
                Clinical Risk & Resident Surveillance
              </h3>
              <span className="inline-flex items-center px-2 py-0.5 rounded-md text-[10px] font-semibold bg-slate-100 dark:bg-zinc-800 text-slate-600 dark:text-zinc-400 border border-slate-200 dark:border-zinc-700">
                Automated Acuity
              </span>
            </div>
            <p className="text-xs text-slate-500 dark:text-zinc-400 mt-0.5">
              Continuous monitoring for fall risks, cognitive wandering, and acute health deterioration
            </p>
          </div>
        </div>
        <div className="flex items-center gap-2">
          <span className="text-xs font-semibold px-2.5 py-1 rounded-lg bg-rose-50 dark:bg-rose-950/50 text-rose-700 dark:text-rose-300 border border-rose-200 dark:border-rose-900/40">
            2 High Acuity Alerts
          </span>
          <Link
            to="/admin/care-plans"
            className="text-xs font-medium text-blue-600 hover:text-blue-700 dark:text-blue-400 flex items-center gap-1 ml-1"
          >
            Care Plans <ChevronRight className="w-3.5 h-3.5" />
          </Link>
        </div>
      </div>

      {/* Cards list */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-3.5">
        {highRiskResidents.map((res) => (
          <div
            key={res.id}
            className={`p-4 rounded-xl border flex flex-col justify-between space-y-3 transition-all ${
              res.riskLevel === "HIGH"
                ? "bg-rose-50/20 dark:bg-rose-950/10 border-rose-200 dark:border-rose-900/30"
                : "bg-amber-50/20 dark:bg-amber-950/10 border-amber-200 dark:border-amber-900/30"
            }`}
          >
            <div>
              <div className="flex items-center justify-between">
                <span className="text-xs font-bold text-slate-900 dark:text-zinc-100 flex items-center gap-1.5">
                  <User className="w-3.5 h-3.5 text-slate-400" />
                  {res.name}
                </span>
                <span
                  className={`text-[10px] font-bold px-2 py-0.5 rounded-md ${
                    res.riskLevel === "HIGH"
                      ? "bg-rose-100 text-rose-800 dark:bg-rose-900/40 dark:text-rose-300"
                      : "bg-amber-100 text-amber-800 dark:bg-amber-900/40 dark:text-amber-300"
                  }`}
                >
                  Score {res.riskScore}/10
                </span>
              </div>
              <p className="text-[11px] text-slate-500 dark:text-zinc-400 mt-1">
                {res.room} • {res.careLevel} • {res.age} yrs
              </p>

              {/* Factors */}
              <div className="mt-3 space-y-1.5">
                <span className="text-[10px] text-slate-400 dark:text-zinc-500 uppercase font-semibold tracking-wider block">
                  Clinical Indicators:
                </span>
                {res.primaryFactors.map((fac, i) => (
                  <p key={i} className="text-xs text-slate-700 dark:text-zinc-300 flex items-start gap-1.5 leading-snug">
                    <span className="text-rose-500 shrink-0 mt-0.5">•</span>
                    <span>{fac}</span>
                  </p>
                ))}
              </div>
            </div>

            <div className="pt-2.5 border-t border-slate-200/70 dark:border-zinc-800 text-[11px]">
              <span className="font-semibold text-slate-700 dark:text-zinc-200 block mb-0.5">
                Recommended Action:
              </span>
              <p className="text-slate-600 dark:text-zinc-400 line-clamp-2 leading-relaxed">
                {res.recommendedAction}
              </p>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

