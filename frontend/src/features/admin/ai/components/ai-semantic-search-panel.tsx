import { useState } from "react";
import { Search } from "lucide-react";
import { Link } from "react-router";
import { aiService } from "../services/ai-service";
import type { SemanticSearchHit } from "../types/ai.types";
import { PERMISSIONS } from "@/common/permissions";
import { usePermissions } from "@/features/auth/hooks/use-current-user";

export const AiSemanticSearchPanel = () => {
  const { can } = usePermissions();
  const [query, setQuery] = useState("");
  const [residentId, setResidentId] = useState("");
  const [hits, setHits] = useState<SemanticSearchHit[]>([]);
  const [busy, setBusy] = useState(false);
  const [message, setMessage] = useState("");

  const search = async () => {
    setBusy(true); setMessage("");
    try {
      const result = await aiService.semanticSearch(query.trim(), residentId ? Number(residentId) : undefined);
      setHits(result);
      if (result.length === 0) setMessage("No matching indexed records. Ask a DON or administrator to refresh the index if records changed.");
    } catch { setMessage("Search unavailable. Check your access, SQL Server and the Ollama embedding model."); }
    finally { setBusy(false); }
  };

  const reindex = async () => {
    setBusy(true); setMessage("");
    try { setMessage(`${await aiService.reindexSemanticSearch()} records indexed from workflow reminders, care goals, and recent incidents.`); }
    catch { setMessage("Could not refresh the index. DON or administrator access is required."); }
    finally { setBusy(false); }
  };

  return <section className="rounded-2xl border bg-white dark:bg-zinc-900 p-5 space-y-3">
    <h2 className="font-semibold flex items-center gap-2"><Search className="size-4" /> F6 · Semantic chart search</h2>
    <p className="text-xs text-muted-foreground">Finds staff workflow reminders, care goals, and documented incidents using local Ollama embeddings. Results are source excerpts for staff review.</p>
    <div className="flex flex-wrap gap-2">
      <input className="border rounded px-3 py-2 flex-1 min-w-48 text-sm" aria-label="Semantic search query" value={query} onChange={e => setQuery(e.target.value)} onKeyDown={e => { if (e.key === "Enter") void search(); }} placeholder="Search recorded incidents or staff workflow reminders" />
      <input className="border rounded px-3 py-2 w-32 text-sm" aria-label="Resident ID filter" type="number" min="1" value={residentId} onChange={e => setResidentId(e.target.value)} placeholder="Resident ID" />
      <button type="button" onClick={() => void search()} disabled={busy || !query.trim()} className="rounded bg-blue-600 text-white px-3 py-2 text-sm disabled:opacity-50">Search</button>
      <button type="button" onClick={() => void reindex()} disabled={busy} className="rounded border px-3 py-2 text-sm disabled:opacity-50">Refresh index</button>
    </div>
    {message && <p className="text-xs text-slate-600">{message}</p>}
    {hits.length > 0 && <ul className="space-y-2">{hits.map(hit => <li key={`${hit.sourceType}-${hit.sourceId}`} className="border rounded p-3 text-sm">
      <div className="flex justify-between gap-2"><b>{hit.title}</b><span className="text-xs text-slate-500">{hit.sourceType} · {Math.round(hit.similarity * 100)}% similarity</span></div>
      <p className="text-xs mt-1">{hit.excerpt}</p>
      {hit.sourceType === "INCIDENT" && can(PERMISSIONS.INCIDENTS_VIEW) && <Link className="text-xs text-blue-600 underline" to={`/admin/incidents/${hit.sourceId}`}>Open incident record</Link>}
    </li>)}</ul>}
  </section>;
};
