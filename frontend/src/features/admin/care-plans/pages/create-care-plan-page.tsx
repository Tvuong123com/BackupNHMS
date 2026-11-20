import { useEffect, useRef, useState } from "react";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { Link, useNavigate } from "react-router";
import { Button } from "@/components/ui/button";
import { LookupSelect, apiErrorMessage } from "@/components/common/lookup-select";
import { createCarePlan, getCarePlanResidents } from "@/services/care-plan/care-plan-services";
import { aiService } from "@/features/admin/ai/services/ai-service";
import type { SuggestedGoal } from "@/features/admin/ai/types/ai.types";

const emptyGoal = (): SuggestedGoal => ({ goalName: "", description: "", rationale: "", priority: "MEDIUM", interventions: [{ name: "", assignedRole: "NURSE" }] });
const field = "w-full rounded-lg border bg-background p-2 text-sm mt-1";
export default function CreateCarePlanPage() {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const residents = useQuery({ queryKey: ["care-plan-residents"], queryFn: getCarePlanResidents, retry: false });
  const [residentId, setResidentId] = useState("");
  const [goals, setGoals] = useState<SuggestedGoal[]>([emptyGoal()]);
  const [summary, setSummary] = useState("");
  const [aiState, setAiState] = useState("");
  const [generating, setGenerating] = useState(false);
  const [saving, setSaving] = useState(false);
  const [reviewed, setReviewed] = useState(false);
  const [error, setError] = useState("");
  const [purpose, setPurpose] = useState<"DRAFT" | "NEED_REVIEW">("DRAFT");
  const controller = useRef<AbortController | null>(null);
  useEffect(() => () => controller.current?.abort(), []);
  const updateGoal = (index: number, patch: Partial<SuggestedGoal>) => { setGoals(items => items.map((item, i) => i === index ? { ...item, ...patch } : item)); setReviewed(false); };
  const generate = async () => {
    if (!residentId) return;
    setError(""); setGenerating(true); setReviewed(false);
    controller.current = new AbortController();
    try {
      const result = await aiService.suggestCarePlan({ residentId: Number(residentId), existingGoals: goals.map(item => item.goalName).filter(Boolean) }, controller.current.signal);
      setSummary(result.residentSummary);
      setAiState(result.generationMethod === "AI" ? "AI suggestions — review each goal and intervention before saving." : "AI suggestions are unavailable. You can write verified goals manually.");
      if (result.suggestedGoals.length) setGoals(result.suggestedGoals);
    } catch (err) { if (!controller.current.signal.aborted) setError(apiErrorMessage(err, "Could not generate AI suggestions. Check the configured AI provider or enter goals manually.")); }
    finally { setGenerating(false); }
  };
  const valid = goals.length > 0 && goals.every(goal => goal.goalName.trim() && goal.description.trim() && goal.interventions.length > 0 && goal.interventions.every(item => item.name.trim()));
  return <div className="max-w-5xl space-y-5 pb-8">
    <Link to="/admin/care-plans" className="text-sm text-blue-600 underline">Back to Care Plans</Link>
    <div><h1 className="text-2xl font-bold">New Care Plan</h1><p className="text-sm text-muted-foreground mt-1">Create a draft manually or ask AI to propose goals from this resident's chart. Saving does not activate a plan or create scheduled tasks.</p></div>
    <form className="space-y-5" onSubmit={async event => {
      event.preventDefault(); if (!residentId || !valid || !reviewed || saving) return;
      setSaving(true); setError("");
      try { const saved = await createCarePlan(Number(residentId), goals, purpose); await queryClient.invalidateQueries({ queryKey: ["care-plans"] }); navigate(`/admin/care-plans/${saved.id}`); }
      catch (err) { setError(apiErrorMessage(err, "Could not save the care plan. Check the resident chart and reviewed goals.")); }
      finally { setSaving(false); }
    }}>
      <div className="rounded-xl border p-4 space-y-3">
        <LookupSelect id="care-plan-resident" label="Resident" value={residentId} onChange={value => { setResidentId(value); setGoals([emptyGoal()]); setSummary(""); setAiState(""); setReviewed(false); setError(""); }}
          options={(residents.data ?? []).map(item => ({ value: String(item.id), label: `${item.name} · #${item.id}` }))} loading={residents.isPending} failed={residents.isError}
          retry={() => void residents.refetch()} disabled={generating || saving} emptyMessage="No active resident has an unlocked chart. Review Residents first." />
        <Button type="button" disabled={!residentId || generating || saving} onClick={() => void generate()}>{generating ? "Generating AI goals…" : "Generate AI suggestions"}</Button>
        {aiState && <p role="status" className="text-sm text-amber-700">{aiState}</p>}
        {summary && <p className="text-sm whitespace-pre-wrap">{summary}</p>}
      </div>
      {goals.map((goal, index) => <fieldset key={index} disabled={saving || generating || !residentId} className="rounded-xl border p-4 space-y-3">
        <legend className="font-semibold px-1">Goal {index + 1}</legend>
        <label className="block text-sm">Goal title {index + 1}<input aria-label={`Goal title ${index + 1}`} required maxLength={255} value={goal.goalName} onChange={event => updateGoal(index, { goalName: event.target.value })} className={field} /></label>
        <label className="block text-sm">Goal description {index + 1}<textarea aria-label={`Goal description ${index + 1}`} required maxLength={10000} rows={3} value={goal.description} onChange={event => updateGoal(index, { description: event.target.value })} className={field} /></label>
        {goal.rationale && <p className="text-xs text-muted-foreground">AI rationale for review: {goal.rationale}</p>}
        {goal.interventions.map((intervention, j) => <div key={j} className="border-l-2 pl-3 grid gap-2 sm:grid-cols-[1fr_160px_auto]">
          <label className="text-sm">Intervention {index + 1}.{j + 1}<textarea aria-label={`Intervention ${index + 1}.${j + 1}`} rows={2} required maxLength={2000} value={intervention.name} onChange={event => updateGoal(index, { interventions: goal.interventions.map((item, k) => k === j ? { ...item, name: event.target.value } : item) })} className={field} /></label>
          <label className="text-sm">Assigned role<select aria-label={`Assigned role ${index + 1}.${j + 1}`} className={field} value={intervention.assignedRole} onChange={event => updateGoal(index, { interventions: goal.interventions.map((item, k) => k === j ? { ...item, assignedRole: event.target.value } : item) })}>{["NURSE", "CNA", "DOCTOR", "PT", "OT"].map(role => <option key={role}>{role}</option>)}</select></label>
          <button type="button" className="text-sm text-red-600 underline" onClick={() => updateGoal(index, { interventions: goal.interventions.filter((_, k) => k !== j) })}>Remove intervention {index + 1}.{j + 1}</button>
        </div>)}
        <div className="flex gap-4 flex-wrap"><button type="button" className="text-sm text-blue-600 underline" disabled={goal.interventions.length >= 20} onClick={() => updateGoal(index, { interventions: [...goal.interventions, { name: "", assignedRole: "NURSE" }] })}>Add intervention to goal {index + 1}</button>
          <button type="button" className="text-sm text-red-600 underline" onClick={() => { setGoals(items => items.filter((_, i) => i !== index)); setReviewed(false); }}>Remove goal {index + 1}</button></div>
      </fieldset>)}
      <Button type="button" variant="outline" disabled={!residentId || generating || saving || goals.length >= 10} onClick={() => { setGoals(items => [...items, emptyGoal()]); setReviewed(false); }}>Add care goal</Button>
      <div className="rounded-xl border p-4 space-y-3">
        <label className="block text-sm">Save status<select aria-label="Save status" disabled={saving} className={field} value={purpose} onChange={event => setPurpose(event.target.value as "DRAFT" | "NEED_REVIEW")}><option value="DRAFT">Draft</option><option value="NEED_REVIEW">Pending review</option></select></label>
        <label className="flex items-start gap-2 text-sm"><input type="checkbox" checked={reviewed} disabled={saving || generating} onChange={event => setReviewed(event.target.checked)} />I verified the resident, chart information, care goals and interventions before saving.</label>
        {error && <p role="alert" className="text-sm text-destructive">{error}</p>}
        <Button type="submit" disabled={!residentId || !valid || !reviewed || saving || generating}>{saving ? "Saving care plan…" : "Save care plan"}</Button>
      </div>
    </form>
  </div>;
}
