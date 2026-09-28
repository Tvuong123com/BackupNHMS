import { AlarmClock, Clock, Database, FileText } from "lucide-react";
import type {
  CarePlan,
  CarePlanMetadata,
} from "@/services/care-plan/care-plan-types";

type CarePlanStatisticalProps = {
  carePlans: CarePlan[];
  carePlanMetadata: CarePlanMetadata;
};

export default function CarePlanStatistical({
  carePlans,
  carePlanMetadata,
}: CarePlanStatisticalProps) {
  const statistics = carePlans.reduce(
    (acc, carePlan) => {
      switch (carePlan.status) {
        case "Draft":
          acc.draft++;
          break;

        case "Pending review":
          acc.pendingReview++;
          break;

        case "Review due":
          acc.reviewDue++;
          break;
      }

      return acc;
    },
    {
      draft: 0,
      pendingReview: 0,
      reviewDue: 0,
    },
  );

  const cards = [
    {
      title: "Total plans",
      amount: carePlanMetadata.totalElements,
      icon: Database,
      iconClass: "bg-blue-50 text-blue-600",
    },
    {
      title: "Draft",
      amount: statistics.draft,
      icon: FileText,
      iconClass: "bg-slate-100 text-slate-600",
    },
    {
      title: "Pending Review",
      amount: statistics.pendingReview,
      icon: Clock,
      iconClass: "bg-amber-50 text-amber-600",
    },
    {
      title: "Review Due",
      amount: statistics.reviewDue,
      icon: AlarmClock,
      iconClass: "bg-red-50 text-red-600",
    },
  ];

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
      {cards.map((card) => {
        const Icon = card.icon;
        return (
          <div
            key={card.title}
            className="p-4 bg-white rounded-xl border border-slate-200/80 shadow-xs flex items-center justify-between transition-all hover:border-slate-300 hover:shadow-sm"
          >
            <div className="flex items-center gap-4">
              <div className={`size-11 rounded-full ${card.iconClass} flex items-center justify-center transition-all`}>
                <Icon className="size-5.5" />
              </div>
              <div className="flex flex-col">
                <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider">{card.title}</span>
                <span className="text-2xl font-bold text-slate-900 mt-0.5">{card.amount}</span>
              </div>
            </div>
          </div>
        );
      })}
    </div>
  );
}
