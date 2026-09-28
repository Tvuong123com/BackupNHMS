import { useState } from "react";
import type { PaginationState, SortingState } from "@tanstack/react-table";
import { useAdmissions } from "../hooks/use-admissions";
import { useDischargeAdmission } from "../hooks/use-discharge-admission";
import { useAdmissionSearchParams } from "../search-params";
import { getAdmissionColumns } from "./columns";
import { DataTable } from "@/components/data-table/data-table";
import { Button } from "@/components/ui/button";
import { useDialogStore } from "@/store/use-dialog-store";
import { useConfirmStore } from "@/store/use-confirm-store";
import { CreateAdmissionForm } from "./create-admission-form";
import { Plus, Building2, BedDouble, UserCheck, Calendar, Search, Sparkles, Activity } from "lucide-react";
import { Card, CardContent, CardHeader } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import type { AdmissionResponse } from "../types/admission-type";

export const AdmissionTable = () => {
  const [{ page, size, sort }, setParams] = useAdmissionSearchParams();
  const { data, isLoading } = useAdmissions({ page, size, sort });
  const dischargeMutation = useDischargeAdmission();
  const openDialog = useDialogStore((s) => s.open);
  const openConfirm = useConfirmStore((s) => s.open);

  const [search, setSearch] = useState("");

  const pagination: PaginationState = { pageIndex: page, pageSize: size };
  const sorting: SortingState = sort
    ? [{ id: sort.split(",")[0], desc: sort.split(",")[1] === "desc" }]
    : [];

  const rawList: AdmissionResponse[] = data?.data ?? [];

  const filteredData = rawList.filter((item) =>
    !search || (item.residentName || "").toLowerCase().includes(search.toLowerCase())
  );

  const totalAdmissions = data?.metadata?.totalElements ?? rawList.length;
  const activeAdmissions = rawList.filter((a) => !a.dischargeDate).length;
  const dischargedAdmissions = rawList.filter((a) => !!a.dischargeDate).length;

  const handleDischarge = (row: AdmissionResponse) => {
    openConfirm({
      title: "Discharge Resident & Release Bed?",
      description: `Confirm discharging ${row.residentName}. This will automatically release room ${row.roomNumber || 'assigned'} and set bed status to AVAILABLE.`,
      onConfirm: () =>
        dischargeMutation.mutate({
          id: row.id,
          payload: {
            dischargeDate: new Date().toISOString().split("T")[0],
            dischargeReason: "Routine Discharge",
          },
        }),
    });
  };

  const columns = getAdmissionColumns(handleDischarge);

  return (
    <div className="space-y-6">
      {/* Header Banner */}
      <div className="bg-gradient-to-r from-slate-900 via-indigo-950 to-blue-950 p-8 rounded-2xl text-white shadow-md flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-indigo-500/20 text-indigo-300 text-xs font-semibold backdrop-blur-md mb-2">
            <BedDouble className="h-3.5 w-3.5" /> Admissions & Bed Occupancy Ledger
          </div>
          <h1 className="text-3xl font-extrabold tracking-tight">Admissions & Bed Assignments</h1>
          <p className="text-indigo-100/80 text-sm mt-1 max-w-xl">
            Register approved resident admissions, assign available facility beds, and manage clinical discharge protocols.
          </p>
        </div>

        <Button
          onClick={() =>
            openDialog({
              title: "Process New Admission",
              content: <CreateAdmissionForm />,
            })
          }
          size="lg"
          className="bg-indigo-600 hover:bg-indigo-700 font-bold text-white shadow-lg shadow-indigo-600/30"
        >
          <Plus className="mr-2 h-5 w-5" /> Admit Resident
        </Button>
      </div>

      {/* KPI Stats Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="p-4 bg-white rounded-xl border border-slate-200/80 shadow-xs flex items-center justify-between transition-all hover:border-slate-300 hover:shadow-sm">
          <div className="flex items-center gap-4">
            <div className="size-11 rounded-full bg-blue-50 text-blue-600 flex items-center justify-center">
              <Building2 className="size-5.5" />
            </div>
            <div className="flex flex-col">
              <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Total Ledger Entries</span>
              <span className="text-2xl font-bold text-slate-900 mt-0.5">{totalAdmissions}</span>
            </div>
          </div>
        </div>

        <div className="p-4 bg-white rounded-xl border border-slate-200/80 shadow-xs flex items-center justify-between transition-all hover:border-slate-300 hover:shadow-sm">
          <div className="flex items-center gap-4">
            <div className="size-11 rounded-full bg-emerald-50 text-emerald-600 flex items-center justify-center">
              <BedDouble className="size-5.5" />
            </div>
            <div className="flex flex-col">
              <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Currently Occupied Beds</span>
              <span className="text-2xl font-bold text-slate-900 mt-0.5">{activeAdmissions}</span>
            </div>
          </div>
        </div>

        <div className="p-4 bg-white rounded-xl border border-slate-200/80 shadow-xs flex items-center justify-between transition-all hover:border-slate-300 hover:shadow-sm">
          <div className="flex items-center gap-4">
            <div className="size-11 rounded-full bg-slate-100 text-slate-600 flex items-center justify-center">
              <UserCheck className="size-5.5" />
            </div>
            <div className="flex flex-col">
              <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Completed Discharges</span>
              <span className="text-2xl font-bold text-slate-900 mt-0.5">{dischargedAdmissions}</span>
            </div>
          </div>
        </div>
      </div>

      {/* Main Table Card */}
      <Card className="border-gray-100 shadow-sm overflow-hidden bg-white">
        <CardHeader className="border-b border-gray-100 bg-gray-50/50 py-4 px-6 flex items-center justify-between">
          <div className="relative w-full sm:w-72">
            <Search className="absolute left-3 top-2.5 h-4 w-4 text-gray-400" />
            <Input
              placeholder="Search by resident name..."
              className="pl-9 text-sm"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
          </div>
        </CardHeader>

        <CardContent className="p-0">
          <DataTable
            columns={columns}
            data={filteredData}
            rowCount={data?.metadata?.totalElements ?? filteredData.length}
            isLoading={isLoading}
            pagination={pagination}
            onPaginationChange={(p) =>
              setParams({ page: p.pageIndex, size: p.pageSize })
            }
            sorting={sorting}
            onSortingChange={(s) =>
              setParams({
                sort: s[0]
                  ? `${s[0].id},${s[0].desc ? "desc" : "asc"}`
                  : "admissionDate,desc",
              })
            }
          />
        </CardContent>
      </Card>
    </div>
  );
};
