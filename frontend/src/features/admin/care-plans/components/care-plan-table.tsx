import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Link } from "react-router";
import type {
  CarePlan,
  ResidentDefinition,
  ResidentInfo,
} from "@/services/care-plan/care-plan-types";
import { formatOffsetDateTimeToDate } from "../utils/time-utils";
import { Loader2 } from "lucide-react";

type CarePlanTableProps = {
  carePlans: CarePlan[];
  isLoading: boolean;
};

export default function CarePlanTable(props: CarePlanTableProps) {
  const getResidentInfo = (carePlan: CarePlan) => {
    const definition: ResidentDefinition = carePlan.definition;
    const residentInfo: ResidentInfo = carePlan.resident;

    return (
      residentInfo.fullname + " · Room " + definition.room + definition.bed
    );
  };

  const getStatusBadgeStyle = (status: string) => {
    switch (status.toLowerCase()) {
      case "active":
        return "bg-emerald-50 text-emerald-700 border-emerald-200/60";
      case "draft":
        return "bg-slate-100 text-slate-700 border-slate-200";
      case "pending review":
        return "bg-amber-50 text-amber-700 border-amber-200/60";
      case "review due":
        return "bg-orange-50 text-orange-700 border-orange-200/60";
      default:
        return "bg-blue-50 text-blue-700 border-blue-200/60";
    }
  };

  const getLOCTierBadgeStyle = (tier: number) => {
    switch (tier) {
      case 1:
        return "bg-blue-50 text-blue-700 border-blue-200/60";
      case 2:
        return "bg-purple-50 text-purple-700 border-purple-200/60";
      case 3:
        return "bg-indigo-50 text-indigo-700 border-indigo-200/60";
      default:
        return "bg-slate-50 text-slate-700 border-slate-200/60";
    }
  };

  if (props.isLoading) {
    return (
      <div className="flex justify-center items-center h-80">
        <Loader2 className="h-8 w-8 animate-spin text-blue-600" />
      </div>
    );
  }

  return (
    <div className="bg-white rounded-xl border border-slate-200/80 shadow-xs overflow-hidden">
      <Table className="min-w-full">
        <TableHeader className="bg-slate-50/70 border-b border-slate-200/80">
          <TableRow>
            <TableHead className="py-3 px-4 text-left text-[11px] uppercase tracking-wider font-semibold text-slate-500">
              Resident
            </TableHead>
            <TableHead className="py-3 px-4 text-left text-[11px] uppercase tracking-wider font-semibold text-slate-500">
              LOC Tier
            </TableHead>
            <TableHead className="py-3 px-4 text-left text-[11px] uppercase tracking-wider font-semibold text-slate-500">
              Status
            </TableHead>
            <TableHead className="py-3 px-4 text-left text-[11px] uppercase tracking-wider font-semibold text-slate-500">
              Last Review
            </TableHead>
            <TableHead className="py-3 px-4 text-left text-[11px] uppercase tracking-wider font-semibold text-slate-500">
              Next Review
            </TableHead>
            <TableHead className="py-3 px-4 text-left text-[11px] uppercase tracking-wider font-semibold text-slate-500">
              Assigned
            </TableHead>
            <TableHead className="py-3 px-4 text-right text-[11px] uppercase tracking-wider font-semibold text-slate-500 pr-6">
              Action
            </TableHead>
          </TableRow>
        </TableHeader>
        <TableBody className="divide-y divide-slate-100">
          {props.carePlans.map((carePlan) => (
            <TableRow key={carePlan.id} className="hover:bg-slate-50/50 transition-colors">
              <TableCell className="py-3.5 px-4 font-semibold text-slate-900 text-sm">
                {getResidentInfo(carePlan)}
              </TableCell>

              <TableCell className="py-3.5 px-4">
                <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold border ${getLOCTierBadgeStyle(carePlan.LOCTier)}`}>
                  Tier {carePlan.LOCTier}
                </span>
              </TableCell>

              <TableCell className="py-3.5 px-4">
                <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold border ${getStatusBadgeStyle(carePlan.status)}`}>
                  {carePlan.status}
                </span>
              </TableCell>

              <TableCell className="py-3.5 px-4 text-slate-600 text-sm">
                {formatOffsetDateTimeToDate(carePlan.lastReviewedDateTime) || "—"}
              </TableCell>

              <TableCell className="py-3.5 px-4 text-sm">
                <span className={
                  carePlan.nextReviewDateTime === "Overdue"
                    ? "text-red-600 font-semibold"
                    : "text-slate-600"
                }>
                  {formatOffsetDateTimeToDate(carePlan.nextReviewDateTime) || "—"}
                </span>
              </TableCell>

              <TableCell className="py-3.5 px-4 text-slate-600 text-sm">
                {carePlan.createdBy.fullname}
              </TableCell>

              <TableCell className="py-3.5 px-4 text-right pr-6">
                <Link
                  to={`/admin/care-plans/${carePlan.id}`}
                  className="text-blue-600 hover:text-blue-800 font-bold text-sm transition-colors"
                >
                  View
                </Link>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </div>
  );
}
