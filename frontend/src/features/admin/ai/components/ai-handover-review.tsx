import { useState } from "react";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { Link } from "react-router";
import axios from "axios";
import { aiService } from "../services/ai-service";
import { userService } from "@/services/user-service";
import { useCurrentUser } from "@/features/auth/hooks/use-current-user";
import type { HandoverDetail, HandoverSuggestion } from "../types/ai.types";

const taskTypes = ["OBSERVATION", "FALL_PREVENTION", "HYDRATION", "MEAL_ASSISTANCE", "MOBILITY", "REPOSITIONING", "TOILETING", "ACTIVITY"];
const errorMessage = (error: unknown, fallback: string) => axios.isAxiosError(error)
  ? error.response?.data?.message || error.response?.data?.detail || fallback : fallback;
const field = "block w-full rounded border p-2 mt-1 text-sm bg-background";

export function AiHandoverReview({ draftId, onShared }: { draftId: number; onShared: () => void }) {
  const { data: user } = useCurrentUser();
  const query = useQuery({ queryKey: ["handover", user?.id, draftId], queryFn: () => aiService.getHandover(draftId),
    refetchInterval: query => query.state.data?.status === "APPROVED" ? 30000 : false });
  if (query.isPending) return <p role="status">Loading handover…</p>;
  if (query.isError) return <div role="alert">Could not open this handover. It may be private or unavailable. <button type="button" className="underline" onClick={() => void query.refetch()}>Retry opening handover</button></div>;
  return <ReviewContent key={`${query.data.id}-${query.data.status}`} handover={query.data} onChanged={() => { void query.refetch(); onShared(); }} />;
}

function ReviewContent({ handover, onChanged }: { handover: HandoverDetail; onChanged: () => void }) {
  const [title, setTitle] = useState(handover.summary.title);
  const [narrative, setNarrative] = useState(handover.summary.narrativeSummary);
  const [actions, setActions] = useState<HandoverSuggestion[]>(handover.actions.map(item => ({
    residentId: item.action.residentId, description: item.action.description,
    taskType: item.action.taskType, sourceRefs: item.action.sourceRefs,
  })));
  const [confirmed, setConfirmed] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const residentOptions = [...new Map(handover.sources.map(source => [source.residentId, { id: source.residentId, name: source.residentName }])).values()];
  const approved = handover.status === "APPROVED";
  const editable = handover.canApprove && !saving;

  const updateAction = (index: number, update: Partial<HandoverSuggestion>) => {
    setActions(previous => previous.map((item, i) => i === index ? { ...item, ...update } : item));
    setConfirmed(false);
  };
  const approve = async () => {
    setError(""); setSaving(true);
    try { await aiService.approveHandover(handover.id, { title, narrativeSummary: narrative, actions }); onChanged(); }
    catch (err) { setError(errorMessage(err, "Could not approve this handover. Review the linked sources and try again.")); }
    finally { setSaving(false); }
  };

  return <article className="rounded-xl border p-4 space-y-4 bg-background" aria-label={`Handover ${handover.id}`}>
    <div className="flex flex-wrap justify-between gap-2 text-sm">
      <strong>Handover #{handover.id} · {approved ? "Approved and shared" : "Private draft"}</strong>
      <span className="text-muted-foreground">{handover.summary.generationMethod === "AI" ? (approved ? "AI assisted · staff approved" : "AI generated · staff review required") : "Recorded-input draft · AI generation unavailable or unverified"}</span>
    </div>
    <p className="text-xs text-muted-foreground">Author: {handover.createdBy} · Shift: {new Date(handover.start).toLocaleString()} – {new Date(handover.end).toLocaleString()}</p>
    {approved && <p className="text-xs text-emerald-700">Approved by {handover.approvedBy} · {handover.approvedAt && new Date(handover.approvedAt).toLocaleString()}. Shared with Admin/DON/Nurse.</p>}
    {!approved && !handover.canApprove && <p className="text-sm text-amber-700">This older draft has no resident/source links. Generate a new draft to review and share follow-up tasks.</p>}
    {handover.canApprove ? <>
      <label className="block text-sm">Reviewed title<input className={field} disabled={!editable} maxLength={255} value={title} onChange={e => { setTitle(e.target.value); setConfirmed(false); }} /></label>
      <label className="block text-sm">Reviewed handover summary<textarea aria-label="Reviewed handover summary" className={field} disabled={!editable} rows={5} maxLength={20000} value={narrative} onChange={e => { setNarrative(e.target.value); setConfirmed(false); }} /></label>
    </> : <div><h3 className="font-semibold">{handover.summary.title}</h3><p className="whitespace-pre-wrap text-sm mt-2">{handover.summary.narrativeSummary}</p></div>}
    <details className="text-sm"><summary className="cursor-pointer font-medium">Source records ({handover.sources.length})</summary>
      <ul className="mt-2 space-y-2">{handover.sources.map(source => <li key={source.reference} className="border-l-2 pl-3">
        <Link className="text-blue-600 underline" to={`/admin/residents/${source.residentId}`}>{source.residentName} · Resident #{source.residentId}</Link>
        <p className="text-xs">{source.reference}: {source.description}</p>
        {source.reference.startsWith("INCIDENT:") && <Link className="text-xs text-blue-600 underline" to={`/admin/incidents/${source.reference.split(":")[1]}`}>Open incident record</Link>}
      </li>)}</ul>
    </details>
    <h3 className="font-semibold text-sm">Resident follow-up actions</h3>
    {handover.canApprove ? <>
      <p className="text-xs text-muted-foreground">Review AI suggestions, remove unsupported items or add verified care support. Each action must link to a resident in the source records. Medication orders require the clinical prescribing workflow.</p>
      {actions.map((action, index) => <div key={index} className="border rounded p-3 space-y-2">
        <label className="block text-sm">Resident for action {index + 1}<select className={field} disabled={!editable} value={action.residentId} onChange={e => {
          const id = Number(e.target.value);
          updateAction(index, { residentId: id, sourceRefs: handover.sources.filter(s => s.residentId === id).map(s => s.reference) });
        }}>{residentOptions.map(resident => <option key={resident.id} value={resident.id}>{resident.name} (#{resident.id})</option>)}</select></label>
        <label className="block text-sm">Action {index + 1}<textarea aria-label={`Action ${index + 1}`} rows={2} maxLength={2000} className={field} disabled={!editable} value={action.description} onChange={e => updateAction(index, { description: e.target.value })} /></label>
        <label className="block text-sm">Task type {index + 1}<select className={field} disabled={!editable} value={action.taskType} onChange={e => updateAction(index, { taskType: e.target.value })}>{taskTypes.map(type => <option key={type}>{type}</option>)}</select></label>
        <p className="text-xs text-muted-foreground">Linked sources: {action.sourceRefs.join(", ")}</p>
        <button type="button" className="text-sm text-red-600 underline" disabled={!editable} onClick={() => { setActions(items => items.filter((_, i) => i !== index)); setConfirmed(false); }}>Remove action {index + 1}</button>
      </div>)}
      <button type="button" className="border rounded px-3 py-2 text-sm" disabled={!editable || !residentOptions.length || actions.length >= 12} onClick={() => {
        const residentId = residentOptions[0].id;
        setActions(items => [...items, { residentId, description: "", taskType: "OBSERVATION", sourceRefs: handover.sources.filter(s => s.residentId === residentId).map(s => s.reference) }]); setConfirmed(false);
      }}>Add verified follow-up</button>
      {!residentOptions.length && <p className="text-xs text-amber-700">No resident-linked source is available. Select a resident when generating a new draft to turn verified notes into linked actions.</p>}
      <label className="flex items-start gap-2 text-sm"><input type="checkbox" checked={confirmed} disabled={saving} onChange={e => setConfirmed(e.target.checked)} />I verified the summary, resident identities and sources. Share this reviewed handover with Admin/DON/Nurse.</label>
      <button type="button" disabled={!confirmed || saving || !title.trim() || !narrative.trim() || actions.some(a => !a.description.trim())} onClick={() => void approve()} className="rounded bg-emerald-700 text-white px-4 py-2 text-sm disabled:opacity-50">{saving ? "Sharing…" : "Approve & share handover"}</button>
    </> : approved ? <>
      {!handover.actions.length && <p className="text-sm text-muted-foreground">No follow-up tasks were included in this reviewed handover.</p>}
      {handover.actions.map(item => <AssignmentCard key={item.action.id} handoverId={handover.id} item={item} onChanged={onChanged} />)}
    </> : <p className="text-sm text-muted-foreground">Resident-linked follow-ups are available for newly generated handovers.</p>}
    {error && <p role="alert" className="text-sm text-red-600">{error}</p>}
  </article>;
}

function AssignmentCard({ handoverId, item, onChanged }: { handoverId: number; item: HandoverDetail["actions"][number]; onChanged: () => void }) {
  const queryClient = useQueryClient();
  const [cnaId, setCnaId] = useState("");
  const [due, setDue] = useState("");
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const cnas = useQuery({ queryKey: ["handover-cnas"], queryFn: userService.getActiveCnas, enabled: !item.action.careTaskId, retry: false });
  const assign = async () => {
    const scheduled = new Date(due);
    if (!cnaId || !Number.isFinite(scheduled.getTime()) || scheduled.getTime() <= Date.now()) { setError("Choose a CNA and a future scheduled time."); return; }
    setError(""); setSaving(true);
    try {
      await aiService.assignHandoverTask(handoverId, item.action.id, { assignedCnaId: Number(cnaId), scheduledTime: scheduled.toISOString() });
      await Promise.all([queryClient.invalidateQueries({ queryKey: ["care-tasks-by-cna"] }), queryClient.invalidateQueries({ queryKey: ["care-tasks-by-resident"] })]);
      onChanged();
    } catch (err) { setError(errorMessage(err, "Could not create the task. Check the resident's active care plan and selected CNA.")); }
    finally { setSaving(false); }
  };
  return <div className="border rounded p-3 space-y-2 text-sm">
    <Link className="font-medium text-blue-600 underline" to={`/admin/residents/${item.action.residentId}`}>{item.residentName} · Resident #{item.action.residentId}</Link>
    <p>{item.action.description}</p><p className="text-xs text-muted-foreground">{item.action.taskType} · Sources: {item.action.sourceRefs.join(", ")}</p>
    {item.action.careTaskId ? <>
      <p className="font-medium">Task #{item.action.careTaskId} · {item.taskStatus}</p>
      <p className="text-xs">CNA: {item.assignedCnaName || "Unassigned"} · Scheduled: {item.scheduledTime ? new Date(item.scheduledTime).toLocaleString() : "Unavailable"}</p>
      <p className="text-xs text-muted-foreground">Assigned by {item.action.assignedBy}</p>
      <Link className="text-blue-600 underline" to={`/admin/care-tasks?tab=by-resident&residentId=${item.action.residentId}`}>Open task #{item.action.careTaskId} in Care Tasks</Link>
    </> : <>
      <div className="grid gap-3 sm:grid-cols-2">
        <label>Assigned CNA<select aria-label={`Assigned CNA for ${item.action.id}`} className={field} disabled={saving || cnas.isPending} value={cnaId} onChange={e => setCnaId(e.target.value)}><option value="">Select a CNA</option>{(cnas.data ?? []).map((cna: { id: number; fullName: string }) => <option key={cna.id} value={cna.id}>{cna.fullName}</option>)}</select></label>
        <label>Scheduled time<input aria-label={`Scheduled time for ${item.action.id}`} type="datetime-local" className={field} disabled={saving} value={due} onChange={e => setDue(e.target.value)} /></label>
      </div>
      {cnas.isError && <p role="alert">Could not load CNAs. <button type="button" className="underline" onClick={() => void cnas.refetch()}>Retry CNA list</button></p>}
      <button type="button" className="bg-blue-600 rounded text-white px-3 py-2 disabled:opacity-50" disabled={saving || !cnaId || !due} onClick={() => void assign()}>{saving ? "Creating task…" : "Create assigned Care Task"}</button>
    </>}
    {error && <p role="alert" className="text-red-600">{error}</p>}
  </div>;
}
