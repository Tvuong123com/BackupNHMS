import React from "react";
import { AlertTriangle, ShieldAlert, Sparkles, ArrowRight, User, CheckCircle } from "lucide-react";

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
      room: "Phòng 204",
      age: 82,
      careLevel: "Skilled Nursing",
      riskLevel: "HIGH",
      riskScore: 8.8,
      primaryFactors: [
        "2 sự cố té ngã trong 30 ngày",
        "Điểm ADL giảm 4 điểm",
        "Có điều chỉnh đơn thuốc hạ áp",
      ],
      recommendedAction: "Tăng cường giám sát ban đêm, chỉ định đánh giá vật lý trị liệu (PT) khẩn.",
    },
    {
      id: "res-2",
      name: "Arthur Pendelton",
      room: "Phòng 108",
      age: 89,
      careLevel: "Memory Care",
      riskLevel: "HIGH",
      riskScore: 8.4,
      primaryFactors: [
        "Biểu hiện bồn chồn (agitation) ca đêm",
        "Xu hướng tìm lối thoát hiểm (elopement risk)",
      ],
      recommendedAction: "Kiểm tra định kỳ 30 phút/lần, kích hoạt vòng đeo tay định vị an toàn.",
    },
    {
      id: "res-3",
      name: "Mildred Hayes",
      room: "Phòng 315",
      age: 77,
      careLevel: "Assisted Living",
      riskLevel: "MODERATE",
      riskScore: 5.6,
      primaryFactors: ["Khẩu phần ăn dưới 50% trong 3 ngày", "Sụt 1.2kg trong tuần"],
      recommendedAction: "Hội chẩn chuyên viên dinh dưỡng, theo dõi bù nước và sinh tố.",
    },
  ];

  return (
    <div
      className={`rounded-2xl border border-zinc-200 dark:border-zinc-800 bg-white dark:bg-zinc-900/60 p-5 shadow-xs flex flex-col space-y-4 ${className}`}
    >
      {/* Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-2.5">
          <div className="p-2 rounded-xl bg-amber-50 dark:bg-amber-950/50 text-amber-600 dark:text-amber-400">
            <ShieldAlert className="w-5 h-5" />
          </div>
          <div>
            <div className="flex items-center space-x-2">
              <h3 className="text-sm font-semibold text-zinc-900 dark:text-zinc-100">
                AI Cảnh Báo Nguy Cơ Sức Khỏe & Sự Cố
              </h3>
              <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-[10px] font-bold bg-indigo-50 dark:bg-indigo-950/60 text-indigo-600 dark:text-indigo-400 border border-indigo-200 dark:border-indigo-800">
                <Sparkles className="w-3 h-3" /> Predictive AI
              </span>
            </div>
            <p className="text-xs text-zinc-500">
              Mô hình dự đoán các biến cố sức khỏe và nguy cơ té ngã / đi lạc trong 7-14 ngày tới
            </p>
          </div>
        </div>
        <span className="text-xs font-semibold px-2.5 py-1 rounded-lg bg-red-50 dark:bg-red-950/50 text-red-600 dark:text-red-400 border border-red-200 dark:border-red-900/40">
          2 Cư Dân Nguy Cơ Cao
        </span>
      </div>

      {/* Cards list */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-3">
        {highRiskResidents.map((res) => (
          <div
            key={res.id}
            className={`p-4 rounded-xl border flex flex-col justify-between space-y-3 transition-all ${
              res.riskLevel === "HIGH"
                ? "bg-red-50/30 dark:bg-red-950/10 border-red-200 dark:border-red-900/40"
                : "bg-amber-50/30 dark:bg-amber-950/10 border-amber-200 dark:border-amber-900/40"
            }`}
          >
            <div>
              <div className="flex items-center justify-between">
                <span className="text-xs font-bold text-zinc-900 dark:text-zinc-100 flex items-center gap-1.5">
                  <User className="w-3.5 h-3.5 text-zinc-500" />
                  {res.name}
                </span>
                <span
                  className={`text-[10px] font-bold px-2 py-0.5 rounded-md ${
                    res.riskLevel === "HIGH"
                      ? "bg-red-100 text-red-700 dark:bg-red-900/40 dark:text-red-300"
                      : "bg-amber-100 text-amber-700 dark:bg-amber-900/40 dark:text-amber-300"
                  }`}
                >
                  Score {res.riskScore}/10
                </span>
              </div>
              <p className="text-[11px] text-zinc-500 mt-0.5">
                {res.room} • {res.careLevel} • {res.age} tuổi
              </p>

              {/* Factors */}
              <div className="mt-2.5 space-y-1">
                <span className="text-[10px] text-zinc-400 uppercase font-semibold block">
                  Yếu tố cảnh báo:
                </span>
                {res.primaryFactors.map((fac, i) => (
                  <p key={i} className="text-xs text-zinc-700 dark:text-zinc-300 flex items-start gap-1">
                    <span className="text-red-500 mt-0.5">•</span>
                    <span>{fac}</span>
                  </p>
                ))}
              </div>
            </div>

            <div className="pt-2 border-t border-zinc-200/60 dark:border-zinc-800 text-[11px]">
              <span className="font-semibold text-zinc-800 dark:text-zinc-200 block mb-0.5">
                💡 Khuyến nghị xử lý:
              </span>
              <p className="text-zinc-600 dark:text-zinc-400 line-clamp-2">
                {res.recommendedAction}
              </p>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
