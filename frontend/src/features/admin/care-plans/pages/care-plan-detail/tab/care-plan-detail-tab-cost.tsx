import { DollarSign, ShieldCheck, HeartPulse, Stethoscope, UserCheck, Calculator, FileCheck2 } from "lucide-react";

interface CarePlanDetailTabCostProps {
  locTier?: number;
  status?: string;
  cycle?: number;
}

export default function CarePlanDetailTabCost({
  locTier = 2,
  status = "ACTIVE",
  cycle = 30,
}: CarePlanDetailTabCostProps) {
  const tier = locTier > 0 ? locTier : 2;

  // Evidence-based pricing tiers standard in elderly care facility management
  const tierRates: Record<number, { name: string; daily: number; cnaHours: number; nurseHours: number; desc: string }> = {
    1: { name: "Tier 1 — Minimal Assistance / Independent", daily: 125, cnaHours: 1.5, nurseHours: 0.5, desc: "Supervised living with basic meal, hydration, and emergency call support." },
    2: { name: "Tier 2 — Moderate ADL Support & Assisted Living", daily: 185, cnaHours: 3.0, nurseHours: 1.0, desc: "Assistance with 2-3 ADLs, scheduled toileting, and routine medication passes." },
    3: { name: "Tier 3 — High Acuity & Memory Care Supervision", daily: 265, cnaHours: 4.5, nurseHours: 2.0, desc: "Comprehensive mobility support, fall prevention sensors, cognitive wandering mitigation." },
    4: { name: "Tier 4 — Total Dependency / Skilled Nursing", daily: 360, cnaHours: 6.0, nurseHours: 3.5, desc: "24/7 skilled nursing surveillance, full transfer assistance, pressure injury prevention." },
  };

  const currentTier = tierRates[tier] || tierRates[2];
  const monthlyDays = cycle > 0 ? cycle : 30;
  const baseCareTotal = currentTier.daily * monthlyDays;
  const medicationAdminFee = 250;
  const therapyRehabFee = 420;
  const totalEstimatedCost = baseCareTotal + medicationAdminFee + therapyRehabFee;

  return (
    <div className="space-y-6 pt-2">
      {/* Cost Overview Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="p-5 rounded-xl border border-slate-200 dark:border-zinc-800 bg-white dark:bg-zinc-900 shadow-2xs">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Level of Care (LOC)</span>
            <span className="px-2 py-0.5 rounded text-[11px] font-bold bg-blue-100 text-blue-800 dark:bg-blue-950/60 dark:text-blue-200">
              LOC Tier {tier}
            </span>
          </div>
          <span className="text-2xl font-bold text-slate-900 dark:text-zinc-100 mt-2 block">
            ${currentTier.daily} <span className="text-xs font-normal text-slate-500">/ day</span>
          </span>
          <p className="text-xs text-slate-500 mt-1">{currentTier.name}</p>
        </div>

        <div className="p-5 rounded-xl border border-slate-200 dark:border-zinc-800 bg-white dark:bg-zinc-900 shadow-2xs">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Billing Cycle ({monthlyDays}d)</span>
            <span className="px-2 py-0.5 rounded text-[11px] font-bold bg-emerald-100 text-emerald-800 dark:bg-emerald-950/60 dark:text-emerald-200">
              Projected
            </span>
          </div>
          <span className="text-2xl font-bold text-emerald-600 mt-2 block">
            ${totalEstimatedCost.toLocaleString()}
          </span>
          <p className="text-xs text-slate-500 mt-1">Includes daily care, pharmacy, and therapy</p>
        </div>

        <div className="p-5 rounded-xl border border-slate-200 dark:border-zinc-800 bg-white dark:bg-zinc-900 shadow-2xs">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Staffing Intensity</span>
            <span className="px-2 py-0.5 rounded text-[11px] font-bold bg-amber-100 text-amber-800 dark:bg-amber-950/60 dark:text-amber-200">
              MDT Allocation
            </span>
          </div>
          <span className="text-2xl font-bold text-blue-600 mt-2 block">
            {(currentTier.cnaHours + currentTier.nurseHours).toFixed(1)} hrs <span className="text-xs font-normal text-slate-500">/ resident / day</span>
          </span>
          <p className="text-xs text-slate-500 mt-1">{currentTier.cnaHours}h CNA + {currentTier.nurseHours}h Licensed Nurse</p>
        </div>
      </div>

      {/* Itemized Cost Breakdown Table */}
      <div className="rounded-xl border border-slate-200 dark:border-zinc-800 bg-white dark:bg-zinc-900 shadow-2xs overflow-hidden">
        <div className="p-4 border-b border-slate-100 dark:border-zinc-800 flex items-center justify-between">
          <div>
            <h3 className="font-semibold text-sm text-slate-900 dark:text-zinc-100 flex items-center gap-2">
              <Calculator className="size-4 text-emerald-600" />
              Itemized Care Plan Cost Breakdown
            </h3>
            <p className="text-xs text-slate-500 mt-0.5">Based on resident assessment clinical acuity and physician orders</p>
          </div>
          <span className="text-xs text-slate-400">Plan Status: <b>{status}</b></span>
        </div>

        <div className="divide-y divide-slate-100 dark:divide-zinc-800 text-xs">
          <div className="p-3.5 flex items-center justify-between">
            <div>
              <p className="font-semibold text-slate-900 dark:text-zinc-100">Daily Level of Care Nursing & Room Support</p>
              <p className="text-slate-500 text-[11px]">{currentTier.desc} (${currentTier.daily} × {monthlyDays} days)</p>
            </div>
            <span className="font-bold text-slate-900 dark:text-zinc-100">${baseCareTotal.toLocaleString()}</span>
          </div>

          <div className="p-3.5 flex items-center justify-between">
            <div>
              <p className="font-semibold text-slate-900 dark:text-zinc-100">Medication Administration & Clinical CDSS Monitoring</p>
              <p className="text-slate-500 text-[11px]">eMAR tracking, Beers Criteria rà soát, và dược sĩ lão khoa tư vấn</p>
            </div>
            <span className="font-bold text-slate-900 dark:text-zinc-100">${medicationAdminFee.toLocaleString()}</span>
          </div>

          <div className="p-3.5 flex items-center justify-between">
            <div>
              <p className="font-semibold text-slate-900 dark:text-zinc-100">Restorative Care, PT / OT Therapy Sessions</p>
              <p className="text-slate-500 text-[11px]">Tập vận động phục hồi chức năng, phòng ngừa co rút và thăng bằng dáng đi</p>
            </div>
            <span className="font-bold text-slate-900 dark:text-zinc-100">${therapyRehabFee.toLocaleString()}</span>
          </div>

          <div className="p-4 bg-slate-50/70 dark:bg-zinc-800/50 flex items-center justify-between text-sm font-bold">
            <span className="text-slate-900 dark:text-zinc-100">Estimated Total Care Plan Cost (Per {monthlyDays}-Day Cycle):</span>
            <span className="text-emerald-700 dark:text-emerald-400 text-base">${totalEstimatedCost.toLocaleString()}</span>
          </div>
        </div>
      </div>

      {/* Insurance & Quality Assurance Note */}
      <div className="p-4 rounded-xl border border-blue-200 bg-blue-50/40 dark:border-blue-900/40 dark:bg-zinc-900/60 text-xs text-blue-900 dark:text-blue-200 flex items-start gap-3">
        <ShieldCheck className="size-5 text-blue-600 shrink-0 mt-0.5" />
        <div className="space-y-1">
          <p className="font-bold">Insurance & Coverage Verification Note</p>
          <p className="text-slate-600 dark:text-zinc-400">
            Care costs are subject to Medicare / Medicaid skilled nursing benefit coverage, long-term care insurance (LTCI), and private payment schedules. Any significant change in resident ADL acuity will trigger an updated Level of Care reassessment.
          </p>
        </div>
      </div>
    </div>
  );
}
