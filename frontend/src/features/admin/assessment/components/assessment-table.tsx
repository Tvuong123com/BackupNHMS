import { useState } from "react";
import type { PaginationState, SortingState } from "@tanstack/react-table";
import { getAssessmentColumns } from "./columns";
import { DataTable } from "@/components/data-table/data-table";
import { useAssessmentSearchParams } from "../search-params";
import { Button } from "@/components/ui/button";
import { useDialogStore } from "@/store/use-dialog-store";
import { CreateAssessmentForm } from "./create-assessment-form";
import { DecideAssessmentForm } from "./decide-assessment-form";
import { useAssessments } from "../hooks/use-pending-assessment";
import { Plus, Activity, HeartPulse, ShieldCheck, Search, Sparkles, Award } from "lucide-react";
import { UpdateAssessmentForm } from "./update-assessment-form";
import { AssessmentDetailView } from "./assessment-detail-view";
import { useConfirmStore } from "@/store/use-confirm-store";
import { useDeleteAssessment } from "../hooks/use-delete-assessment";
import { Card, CardContent, CardHeader } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import type { AssessmentResponse } from "../types/assessment-type";

export const AssessmentTable = () => {
  const [{ page, size, sort }, setParams] = useAssessmentSearchParams();
  const { data, isLoading } = useAssessments({ page, size, sort });
  const deleteMutation = useDeleteAssessment();
  const openDialog = useDialogStore((s) => s.open);
  const openConfirm = useConfirmStore((s) => s.open);

  const [search, setSearch] = useState("");

  const pagination: PaginationState = { pageIndex: page, pageSize: size };
  const sorting: SortingState = sort
    ? [{ id: sort.split(",")[0], desc: sort.split(",")[1] === "desc" }]
    : [];

  const rawList: AssessmentResponse[] = data?.data ?? [];

  const filteredData = rawList.filter((item) =>
    !search || (item.residentName || "").toLowerCase().includes(search.toLowerCase())
  );

  const totalAssessments = data?.metadata?.totalElements ?? rawList.length;
  const confirmedCount = rawList.filter((a) => a.status === "CONFIRMED").length;
  const draftCount = rawList.filter((a) => a.status === "DRAFT").length;

  const columns = getAssessmentColumns({
    onDecide: (row) =>
      openDialog({
        title: `Confirm / Override Care Level - Assessment #${row.id}`,
        content: <DecideAssessmentForm row={row} />,
      }),
    onUpdate: (row) =>
      openDialog({
        title: `Update Assessment Scores #${row.id}`,
        content: <UpdateAssessmentForm row={row} />,
      }),
    onViewDetail: (row) =>
      openDialog({
        title: `ADL Assessment #${row.id} Detailed Report`,
        content: <AssessmentDetailView row={row} />,
      }),
    onDelete: (row) =>
      openConfirm({
        title: "Delete Draft Assessment?",
        description: `This will permanently delete the draft assessment for ${row.residentName}.`,
        onConfirm: () => deleteMutation.mutate(row.id),
      }),
  });

  return (
    <div className="space-y-6">
      {/* Header Banner */}
      <div className="bg-gradient-to-r from-emerald-950 via-slate-900 to-indigo-950 p-8 rounded-2xl text-white shadow-md flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-emerald-500/20 text-emerald-300 text-xs font-semibold backdrop-blur-md mb-2">
            <HeartPulse className="h-3.5 w-3.5" /> ADL Functional Capability Index
          </div>
          <h1 className="text-3xl font-extrabold tracking-tight">Health & ADL Assessment</h1>
          <p className="text-emerald-100/80 text-sm mt-1 max-w-xl">
            Evaluate Resident Activities of Daily Living (ADL), score functional dependency, and confirm level of care classifications.
          </p>
        </div>

        <Button
          onClick={() =>
            openDialog({
              title: "Perform New ADL Assessment",
              content: <CreateAssessmentForm />,
            })
          }
          size="lg"
          className="bg-emerald-600 hover:bg-emerald-700 font-bold text-white shadow-lg shadow-emerald-600/30"
        >
          <Plus className="mr-2 h-5 w-5" /> New ADL Assessment
        </Button>
      </div>

      {/* KPI Stats Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="p-4 bg-white rounded-xl border border-slate-200/80 shadow-xs flex items-center justify-between transition-all hover:border-slate-300 hover:shadow-sm">
          <div className="flex items-center gap-4">
            <div className="size-11 rounded-full bg-blue-50 text-blue-600 flex items-center justify-center">
              <Activity className="size-5.5" />
            </div>
            <div className="flex flex-col">
              <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Total Assessments</span>
              <span className="text-2xl font-bold text-slate-900 mt-0.5">{totalAssessments}</span>
            </div>
          </div>
        </div>

        <div className="p-4 bg-white rounded-xl border border-slate-200/80 shadow-xs flex items-center justify-between transition-all hover:border-slate-300 hover:shadow-sm">
          <div className="flex items-center gap-4">
            <div className="size-11 rounded-full bg-emerald-50 text-emerald-600 flex items-center justify-center">
              <ShieldCheck className="size-5.5" />
            </div>
            <div className="flex flex-col">
              <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Confirmed Care Levels</span>
              <span className="text-2xl font-bold text-slate-900 mt-0.5">{confirmedCount}</span>
            </div>
          </div>
        </div>

        <div className="p-4 bg-white rounded-xl border border-slate-200/80 shadow-xs flex items-center justify-between transition-all hover:border-slate-300 hover:shadow-sm">
          <div className="flex items-center gap-4">
            <div className="size-11 rounded-full bg-amber-50 text-amber-600 flex items-center justify-center">
              <Award className="size-5.5" />
            </div>
            <div className="flex flex-col">
              <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Pending Clinical Review</span>
              <span className="text-2xl font-bold text-slate-900 mt-0.5">{draftCount}</span>
            </div>
          </div>
        </div>
      </div>

      {/* Table Card */}
      <Card className="border-gray-100 shadow-sm overflow-hidden bg-white">
        <CardHeader className="border-b border-gray-100 bg-gray-50/50 py-4 px-6 flex items-center justify-between">
          <div className="relative w-full sm:w-72">
            <Search className="absolute left-3 top-2.5 h-4 w-4 text-gray-400" />
            <Input
              placeholder="Search resident name..."
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
                sort: s[0] ? `${s[0].id},${s[0].desc ? "desc" : "asc"}` : "id,desc",
              })
            }
          />
        </CardContent>
      </Card>
    </div>
  );
};
