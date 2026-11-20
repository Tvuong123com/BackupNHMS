import { useState, useEffect } from "react";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import axios from "axios";
import { FileText, Loader2, Clock, CheckCircle2, AlertCircle, Sparkles, Send } from "lucide-react";
import { aiService } from "../services/ai-service";
import type { ShiftDraftResponse } from "../types/ai.types";
import { useCurrentUser } from "@/features/auth/hooks/use-current-user";
import { AiHandoverReview } from "./ai-handover-review";

const localInput = (date: Date) => new Date(date.getTime() - date.getTimezoneOffset() * 60000)
  .toISOString().slice(0, 16);

export const AiShiftSummaryPanel = () => {
  const { data: currentUser } = useCurrentUser();
  const queryClient = useQueryClient();
  const [start, setStart] = useState(localInput(new Date(Date.now() - 8 * 3600000)));
  const [end, setEnd] = useState(localInput(new Date()));
  const [notes, setNotes] = useState("");
  const [residentId, setResidentId] = useState("");
  const [selectedDraftId, setSelectedDraftId] = useState<number | null>(null);
  const [result, setResult] = useState<ShiftDraftResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const history = useQuery({
    queryKey: ["shift-handover-drafts", currentUser?.id],
    enabled: Boolean(currentUser),
    queryFn: async () => (await aiService.getMySummaryDrafts())
      .filter((draft) => draft.audience === "STAFF"),
    retry: false,
  });
  const savedDrafts = history.data ?? [];
  const residents = useQuery({ queryKey: ["handover-residents", currentUser?.id], queryFn: aiService.getHandoverResidents, retry: false });
  const shared = useQuery({ queryKey: ["shared-handovers", currentUser?.id], queryFn: aiService.getSharedHandovers, retry: false, refetchInterval: 30000 });

  // Auto-select latest shared or saved handover if none selected yet (Solves empty screen issue)
  useEffect(() => {
    if (selectedDraftId === null) {
      if (shared.data && shared.data.length > 0) {
        setSelectedDraftId(shared.data[0].id);
      } else if (savedDrafts.length > 0) {
        setSelectedDraftId(savedDrafts[0].id);
      }
    }
  }, [shared.data, savedDrafts, selectedDraftId]);

  const refreshShared = () => {
    void shared.refetch();
    void queryClient.invalidateQueries({ queryKey: ["handover", currentUser?.id] });
  };

  const setPresetShift = (hoursBackStart: number, hoursBackEnd: number) => {
    const now = Date.now();
    setStart(localInput(new Date(now - hoursBackStart * 3600000)));
    setEnd(localInput(new Date(now - hoursBackEnd * 3600000)));
    setError("");
  };

  const generate = async () => {
    setError("");
    setResult(null);
    const startTime = new Date(start);
    const endTime = new Date(end);
    if (!Number.isFinite(startTime.getTime()) || !Number.isFinite(endTime.getTime())) {
      setError("Select both shift start and shift end.");
      return;
    }
    if (endTime <= startTime) {
      setError("Shift end must be later than shift start.");
      return;
    }
    if (endTime.getTime() - startTime.getTime() > 24 * 3600000) {
      setError("Choose a shift lasting no more than 24 hours.");
      return;
    }
    // Allow small clock skew
    if (endTime.getTime() > Date.now() + 60000) {
      setError("Shift end must be in the past. Use 'Past 8 Hours' for a quick preset.");
      return;
    }
    setLoading(true);
    try {
      const data = await aiService.draftFromRecords({
        start: startTime.toISOString(),
        end: endTime.toISOString(),
        audience: "STAFF",
        residentId: residentId ? Number(residentId) : undefined,
        verifiedNotes: notes.trim() || undefined,
      });
      setResult(data);
      setSelectedDraftId(data.draftId);
      void history.refetch();
    } catch (err) {
      const response = axios.isAxiosError<{ message?: string }>(err) ? err.response : undefined;
      const validationMessage = response && [400, 404].includes(response.status)
        ? response.data?.message
        : undefined;
      setError(validationMessage || "Could not create a draft. Check your connection, AI provider and available chart records, then try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <section className="mt-5 rounded-2xl border border-slate-200 dark:border-zinc-800 bg-white dark:bg-zinc-900 p-6 space-y-6 shadow-xs">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 border-b border-slate-100 dark:border-zinc-800 pb-4">
        <div>
          <h2 className="font-bold text-lg flex gap-2 items-center text-slate-900 dark:text-zinc-100">
            <FileText className="size-5 text-blue-600" />
            Clinical Shift Handover (SBAR Protocol & AI Synthesis)
          </h2>
          <p className="text-xs text-slate-500 mt-1">
            Automates shift handoffs by aggregating EHR completed care tasks, medication passes, and incident logs into standard SBAR format (Situation, Background, Assessment, Recommendation).
          </p>
        </div>
        <span className="px-3 py-1 rounded-full text-xs font-semibold bg-blue-50 text-blue-700 border border-blue-200 dark:bg-blue-950/40 shrink-0">
          Clinical SBAR Standard
        </span>
      </div>

      {/* Preset Shift Buttons */}
      <div className="space-y-2">
        <span className="text-xs font-semibold text-slate-600 dark:text-zinc-300 block">
          Quick Shift Preset Selection:
        </span>
        <div className="flex flex-wrap gap-2">
          <button
            type="button"
            onClick={() => setPresetShift(8, 0)}
            className="px-3 py-1.5 rounded-lg border border-slate-200 dark:border-zinc-700 bg-slate-50 hover:bg-slate-100 dark:bg-zinc-800 text-xs font-medium text-slate-700 dark:text-zinc-200 transition-colors"
          >
            ⏱️ Past 8 Hours
          </button>
          <button
            type="button"
            onClick={() => setPresetShift(12, 0)}
            className="px-3 py-1.5 rounded-lg border border-slate-200 dark:border-zinc-700 bg-slate-50 hover:bg-slate-100 dark:bg-zinc-800 text-xs font-medium text-slate-700 dark:text-zinc-200 transition-colors"
          >
            🌙 12-Hour Night Shift
          </button>
          <button
            type="button"
            onClick={() => setPresetShift(24, 0)}
            className="px-3 py-1.5 rounded-lg border border-slate-200 dark:border-zinc-700 bg-slate-50 hover:bg-slate-100 dark:bg-zinc-800 text-xs font-medium text-slate-700 dark:text-zinc-200 transition-colors"
          >
            📅 Last 24 Hours
          </button>
        </div>
      </div>

      {/* Shift Scope & Time Inputs */}
      <div className="grid gap-4 sm:grid-cols-3">
        <label className="block text-xs font-semibold text-slate-700 dark:text-zinc-300">
          Resident Scope
          <select
            className="block w-full border border-slate-300 dark:border-zinc-700 rounded-xl p-2 mt-1 text-xs bg-background"
            disabled={loading || residents.isPending}
            value={residentId}
            onChange={(e) => { setResidentId(e.target.value); setResult(null); }}
          >
            <option value="">All facility residents with shift events</option>
            {(residents.data ?? []).map((resident) => (
              <option key={resident.id} value={resident.id}>
                {resident.name} (#{resident.id})
              </option>
            ))}
          </select>
        </label>

        <label className="block text-xs font-semibold text-slate-700 dark:text-zinc-300">
          Shift Start Time
          <input
            type="datetime-local"
            disabled={loading}
            value={start}
            onChange={(e) => { setStart(e.target.value); setError(""); setResult(null); }}
            className="block w-full border border-slate-300 dark:border-zinc-700 rounded-xl p-2 mt-1 text-xs bg-background"
          />
        </label>

        <label className="block text-xs font-semibold text-slate-700 dark:text-zinc-300">
          Shift End Time
          <input
            type="datetime-local"
            disabled={loading}
            value={end}
            onChange={(e) => { setEnd(e.target.value); setError(""); setResult(null); }}
            className="block w-full border border-slate-300 dark:border-zinc-700 rounded-xl p-2 mt-1 text-xs bg-background"
          />
        </label>
      </div>

      {/* Additional Notes */}
      <label className="block text-xs font-semibold text-slate-700 dark:text-zinc-300">
        Clinical Observations & Verification Notes (Optional verbal handoff items)
        <textarea
          aria-label="Additional verified notes"
          maxLength={10000}
          disabled={loading}
          value={notes}
          onChange={(e) => { setNotes(e.target.value); setError(""); setResult(null); }}
          rows={2}
          className="block w-full border border-slate-300 dark:border-zinc-700 rounded-xl p-2.5 mt-1 text-xs bg-background"
          placeholder="e.g. Resident in Room 204 was mildly confused at 03:00; fluids encouraged. Dr. Smith notified of blood pressure reading."
        />
      </label>

      {/* Action Button */}
      <div className="flex items-center gap-3">
        <button
          type="button"
          onClick={() => void generate()}
          disabled={loading || !start || !end}
          className="px-4 py-2.5 rounded-xl bg-blue-600 hover:bg-blue-700 text-white text-xs font-semibold disabled:opacity-50 transition-colors shadow-2xs flex items-center gap-2"
        >
          {loading ? (
            <>
              <Loader2 className="size-4 animate-spin" />
              Synthesizing EHR Records into SBAR…
            </>
          ) : (
            <>
              <Sparkles className="size-4" />
              Draft Handover Summary (SBAR)
            </>
          )}
        </button>
      </div>

      {error && (
        <div className="p-3 rounded-xl bg-rose-50 border border-rose-200 text-rose-800 text-xs flex items-center justify-between">
          <span>{error}</span>
          <button type="button" onClick={() => setError("")} className="font-bold ml-2">×</button>
        </div>
      )}

      {result && (
        <div className="p-3 rounded-xl bg-emerald-50 border border-emerald-200 text-emerald-800 text-xs flex items-center justify-between">
          <span>✓ Handover draft #{result.draftId} generated successfully. Review SBAR details and action items below.</span>
          <button type="button" onClick={() => setResult(null)} className="font-bold ml-2">×</button>
        </div>
      )}

      {/* Currently Selected Handover Detailed View */}
      {selectedDraftId !== null && (
        <div className="pt-2 border-t border-slate-200 dark:border-zinc-800">
          <div className="flex items-center justify-between mb-3">
            <h3 className="text-sm font-bold text-slate-900 dark:text-zinc-100 flex items-center gap-2">
              <CheckCircle2 className="size-4 text-emerald-600" />
              Viewing Handover Report #{selectedDraftId}
            </h3>
          </div>
          <AiHandoverReview
            draftId={selectedDraftId}
            onShared={() => {
              void shared.refetch();
              void history.refetch();
            }}
          />
        </div>
      )}

      {/* Shared Approved Handovers List */}
      <div className="border-t border-slate-200 dark:border-zinc-800 pt-4 space-y-3">
        <div className="flex flex-wrap items-center justify-between gap-2">
          <div>
            <h3 className="font-bold text-sm text-slate-900 dark:text-zinc-100">
              Approved Shared Handovers ({shared.data?.length || 0})
            </h3>
            <p className="text-xs text-slate-500">
              Live broadcast handovers accessible to Nurses, DON, and Administrators.
            </p>
          </div>
          <button
            type="button"
            className="text-xs text-blue-600 hover:underline font-medium flex items-center gap-1"
            disabled={shared.isFetching}
            onClick={refreshShared}
          >
            Refresh List
          </button>
        </div>

        {shared.isPending && <p className="text-xs text-slate-400">Loading approved handovers…</p>}
        {shared.isError && <p className="text-xs text-rose-600">Could not load shared handovers.</p>}
        {shared.isSuccess && shared.data.length === 0 && (
          <div className="p-6 rounded-xl border border-dashed border-slate-200 dark:border-zinc-800 text-center text-xs text-slate-500">
            No approved handovers shared yet. Click <b>"Draft Handover Summary"</b> above to generate the first clinical handover!
          </div>
        )}

        <div className="grid gap-2 sm:grid-cols-2">
          {(shared.data ?? []).map((item) => (
            <div
              key={item.id}
              onClick={() => setSelectedDraftId(item.id)}
              className={`p-3.5 rounded-xl border text-xs cursor-pointer transition-all hover:shadow-2xs ${
                selectedDraftId === item.id
                  ? "border-blue-500 bg-blue-50/30 dark:bg-zinc-800"
                  : "border-slate-200 dark:border-zinc-800 bg-white dark:bg-zinc-900 hover:border-blue-300"
              }`}
            >
              <div className="flex items-start justify-between gap-2">
                <span className="font-bold text-slate-900 dark:text-zinc-100">
                  #{item.id} · {item.summary.title}
                </span>
                <span className="text-[10px] font-bold px-1.5 py-0.5 rounded bg-emerald-100 text-emerald-800 shrink-0">
                  APPROVED
                </span>
              </div>
              <p className="text-slate-500 text-[11px] mt-1">
                Approved by <b>{item.approvedBy}</b> · {item.actions.length} Action Follow-up(s)
              </p>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
};
