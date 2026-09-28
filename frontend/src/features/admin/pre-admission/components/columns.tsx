import type { ColumnDef } from "@tanstack/react-table";
import { MoreHorizontal, CheckCircle2, Clock, XCircle, Trash2, ShieldAlert, User } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import type { PreResponse } from "../types/pre-admission-type";
import { DataTableColumnHeader } from "@/components/data-table/data-table-column-header";

interface Props {
  onDecide: (row: PreResponse, decision: "COMPLETED" | "REJECTED") => void;
  onDelete: (row: PreResponse) => void;
}

export const getScreeningColumns = ({
  onDecide,
  onDelete,
}: Props): ColumnDef<PreResponse>[] => {
  return [
    {
      accessorKey: "id",
      header: "Ref ID",
      cell: ({ row }) => (
        <span className="font-mono text-xs font-bold text-gray-500 bg-gray-100 px-2 py-1 rounded">
          #PAS-{row.original.id}
        </span>
      ),
    },
    {
      accessorKey: "residentName",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Resident Candidate" />
      ),
      cell: ({ row }) => {
        const name = row.original.residentName || "Unknown Resident";
        const initials = name.split(" ").map(n => n[0]).join("").slice(0, 2).toUpperCase();
        return (
          <div className="flex items-center gap-3">
            <div className="h-9 w-9 rounded-full bg-gradient-to-br from-blue-500 to-indigo-600 flex items-center justify-center text-white font-bold text-xs shadow-sm">
              {initials}
            </div>
            <div>
              <span className="font-bold text-gray-900 text-sm block">{name}</span>
              <span className="text-xs text-gray-400 font-medium">Resident #{row.original.residentId}</span>
            </div>
          </div>
        );
      },
    },
    {
      accessorKey: "status",
      header: "Screening Status",
      cell: ({ row }) => {
        const st = row.original.status;
        if (st === "COMPLETED") {
          return (
            <Badge className="bg-emerald-50 text-emerald-700 border-emerald-200 font-semibold px-2.5 py-1 text-xs">
              <CheckCircle2 className="mr-1 h-3.5 w-3.5 text-emerald-600" /> Approved & Completed
            </Badge>
          );
        }
        if (st === "REJECTED") {
          return (
            <Badge className="bg-rose-50 text-rose-700 border-rose-200 font-semibold px-2.5 py-1 text-xs">
              <XCircle className="mr-1 h-3.5 w-3.5 text-rose-600" /> Ineligible / Rejected
            </Badge>
          );
        }
        return (
          <Badge className="bg-amber-50 text-amber-800 border-amber-200 font-semibold px-2.5 py-1 text-xs">
            <Clock className="mr-1 h-3.5 w-3.5 text-amber-600 animate-pulse" /> Pending Review
          </Badge>
        );
      },
    },
    {
      accessorKey: "createdAt",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Submitted Date" />
      ),
      cell: ({ row }) => {
        const dateStr = row.original.createdAt
          ? new Date(row.original.createdAt).toLocaleDateString("en-US", {
              year: "numeric",
              month: "short",
              day: "numeric",
            })
          : "N/A";
        return <span className="text-xs font-semibold text-gray-600">{dateStr}</span>;
      },
    },
    {
      id: "actions",
      header: "Protocol Action",
      cell: ({ row }) => {
        const s = row.original;
        if (s.status !== "DRAFT") {
          return <span className="text-xs font-semibold text-gray-400 italic">Locked Protocol</span>;
        }
        return (
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="outline" size="sm" className="h-8 border-gray-200 hover:bg-gray-100">
                <MoreHorizontal className="h-4 w-4" />
                <span className="ml-1 text-xs font-semibold">Action</span>
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end" className="w-40">
              <DropdownMenuItem
                onClick={() => onDecide(s, "COMPLETED")}
                className="text-emerald-700 font-semibold cursor-pointer"
              >
                <CheckCircle2 className="mr-2 h-4 w-4 text-emerald-600" /> Approve Candidate
              </DropdownMenuItem>
              <DropdownMenuItem
                onClick={() => onDecide(s, "REJECTED")}
                className="text-rose-700 font-semibold cursor-pointer"
              >
                <XCircle className="mr-2 h-4 w-4 text-rose-600" /> Reject Candidate
              </DropdownMenuItem>
              <DropdownMenuItem
                onClick={() => onDelete(s)}
                className="text-red-600 font-semibold cursor-pointer border-t border-gray-100 mt-1 pt-1.5"
              >
                <Trash2 className="mr-2 h-4 w-4 text-red-500" /> Delete Draft
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        );
      },
    },
  ];
};
