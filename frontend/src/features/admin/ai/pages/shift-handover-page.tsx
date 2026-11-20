import { ChevronRight } from "lucide-react";
import { AiShiftSummaryPanel } from "../components/ai-shift-summary-panel";

export default function ShiftHandoverPage() {
  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-1">
        <div className="flex items-center gap-1.5 text-xs text-slate-400 dark:text-slate-500 font-medium">
          <span>Clinical Operations</span>
          <ChevronRight className="size-3 text-slate-300 dark:text-slate-700" />
          <span className="text-slate-600 dark:text-slate-300">Shift Handover</span>
        </div>
        <h1 className="text-2xl font-bold text-slate-900 dark:text-white tracking-tight">
          Clinical Shift Handover (SBAR Protocol)
        </h1>
        <p className="text-sm text-slate-400 dark:text-slate-500 font-medium">
          Evidence-based shift handoffs integrating Situation, Background, Assessment, and Recommendation (SBAR).
        </p>
      </div>
      <AiShiftSummaryPanel />
    </div>
  );
}
