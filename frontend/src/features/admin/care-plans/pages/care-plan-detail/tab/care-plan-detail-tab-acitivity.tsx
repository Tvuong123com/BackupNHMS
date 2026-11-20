import React from "react";
import type { CarePlanGoal } from "@/services/care-plan/care-plan-types";
import { CheckCircle2, Clock, Activity, UserCheck, Stethoscope, Dumbbell, ShieldAlert, Calendar } from "lucide-react";

interface CarePlanDetailTabActivityProps {
  listCareGoals: CarePlanGoal[];
  reviewCycle?: number;
  lastReview?: string;
  nextReview?: string;
  createdAt?: string;
  status?: string;
}

export default function CarePlainDetailTabActivity({
  listCareGoals = [],
  reviewCycle = 30,
  lastReview = "Not recorded",
  nextReview = "Pending clinical review",
  createdAt,
  status = "ACTIVE",
}: CarePlanDetailTabActivityProps) {
  // Flatten interventions with parent goal info
  const allInterventions = listCareGoals.flatMap((goal) =>
    (goal.interventions || []).map((intervention) => ({
      ...intervention,
      goalTitle: goal.title,
      goalStatus: goal.status,
    }))
  );

  const roleIcons: Record<string, React.ReactNode> = {
    CNA: <UserCheck className="size-4 text-blue-600" />,
    NURSE: <Stethoscope className="size-4 text-emerald-600" />,
    PT: <Dumbbell className="size-4 text-amber-600" />,
    OT: <Activity className="size-4 text-purple-600" />,
    DOCTOR: <CheckCircle2 className="size-4 text-rose-600" />,
  };

  const roleCounts = allInterventions.reduce<Record<string, number>>((acc, item) => {
    const role = (item.assignedRole || "NURSE").toUpperCase();
    acc[role] = (acc[role] || 0) + 1;
    return acc;
  }, {});

  return (
    <div className="space-y-6 pt-2">
      {/* Overview Stat Cards */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        <div className="p-4 rounded-xl border border-slate-200 dark:border-zinc-800 bg-white dark:bg-zinc-900 shadow-2xs">
          <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider block">Care Goals</span>
          <span className="text-2xl font-bold text-slate-900 dark:text-zinc-100 mt-1 block">
            {listCareGoals.length} Active
          </span>
          <p className="text-[11px] text-slate-400 mt-1">Under continuous clinical review</p>
        </div>

        <div className="p-4 rounded-xl border border-slate-200 dark:border-zinc-800 bg-white dark:bg-zinc-900 shadow-2xs">
          <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider block">Interventions</span>
          <span className="text-2xl font-bold text-blue-600 mt-1 block">
            {allInterventions.length} Tasks
          </span>
          <p className="text-[11px] text-slate-400 mt-1">Assigned across nursing roles</p>
        </div>

        <div className="p-4 rounded-xl border border-slate-200 dark:border-zinc-800 bg-white dark:bg-zinc-900 shadow-2xs">
          <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider block">Review Cycle</span>
          <span className="text-2xl font-bold text-emerald-600 mt-1 block">
            {reviewCycle} Days
          </span>
          <p className="text-[11px] text-slate-400 mt-1">Next: {nextReview}</p>
        </div>

        <div className="p-4 rounded-xl border border-slate-200 dark:border-zinc-800 bg-white dark:bg-zinc-900 shadow-2xs">
          <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider block">Plan Status</span>
          <span className="text-2xl font-bold text-amber-600 mt-1 block">
            {status}
          </span>
          <p className="text-[11px] text-slate-400 mt-1">Last reviewed: {lastReview}</p>
        </div>
      </div>

      {/* Role Distribution Pills */}
      <div className="p-4 rounded-xl border border-slate-200 dark:border-zinc-800 bg-white dark:bg-zinc-900 shadow-2xs">
        <h3 className="text-xs font-bold uppercase tracking-wider text-slate-600 dark:text-zinc-300 mb-3">
          Clinical Intervention Distribution by Staff Role
        </h3>
        <div className="flex flex-wrap gap-2.5">
          {["CNA", "NURSE", "PT", "OT", "DOCTOR"].map((role) => {
            const count = roleCounts[role] || 0;
            return (
              <div
                key={role}
                className="flex items-center gap-2 px-3 py-1.5 rounded-lg border border-slate-200 dark:border-zinc-800 bg-slate-50/60 dark:bg-zinc-800/40 text-xs font-medium"
              >
                {roleIcons[role] || <Activity className="size-4" />}
                <span className="font-semibold text-slate-700 dark:text-zinc-200">{role}:</span>
                <span className="px-1.5 py-0.5 rounded-md bg-blue-100 dark:bg-blue-900/50 text-blue-800 dark:text-blue-200 text-[11px] font-bold">
                  {count} action(s)
                </span>
              </div>
            );
          })}
        </div>
      </div>

      {/* List of Interventions with Goal Association */}
      <div className="space-y-3">
        <h3 className="text-sm font-bold text-slate-900 dark:text-zinc-100 flex items-center gap-2">
          <Activity className="size-4 text-blue-600" />
          Active Interventions & Daily Task Allocation
        </h3>

        {allInterventions.length === 0 ? (
          <div className="p-8 text-center rounded-xl border border-dashed border-slate-200 dark:border-zinc-800 text-sm text-slate-500">
            No active interventions recorded in this care plan yet. Click <b>"Suggest Care Goals"</b> above to draft AI-recommended nursing interventions.
          </div>
        ) : (
          <div className="grid gap-3 md:grid-cols-2">
            {allInterventions.map((item, idx) => {
              const role = (item.assignedRole || "NURSE").toUpperCase();
              return (
                <div
                  key={idx}
                  className="p-4 rounded-xl border border-slate-200 dark:border-zinc-800 bg-white dark:bg-zinc-900 shadow-2xs hover:shadow-xs transition-shadow space-y-2"
                >
                  <div className="flex items-start justify-between gap-2">
                    <span className="inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-md bg-blue-50 dark:bg-blue-950/40 border border-blue-200 dark:border-blue-800 text-blue-700 dark:text-blue-300 font-bold text-xs">
                      {roleIcons[role]}
                      {role}
                    </span>
                    <span className="text-[10px] font-semibold uppercase tracking-wider px-2 py-0.5 rounded bg-emerald-50 text-emerald-700 border border-emerald-200">
                      SCHEDULED
                    </span>
                  </div>

                  <p className="text-sm font-semibold text-slate-900 dark:text-zinc-100">
                    {item.title}
                  </p>

                  <p className="text-xs text-slate-500 dark:text-zinc-400">
                    Parent Goal: <span className="font-medium text-slate-700 dark:text-zinc-300">{item.goalTitle}</span>
                  </p>
                </div>
              );
            })}
          </div>
        )}
      </div>

      {/* Activity Timeline Audit */}
      <div className="p-4 rounded-xl border border-slate-200 dark:border-zinc-800 bg-slate-50/50 dark:bg-zinc-900/50 space-y-2 text-xs">
        <h4 className="font-bold uppercase tracking-wider text-slate-600 dark:text-zinc-300 flex items-center gap-1.5">
          <Calendar className="size-3.5" /> Care Plan Activity Audit Trail
        </h4>
        <div className="space-y-1.5 text-slate-600 dark:text-zinc-400">
          <p>• <b>Creation:</b> Plan initialized {createdAt ? new Date(createdAt).toLocaleString() : "Initial admission assessment"}</p>
          <p>• <b>Clinical Review Cadence:</b> Every {reviewCycle} days with multidisciplinary team (MDT)</p>
          <p>• <b>Next Scheduled Evaluation:</b> {nextReview}</p>
        </div>
      </div>
    </div>
  );
}
