import { useState } from "react";
import type { PaginationState, SortingState } from "@tanstack/react-table";
import { getScreeningColumns } from "./columns";
import { useScreenings } from "../hooks/use-screenings";
import { useDecideScreening } from "../hooks/use-decide-screening";
import { DataTable } from "@/components/data-table/data-table";
import { useScreeningSearchParams } from "../search-params";
import { Button } from "@/components/ui/button";
import { useDialogStore } from "@/store/use-dialog-store";
import { useConfirmStore } from "@/store/use-confirm-store";
import { CreateScreeningForm } from "./create-screening-form";
import { Plus, UserCheck, Clock, CheckCircle2, XCircle, Search, Filter, Sparkles, FileText } from "lucide-react";
import type { PreResponse } from "../types/pre-admission-type";
import { useDeleteScreening } from "../hooks/use-delete-screening";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Badge } from "@/components/ui/badge";

export const ScreeningTable = () => {
  const [{ page, size, sort }, setParams] = useScreeningSearchParams();
  const { data, isLoading } = useScreenings({ page, size, sort });
  const decideMutation = useDecideScreening();
  const deleteMutation = useDeleteScreening();
  const openDialog = useDialogStore((s) => s.open);
  const openConfirm = useConfirmStore((s) => s.open);

  const [search, setSearch] = useState("");
  const [statusFilter, setStatusFilter] = useState<string>("ALL");

  const pagination: PaginationState = { pageIndex: page, pageSize: size };
  const sorting: SortingState = sort
    ? [{ id: sort.split(",")[0], desc: sort.split(",")[1] === "desc" }]
    : [];

  const rawList: PreResponse[] = data?.data ?? [];

  // Filter list by search & status
  const filteredData = rawList.filter((item) => {
    const matchesSearch = !search || (item.residentName || "").toLowerCase().includes(search.toLowerCase());
    const matchesStatus = statusFilter === "ALL" || item.status === statusFilter;
    return matchesSearch && matchesStatus;
  });

  const totalCount = data?.metadata?.totalElements ?? rawList.length;
  const draftCount = rawList.filter((r) => r.status === "DRAFT").length;
  const completedCount = rawList.filter((r) => r.status === "COMPLETED").length;
  const rejectedCount = rawList.filter((r) => r.status === "REJECTED").length;

  const handleDecide = (
    row: PreResponse,
    decision: "COMPLETED" | "REJECTED",
  ) => {
    openConfirm({
      title: decision === "COMPLETED" ? "Approve Screening?" : "Reject Screening?",
      description: `Confirm ${decision === "COMPLETED" ? "approving" : "rejecting"} the pre-admission screening for ${row.residentName}.`,
      onConfirm: () =>
        decideMutation.mutate({ id: row.id, payload: { status: decision } }),
    });
  };

  const handleDelete = (row: PreResponse) => {
    openConfirm({
      title: "Delete Screening Draft?",
      description: `This will permanently delete the draft screening for ${row.residentName}.`,
      onConfirm: () => deleteMutation.mutate(row.id),
    });
  };

  const columns = getScreeningColumns({
    onDecide: handleDecide,
    onDelete: handleDelete,
  });

  return (
    <div className="space-y-6">
      {/* Header Banner */}
      <div className="bg-gradient-to-r from-blue-900 via-indigo-950 to-slate-900 p-8 rounded-2xl text-white shadow-md flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-blue-500/20 text-blue-300 text-xs font-semibold backdrop-blur-md mb-2">
            <Sparkles className="h-3.5 w-3.5" /> Intake & Placement Protocol
          </div>
          <h1 className="text-3xl font-extrabold tracking-tight">Pre-Admission Screenings</h1>
          <p className="text-blue-100/80 text-sm mt-1 max-w-xl">
            Evaluate prospective resident health history, clinical eligibility, and care suitability prior to formal room assignment.
          </p>
        </div>

        <Button
          onClick={() =>
            openDialog({
              title: "Create Pre-Admission Screening",
              content: <CreateScreeningForm />,
            })
          }
          size="lg"
          className="bg-blue-500 hover:bg-blue-600 font-bold text-white shadow-lg shadow-blue-500/30"
        >
          <Plus className="mr-2 h-5 w-5" /> Conduct Screening
        </Button>
      </div>

      {/* KPI Stats Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="p-4 bg-white rounded-xl border border-slate-200/80 shadow-xs flex items-center justify-between transition-all hover:border-slate-300 hover:shadow-sm">
          <div className="flex items-center gap-4">
            <div className="size-11 rounded-full bg-blue-50 text-blue-600 flex items-center justify-center">
              <FileText className="size-5.5" />
            </div>
            <div className="flex flex-col">
              <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Total Evaluated</span>
              <span className="text-2xl font-bold text-slate-900 mt-0.5">{totalCount}</span>
            </div>
          </div>
        </div>

        <div className="p-4 bg-white rounded-xl border border-slate-200/80 shadow-xs flex items-center justify-between transition-all hover:border-slate-300 hover:shadow-sm">
          <div className="flex items-center gap-4">
            <div className="size-11 rounded-full bg-amber-50 text-amber-600 flex items-center justify-center">
              <Clock className="size-5.5" />
            </div>
            <div className="flex flex-col">
              <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Pending Review</span>
              <span className="text-2xl font-bold text-slate-900 mt-0.5">{draftCount}</span>
            </div>
          </div>
        </div>

        <div className="p-4 bg-white rounded-xl border border-slate-200/80 shadow-xs flex items-center justify-between transition-all hover:border-slate-300 hover:shadow-sm">
          <div className="flex items-center gap-4">
            <div className="size-11 rounded-full bg-emerald-50 text-emerald-600 flex items-center justify-center">
              <CheckCircle2 className="size-5.5" />
            </div>
            <div className="flex flex-col">
              <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Approved & Completed</span>
              <span className="text-2xl font-bold text-slate-900 mt-0.5">{completedCount}</span>
            </div>
          </div>
        </div>

        <div className="p-4 bg-white rounded-xl border border-slate-200/80 shadow-xs flex items-center justify-between transition-all hover:border-slate-300 hover:shadow-sm">
          <div className="flex items-center gap-4">
            <div className="size-11 rounded-full bg-rose-50 text-rose-600 flex items-center justify-center">
              <XCircle className="size-5.5" />
            </div>
            <div className="flex flex-col">
              <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Ineligible / Rejected</span>
              <span className="text-2xl font-bold text-slate-900 mt-0.5">{rejectedCount}</span>
            </div>
          </div>
        </div>
      </div>

      {/* Filter & Table Container */}
      <Card className="border-gray-100 shadow-sm overflow-hidden bg-white">
        <CardHeader className="border-b border-gray-100 bg-gray-50/50 py-4 px-6 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
          <div className="relative w-full sm:w-72">
            <Search className="absolute left-3 top-2.5 h-4 w-4 text-gray-400" />
            <Input
              placeholder="Search resident name..."
              className="pl-9 text-sm"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
          </div>

          <div className="flex items-center gap-2">
            <Filter className="h-4 w-4 text-gray-400" />
            <span className="text-xs font-semibold text-gray-500">Filter Status:</span>
            <div className="flex items-center gap-1.5">
              {["ALL", "DRAFT", "COMPLETED", "REJECTED"].map((st) => (
                <Button
                  key={st}
                  size="sm"
                  variant={statusFilter === st ? "default" : "outline"}
                  onClick={() => setStatusFilter(st)}
                  className={`text-xs h-8 px-3 font-semibold ${
                    statusFilter === st ? "bg-blue-600 text-white" : "border-gray-200 text-gray-600"
                  }`}
                >
                  {st}
                </Button>
              ))}
            </div>
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
                  : "createdAt,desc",
              })
            }
          />
        </CardContent>
      </Card>
    </div>
  );
};
