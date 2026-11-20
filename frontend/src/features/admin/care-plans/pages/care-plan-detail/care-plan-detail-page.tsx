import { Link, useParams } from "react-router";
import { useQuery } from "@tanstack/react-query";
import CarePlanNavBar from "../../components/care-plan-detail/care-plan-detail-tabs";
import { getCarePlanDetail } from "@/services/care-plan/care-plan-services";
import { AiSuggestCarePlanModal } from "@/features/admin/ai/components/ai-suggest-careplan-modal";
import type { SuggestedGoal } from "@/features/admin/ai/types/ai.types";
import { apiClient } from "@/lib/api-client";
import { apiErrorMessage } from "@/components/common/lookup-select";
import { useCurrentUser } from "@/features/auth/hooks/use-current-user";
import { isClinicalReviewer } from "@/features/auth/role-permissions";

export default function CarePlanDetailPage() {
  const { id } = useParams();
  const planId = Number(id);
  const validId = Number.isSafeInteger(planId) && planId > 0;
  const { data: user } = useCurrentUser();
  const query = useQuery({ queryKey: ["care-plan", planId], queryFn: async () => (await getCarePlanDetail(planId)).data, enabled: validId, retry: false });
  const carePlan = query.data;
  const applySuggestedGoals = async (goals: SuggestedGoal[]) => {
    await apiClient.post(`/care-plans/${planId}/goals/batch`, goals.map(goal => ({
      careGoalName: goal.goalName || "Care Goal",
      careGoalDescription: goal.description || goal.goalName || "Evidence-based clinical care objective",
      interventions: (goal.interventions || []).map(item => ({
        title: item.name || "Actionable nursing intervention",
        assignedRole: item.assignedRole || "NURSE",
      })),
    })));
    await query.refetch();
  };
  if (!validId) return <p role="alert">Invalid care plan ID. <Link to="/admin/care-plans" className="underline">Back to Care Plans</Link></p>;
  if (query.isPending) return <p role="status">Loading care plan…</p>;
  if (query.isError || !carePlan) return <div role="alert" className="space-y-3"><p>{apiErrorMessage(query.error, "Could not load this care plan.")}</p><button className="underline" onClick={() => void query.refetch()}>Retry care plan</button><Link className="block underline" to="/admin/care-plans">Back to Care Plans</Link></div>;
  const needsUpdate = String(carePlan.status).toUpperCase().replaceAll(" ", "_") === "NEEDS_UPDATE";
  return <div className="space-y-5">
    <nav aria-label="Breadcrumb" className="text-sm text-muted-foreground"><Link to="/admin/care-plans" className="text-blue-600 underline">Care Plans</Link> / Care Plan #{carePlan.id} / <Link to={`/admin/residents/${carePlan.resident.id}`} className="text-blue-600 underline">{carePlan.resident.fullname}</Link></nav>
    <div className="rounded-xl border p-5 space-y-3 bg-white dark:bg-zinc-900 shadow-2xs">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
        <div>
          <h1 className="text-2xl font-bold">Care Plan Detail · {carePlan.resident.fullname}</h1>
          <p className="text-sm text-muted-foreground mt-0.5">Resident #{carePlan.resident.id} · DOB {carePlan.resident.dateOfBirth || "Not recorded"} · Room {carePlan.definition?.room || "Not assigned"} · Bed {carePlan.definition?.bed || "Not assigned"}</p>
        </div>
        <div className="shrink-0">
          <AiSuggestCarePlanModal
            key={carePlan.id}
            residentId={carePlan.resident.id}
            residentName={carePlan.resident.fullname}
            careLevel={carePlan.locTier > 0 ? `Level ${carePlan.locTier}` : undefined}
            onApplyGoals={applySuggestedGoals}
          />
        </div>
      </div>
      <div className="flex gap-2 flex-wrap items-center pt-1 border-t border-slate-100 dark:border-zinc-800">
        <span className={`rounded-full px-3 py-1 text-xs font-semibold ${needsUpdate ? "bg-amber-100 text-amber-800" : "bg-blue-100 text-blue-800"}`}>{carePlan.status}</span>
        <span className="rounded-full border px-3 py-1 text-xs font-medium">{carePlan.locTier > 0 ? `LOC Tier ${carePlan.locTier}` : "LOC not recorded"}</span>
        <span className="text-xs text-muted-foreground ml-auto">Created by {carePlan.createdBy?.fullname || "Clinical Coordinator"} · {carePlan.createdAt ? new Date(carePlan.createdAt).toLocaleString() : "Active Record"}</span>
      </div>
    </div>
    <CarePlanNavBar
      listCareGoals={carePlan.goals ?? []}
      cycle={carePlan.cycle}
      lastReview={carePlan.lastReviewedDateTime ?? "null"}
      nextReview={carePlan.nextReviewDateTime ?? "null"}
      locTier={carePlan.locTier}
      status={carePlan.status}
      createdAt={carePlan.createdAt}
    />
  </div>;
}
