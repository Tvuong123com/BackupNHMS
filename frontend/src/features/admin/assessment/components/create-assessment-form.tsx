import { useState } from "react";
import { Button } from "@/components/ui/button";
import { LookupSelect, apiErrorMessage } from "@/components/common/lookup-select";
import { useCreateAssessment } from "../hooks/use-create-assessment";
import { useAssessmentMetrics } from "../hooks/use-assessment-metrics";
import { useActiveAdmissions } from "../../admissions/hooks/use-active-admissions";
import { useDialogStore } from "@/store/use-dialog-store";

export const CreateAssessmentForm = () => {
  const admissions = useActiveAdmissions();
  const metrics = useAssessmentMetrics();
  const options = admissions.data?.data ?? [];
  const items = metrics.data?.data ?? [];
  const [admissionId, setAdmissionId] = useState("");
  const [scores, setScores] = useState<Record<number, string>>({});
  const [notes, setNotes] = useState<Record<number, string>>({});
  const create = useCreateAssessment();
  const close = useDialogStore(state => state.close);
  const complete = items.length > 0 && items.every(item => scores[item.id] !== undefined && scores[item.id] !== "" && Number.isInteger(Number(scores[item.id])) && Number(scores[item.id]) >= 0);
  return <form className="space-y-4" onSubmit={event => {
    event.preventDefault();
    if (!complete || !options.some(item => String(item.id) === admissionId)) return;
    create.mutate({ admissionId: Number(admissionId), details: items.map(item => ({ metricId: item.id, score: Number(scores[item.id]), notes: notes[item.id]?.trim() || undefined })) }, { onSuccess: close });
  }}>
    <LookupSelect id="assessment-admission" label="Admission" value={admissionId} onChange={value => { setAdmissionId(value); setScores({}); setNotes({}); }}
      loading={admissions.isPending} failed={admissions.isError} retry={() => void admissions.refetch()} disabled={create.isPending}
      options={options.map(item => ({ value: String(item.id), label: `#${item.id} · ${item.residentName}` }))}
      emptyMessage="No eligible admission is available. This application requires a current, non-discharged admission within 14 days, without an assessment draft in progress." />
    {!admissions.isPending && options.length === 0 && <a href="/admin/admissions" onClick={close} className="text-sm text-blue-600 underline">Open Admissions & Bed Assignments</a>}
    <p className="text-xs text-muted-foreground">Enter observed scores using the facility's assessment criteria. Scores are not prefilled or inferred by AI. Only ADL metrics contribute to the ADL total.</p>
    {metrics.isPending && <p role="status">Loading assessment metrics…</p>}
    {metrics.isError && <p role="alert">Could not load assessment metrics. <button type="button" className="underline" onClick={() => void metrics.refetch()}>Retry metrics</button></p>}
    {metrics.isSuccess && !items.length && <p>No assessment metrics are configured.</p>}
    {items.map(item => <fieldset key={item.id} className="border rounded-lg p-3 space-y-2" disabled={create.isPending || !admissionId}>
      <legend className="text-sm font-medium">{item.metricName} · {item.category}</legend>
      <label className="text-sm" htmlFor={`metric-${item.id}`}>Score · {item.metricName}</label>
      <input id={`metric-${item.id}`} type="number" required min={0} step={1} value={scores[item.id] ?? ""} onChange={event => setScores(previous => ({ ...previous, [item.id]: event.target.value }))} className="w-full rounded border p-2 text-sm" />
      <label className="block text-sm" htmlFor={`metric-notes-${item.id}`}>Observation notes · {item.metricName}</label>
      <input id={`metric-notes-${item.id}`} value={notes[item.id] ?? ""} onChange={event => setNotes(previous => ({ ...previous, [item.id]: event.target.value }))} className="w-full rounded border p-2 text-sm" />
    </fieldset>)}
    {create.isError && <p role="alert" className="text-sm text-destructive">{apiErrorMessage(create.error, "Could not create assessment. Check admission eligibility and scores.")}</p>}
    <Button type="submit" disabled={create.isPending || !complete || !options.some(item => String(item.id) === admissionId)}>{create.isPending ? "Creating…" : "Create"}</Button>
  </form>;
};
