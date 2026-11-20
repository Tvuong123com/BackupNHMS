import { useEffect, useState } from "react";
import axios from "axios";
import { Link, useParams } from "react-router";
import { IncidentDetailPage } from "../components/report/incident-detail-view";
import { toIncidentDetail } from "../utils/transform";
import { incidentsApi, type IncidentApiResponse } from "@/services/incidents/incidents-api";

export default function IncidentRecordPage() {
  const { id } = useParams<{ id: string }>();
  const [incident, setIncident] = useState<IncidentApiResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const incidentId = Number(id);
    if (!id || !/^\d+$/.test(id) || !Number.isSafeInteger(incidentId) || incidentId < 1) {
      setIncident(null);
      setLoading(false);
      setError("Invalid incident ID.");
      return;
    }

    let active = true;
    setIncident(null);
    setLoading(true);
    setError("");
    void incidentsApi.getById(incidentId)
      .then((record) => { if (active) setIncident(record); })
      .catch((cause: unknown) => {
        if (!active) return;
        const status = axios.isAxiosError(cause) ? cause.response?.status : undefined;
        setError(status === 404
          ? `Incident #${incidentId} was not found.`
          : status === 403
            ? "You do not have permission to view this incident."
            : "Could not load this incident. Try again later.");
      })
      .finally(() => { if (active) setLoading(false); });
    return () => { active = false; };
  }, [id]);

  return (
    <div className="space-y-4">
      <Link to="/admin/incidents" className="text-sm font-medium text-blue-600 hover:underline">← Back to incidents</Link>
      {loading && <p className="text-sm text-slate-500">Loading incident record…</p>}
      {error && <p role="alert" className="rounded-lg border border-red-200 bg-red-50 p-4 text-sm text-red-700">{error}</p>}
      {incident && !loading && (
        <>
          {incident.isLocked && <p className="rounded-lg border border-amber-200 bg-amber-50 p-4 text-sm text-amber-800">This chart is locked. The incident record is read-only.</p>}
          <IncidentDetailPage mode="view" data={toIncidentDetail(incident)} />
        </>
      )}
    </div>
  );
}
