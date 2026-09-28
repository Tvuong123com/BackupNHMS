import type { ColumnDef } from "@tanstack/react-table";
import { MoreHorizontal, BedDouble, UserCheck, LogOut, Building, Calendar } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import type { AdmissionResponse } from "../types/admission-type";
import { DataTableColumnHeader } from "@/components/data-table/data-table-column-header";

export const getAdmissionColumns = (
  onDischarge: (row: AdmissionResponse) => void,
): ColumnDef<AdmissionResponse>[] => {
  return [
    {
      accessorKey: "id",
      header: "Admit Ref",
      cell: ({ row }) => (
        <span className="font-mono text-xs font-bold text-indigo-700 bg-indigo-50 px-2 py-1 rounded border border-indigo-100">
          #ADM-{row.original.id}
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
            <div className="h-9 w-9 rounded-full bg-gradient-to-br from-indigo-500 to-purple-600 flex items-center justify-center text-white font-bold text-xs shadow-sm">
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
      id: "placement",
      header: "Room & Bed Placement",
      cell: ({ row }) => {
        const room = row.original.roomNumber;
        const bed = row.original.bedNumber;
        if (room && bed) {
          return (
            <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-lg bg-indigo-50 text-indigo-800 border border-indigo-200 font-semibold text-xs">
              <BedDouble className="h-3.5 w-3.5 text-indigo-600" />
              <span>Room {room} – Bed {bed}</span>
            </div>
          );
        }
        return (
          <span className="inline-flex items-center gap-1 text-xs font-semibold text-amber-700 bg-amber-50 px-2.5 py-1 rounded border border-amber-200">
            Pending Bed Assignment
          </span>
        );
      },
    },
    {
      accessorKey: "admissionDate",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Admission Date" />
      ),
      cell: ({ row }) => {
        const dateStr = row.original.admissionDate
          ? new Date(row.original.admissionDate).toLocaleDateString("en-US", {
              year: "numeric",
              month: "short",
              day: "numeric",
            })
          : "N/A";
        return (
          <div className="flex items-center gap-1.5 text-xs font-semibold text-gray-700">
            <Calendar className="h-3.5 w-3.5 text-gray-400" />
            <span>{dateStr}</span>
          </div>
        );
      },
    },
    {
      accessorKey: "status",
      header: "Occupancy Status",
      cell: ({ row }) => {
        const isDischarged = !!row.original.dischargeDate || row.original.status === "DISCHARGED";
        if (isDischarged) {
          return (
            <Badge className="bg-gray-100 text-gray-600 border-gray-200 font-semibold px-2.5 py-1 text-xs">
              <UserCheck className="mr-1 h-3.5 w-3.5 text-gray-500" /> Discharged
            </Badge>
          );
        }
        return (
          <Badge className="bg-emerald-50 text-emerald-700 border-emerald-200 font-semibold px-2.5 py-1 text-xs">
            <span className="h-2 w-2 rounded-full bg-emerald-500 mr-1.5 animate-pulse" /> Active Resident
          </Badge>
        );
      },
    },
    {
      id: "actions",
      header: "Action Protocol",
      cell: ({ row }) => {
        const a = row.original;
        const isDischarged = !!a.dischargeDate || a.status === "DISCHARGED";
        if (isDischarged) {
          return <span className="text-xs font-semibold text-gray-400 italic">Discharged History</span>;
        }
        return (
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="outline" size="sm" className="h-8 border-gray-200 hover:bg-gray-100">
                <MoreHorizontal className="h-4 w-4" />
                <span className="ml-1 text-xs font-semibold">Options</span>
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end" className="w-44">
              <DropdownMenuItem
                onClick={() => onDischarge(a)}
                className="text-rose-700 font-semibold cursor-pointer"
              >
                <LogOut className="mr-2 h-4 w-4 text-rose-600" /> Discharge & Release Bed
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        );
      },
    },
  ];
};
