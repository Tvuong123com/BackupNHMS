import type { ColumnDef } from "@tanstack/react-table";
import { MoreHorizontal, Activity, Eye, Edit, CheckSquare, Trash2, HeartPulse, ShieldCheck, Clock } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import type { AssessmentResponse } from "../types/assessment-type";
import { DataTableColumnHeader } from "@/components/data-table/data-table-column-header";

interface Props {
  onDecide: (row: AssessmentResponse) => void;
  onUpdate: (row: AssessmentResponse) => void;
  onViewDetail: (row: AssessmentResponse) => void;
  onDelete: (row: AssessmentResponse) => void;
}

export const getAssessmentColumns = ({
  onDecide,
  onUpdate,
  onViewDetail,
  onDelete,
}: Props): ColumnDef<AssessmentResponse>[] => {
  return [
    {
      accessorKey: "id",
      header: "Assessment Ref",
      cell: ({ row }) => (
        <span className="font-mono text-xs font-bold text-emerald-700 bg-emerald-50 px-2 py-1 rounded border border-emerald-100">
          #ASM-{row.original.id}
        </span>
      ),
    },
    {
      accessorKey: "residentName",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Resident Profile" />
      ),
      cell: ({ row }) => {
        const name = row.original.residentName || "Unknown Resident";
        const initials = name.split(" ").map(n => n[0]).join("").slice(0, 2).toUpperCase();
        return (
          <div className="flex items-center gap-3">
            <div className="h-9 w-9 rounded-full bg-gradient-to-br from-emerald-500 to-teal-600 flex items-center justify-center text-white font-bold text-xs shadow-sm">
              {initials}
            </div>
            <div>
              <span className="font-bold text-gray-900 text-sm block">{name}</span>
              <span className="text-xs text-gray-400 font-medium">Resident ID #{row.original.residentId}</span>
            </div>
          </div>
        );
      },
    },
    {
      accessorKey: "adlTotalScore",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="ADL Index Score" />
      ),
      cell: ({ row }) => {
        const score = row.original.adlTotalScore ?? 0;
        // ADL Score visualization
        let colorClass = "text-emerald-700 bg-emerald-50 border-emerald-200";
        if (score > 15) colorClass = "text-rose-700 bg-rose-50 border-rose-200";
        else if (score > 8) colorClass = "text-amber-700 bg-amber-50 border-amber-200";

        return (
          <div className="flex items-center gap-2">
            <div className={`px-2.5 py-1 rounded-md border font-mono font-extrabold text-xs ${colorClass}`}>
              {score} Points
            </div>
            <span className="text-[11px] text-gray-400 font-medium">
              ({row.original.details?.length || 0} ADL Metrics)
            </span>
          </div>
        );
      },
    },
    {
      accessorKey: "status",
      header: "Assessment Status",
      cell: ({ row }) => {
        const st = row.original.status;
        if (st === "CONFIRMED" || st === "COMPLETED") {
          return (
            <Badge className="bg-emerald-50 text-emerald-700 border-emerald-200 font-semibold px-2.5 py-1 text-xs">
              <ShieldCheck className="mr-1 h-3.5 w-3.5 text-emerald-600" /> Confirmed
            </Badge>
          );
        }
        return (
          <Badge className="bg-amber-50 text-amber-800 border-amber-200 font-semibold px-2.5 py-1 text-xs">
            <Clock className="mr-1 h-3.5 w-3.5 text-amber-600 animate-pulse" /> Clinical Draft
          </Badge>
        );
      },
    },
    {
      id: "actions",
      header: "Clinical Options",
      cell: ({ row }) => {
        const a = row.original;
        return (
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="outline" size="sm" className="h-8 border-gray-200 hover:bg-gray-100">
                <MoreHorizontal className="h-4 w-4" />
                <span className="ml-1 text-xs font-semibold">Manage</span>
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end" className="w-48">
              <DropdownMenuItem onClick={() => onViewDetail(a)} className="font-semibold cursor-pointer">
                <Eye className="mr-2 h-4 w-4 text-blue-600" /> View ADL Metrics
              </DropdownMenuItem>
              {a.status === "DRAFT" && (
                <>
                  <DropdownMenuItem onClick={() => onUpdate(a)} className="font-semibold cursor-pointer">
                    <Edit className="mr-2 h-4 w-4 text-amber-600" /> Update ADL Scores
                  </DropdownMenuItem>
                  <DropdownMenuItem onClick={() => onDecide(a)} className="text-emerald-700 font-semibold cursor-pointer">
                    <CheckSquare className="mr-2 h-4 w-4 text-emerald-600" /> Confirm Care Level
                  </DropdownMenuItem>
                  <DropdownMenuItem
                    onClick={() => onDelete(a)}
                    className="text-red-600 font-semibold cursor-pointer border-t border-gray-100 mt-1 pt-1.5"
                  >
                    <Trash2 className="mr-2 h-4 w-4 text-red-500" /> Delete Draft
                  </DropdownMenuItem>
                </>
              )}
            </DropdownMenuContent>
          </DropdownMenu>
        );
      },
    },
  ];
};
