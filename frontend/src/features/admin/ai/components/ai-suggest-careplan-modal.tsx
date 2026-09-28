import React, { useState } from "react";
import { Sparkles, Loader2, CheckCircle2, HeartPulse, UserCheck, ShieldAlert } from "lucide-react";
import { aiService } from "../services/ai-service";
import type { CarePlanSuggestionResponse, SuggestedGoal } from "../types/ai.types";

interface AiSuggestCarePlanModalProps {
  residentName?: string;
  careLevel?: string;
  adlScore?: number;
  diagnoses?: string[];
  recentIncidents?: string[];
  onApplyGoals?: (goals: SuggestedGoal[]) => void;
  className?: string;
}

export const AiSuggestCarePlanModal: React.FC<AiSuggestCarePlanModalProps> = ({
  residentName = "Eleanor Vance",
  careLevel = "Skilled Nursing",
  adlScore = 22,
  diagnoses = ["Tăng huyết áp", "Suy giảm thăng bằng nhẹ"],
  recentIncidents = ["Té ngã tại phòng vệ sinh 2 tuần trước"],
  onApplyGoals,
  className = "",
}) => {
  const [isOpen, setIsOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<CarePlanSuggestionResponse | null>(null);
  const [selectedGoals, setSelectedGoals] = useState<Record<number, boolean>>({});
  const [error, setError] = useState<string | null>(null);

  const handleFetchSuggestions = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await aiService.suggestCarePlan({
        residentName,
        careLevel,
        adlScore,
        diagnoses,
        recentIncidents,
      });
      setResult(data);
      // Mặc định chọn tất cả
      const initialSelected: Record<number, boolean> = {};
      data.suggestedGoals.forEach((_, idx) => {
        initialSelected[idx] = true;
      });
      setSelectedGoals(initialSelected);
    } catch {
      setError("Không thể kết nối với dịch vụ AI gợi ý kế hoạch. Vui lòng kiểm tra lại dịch vụ Ollama local.");
    } finally {
      setLoading(false);
    }
  };

  const handleOpen = () => {
    setIsOpen(true);
    if (!result) {
      handleFetchSuggestions();
    }
  };

  const toggleGoal = (index: number) => {
    setSelectedGoals((prev) => ({
      ...prev,
      [index]: !prev[index],
    }));
  };

  const handleApply = () => {
    if (!result || !onApplyGoals) return;
    const goalsToApply = result.suggestedGoals.filter((_, idx) => selectedGoals[idx]);
    onApplyGoals(goalsToApply);
    setIsOpen(false);
  };

  return (
    <>
      <button
        type="button"
        onClick={handleOpen}
        className={`inline-flex items-center gap-1.5 px-3.5 py-1.5 rounded-lg text-xs font-medium bg-gradient-to-r from-emerald-600 to-teal-700 hover:from-emerald-700 hover:to-teal-800 text-white shadow-sm transition-all duration-150 ${className}`}
      >
        <Sparkles className="w-3.5 h-3.5 text-amber-300" />
        <span>🤖 AI Gợi Ý Care Goals</span>
      </button>

      {isOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-xs animate-in fade-in duration-150">
          <div className="bg-white dark:bg-zinc-900 border border-zinc-200 dark:border-zinc-800 rounded-2xl max-w-2xl w-full p-6 shadow-2xl flex flex-col max-h-[85vh]">
            {/* Header */}
            <div className="flex items-center justify-between border-b border-zinc-100 dark:border-zinc-800 pb-3 mb-4">
              <div className="flex items-center space-x-2.5">
                <div className="p-2 rounded-xl bg-emerald-50 dark:bg-emerald-950/60 text-emerald-600 dark:text-emerald-400">
                  <HeartPulse className="w-5 h-5 text-emerald-600 dark:text-emerald-400" />
                </div>
                <div>
                  <h3 className="text-sm font-semibold text-zinc-900 dark:text-zinc-100">
                    AI Gợi Ý Kế Hoạch Chăm Sóc Cá Nhân Hóa
                  </h3>
                  <p className="text-[11px] text-zinc-500">
                    Dựa trên đánh giá ADL ({adlScore}/28) và hồ sơ sức khỏe của {residentName}
                  </p>
                </div>
              </div>
              <button
                onClick={() => setIsOpen(false)}
                className="text-zinc-400 hover:text-zinc-600 dark:hover:text-zinc-200 text-sm font-bold p-1"
              >
                ✕
              </button>
            </div>

            {/* Resident Brief Tags */}
            <div className="flex flex-wrap gap-2 mb-4 p-3 rounded-xl bg-zinc-50 dark:bg-zinc-800/40 border border-zinc-200/80 dark:border-zinc-800 text-xs">
              <span className="px-2 py-0.5 rounded-md bg-blue-50 dark:bg-blue-950/50 text-blue-700 dark:text-blue-300 font-medium">
                Cấp độ: {careLevel}
              </span>
              <span className="px-2 py-0.5 rounded-md bg-amber-50 dark:bg-amber-950/50 text-amber-700 dark:text-amber-300 font-medium">
                ADL Score: {adlScore}
              </span>
              {recentIncidents.map((inc, i) => (
                <span key={i} className="px-2 py-0.5 rounded-md bg-red-50 dark:bg-red-950/50 text-red-700 dark:text-red-300 flex items-center gap-1 font-medium">
                  <ShieldAlert className="w-3 h-3" /> {inc}
                </span>
              ))}
            </div>

            {/* Content Body */}
            <div className="flex-1 overflow-y-auto space-y-4 pr-1">
              {loading && (
                <div className="py-12 flex flex-col items-center justify-center space-y-3 text-zinc-500">
                  <Loader2 className="w-8 h-8 animate-spin text-emerald-600" />
                  <p className="text-xs">
                    Mô hình AI đang phân tích dữ liệu lâm sàng và thiết lập các can thiệp phù hợp...
                  </p>
                </div>
              )}

              {error && (
                <div className="p-3 bg-red-50 dark:bg-red-950/30 border border-red-200 dark:border-red-900/50 rounded-xl text-red-700 dark:text-red-400 text-xs">
                  {error}
                </div>
              )}

              {result && !loading && (
                <div className="space-y-3.5">
                  <p className="text-xs text-zinc-600 dark:text-zinc-300 italic">
                    "{result.residentSummary}"
                  </p>

                  <div className="space-y-3">
                    {result.suggestedGoals.map((goal, idx) => (
                      <div
                        key={idx}
                        onClick={() => toggleGoal(idx)}
                        className={`p-4 rounded-xl border transition-all cursor-pointer ${
                          selectedGoals[idx]
                            ? "bg-emerald-50/40 dark:bg-emerald-950/20 border-emerald-300 dark:border-emerald-800 shadow-xs"
                            : "bg-white dark:bg-zinc-800/40 border-zinc-200 dark:border-zinc-800 opacity-60"
                        }`}
                      >
                        <div className="flex items-start justify-between gap-3">
                          <div className="flex items-start gap-2.5">
                            <input
                              type="checkbox"
                              checked={!!selectedGoals[idx]}
                              onChange={() => toggleGoal(idx)}
                              className="mt-1 h-4 w-4 rounded border-zinc-300 text-emerald-600 focus:ring-emerald-500"
                            />
                            <div>
                              <div className="flex items-center gap-2">
                                <h4 className="text-xs font-bold text-zinc-900 dark:text-zinc-100">
                                  {goal.goalName}
                                </h4>
                                <span
                                  className={`px-2 py-0.5 rounded text-[10px] font-bold ${
                                    goal.priority === "HIGH"
                                      ? "bg-red-100 text-red-700 dark:bg-red-900/40 dark:text-red-300"
                                      : "bg-blue-100 text-blue-700 dark:bg-blue-900/40 dark:text-blue-300"
                                  }`}
                                >
                                  {goal.priority}
                                </span>
                              </div>
                              <p className="text-xs text-zinc-600 dark:text-zinc-300 mt-1">
                                {goal.description}
                              </p>
                              <p className="text-[11px] text-zinc-500 dark:text-zinc-400 mt-1">
                                💡 <span className="font-semibold">Lý do:</span> {goal.rationale}
                              </p>
                            </div>
                          </div>
                        </div>

                        {/* Interventions */}
                        {goal.interventions && goal.interventions.length > 0 && (
                          <div className="mt-3 pt-2.5 border-t border-zinc-200/60 dark:border-zinc-700/60 pl-6 space-y-1.5">
                            <span className="text-[10px] font-semibold text-zinc-400 uppercase tracking-wider block">
                              Biện pháp can thiệp (Interventions)
                            </span>
                            {goal.interventions.map((itv, itvIdx) => (
                              <div
                                key={itvIdx}
                                className="flex items-center justify-between text-xs text-zinc-700 dark:text-zinc-300"
                              >
                                <span className="flex items-center gap-1.5">
                                  <CheckCircle2 className="w-3.5 h-3.5 text-emerald-500 shrink-0" />
                                  {itv.name}
                                </span>
                                <span className="text-[10px] font-medium px-2 py-0.5 rounded bg-zinc-100 dark:bg-zinc-700 text-zinc-600 dark:text-zinc-300 shrink-0">
                                  {itv.assignedRole}
                                </span>
                              </div>
                            ))}
                          </div>
                        )}
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </div>

            {/* Footer */}
            {result && !loading && (
              <div className="flex items-center justify-between pt-4 mt-4 border-t border-zinc-100 dark:border-zinc-800">
                <span className="text-xs text-zinc-500">
                  Đã chọn {Object.values(selectedGoals).filter(Boolean).length} / {result.suggestedGoals.length} mục tiêu
                </span>
                <div className="flex items-center gap-2">
                  <button
                    type="button"
                    onClick={() => setIsOpen(false)}
                    className="px-3 py-1.5 text-xs text-zinc-600 dark:text-zinc-400 hover:bg-zinc-100 dark:hover:bg-zinc-800 rounded-lg transition-colors"
                  >
                    Hủy bỏ
                  </button>
                  <button
                    type="button"
                    onClick={handleApply}
                    className="inline-flex items-center gap-1.5 px-4 py-1.5 text-xs font-medium text-white bg-emerald-600 hover:bg-emerald-700 rounded-lg shadow-sm transition-colors"
                  >
                    <UserCheck className="w-3.5 h-3.5" />
                    Áp Dụng Vào Kế Hoạch Chăm Sóc
                  </button>
                </div>
              </div>
            )}
          </div>
        </div>
      )}
    </>
  );
};
