import type { IncidentApiResponse } from "@/services/incidents/incidents-api";
import type { IncidentDetailsData } from "../components/report/incident-detail-view";

const typeLabels: Record<string, string> = {
  FALL: "Fall",
  MEDICATION_ERROR: "Medication Error",
  ALTERCATION: "Altercation",
  SKIN_TEAR: "Skin Tear",
};

const statusLabels: Record<string, string> = {
  OPEN: "Open",
  UNDER_INVESTIGATION: "Under Investigation",
  SUBMITTED: "Submitted",
  RESOLVED: "Resolved",
};

export function toIncidentRow(api: IncidentApiResponse) {
  const room = api.resident.bed?.room?.roomNumber;
  const resident = room ? `${api.resident.displayName} · ${room}` : api.resident.displayName;

  let sla = "—";
  let slaStyle = "text-slate-400";
  if (api.slaCountDown !== null && api.slaCountDown !== undefined) {
    if (api.slaCountDown < 0) {
      sla = "OVERDUE";
      slaStyle = "text-red-600";
    } else {
      sla = `${api.slaCountDown}h left`;
      slaStyle = api.slaCountDown <= 24 ? "text-red-600" : "text-emerald-600";
    }
  }

  return {
    id: api.id,
    resident,
    type: typeLabels[api.incidentType] ?? api.incidentType,
    severity: api.severity.levelName,
    reported: api.reportedAt ? api.reportedAt.replace("T", " ").slice(0, 16) : "",
    sla,
    slaStyle,
    status: statusLabels[api.status] ?? api.status,
    chart: api.isLocked ? "Locked" : "Unlocked",
  };
}

export function toIncidentDetail(api: IncidentApiResponse): IncidentDetailsData {
  const room = api.resident.bed?.room?.roomNumber;
  const timeline = (api.timelines ?? [])
    .slice()
    .sort((a, b) => (a.createdAt < b.createdAt ? 1 : -1))
    .map((entry, index) => ({
      title: entry.action,
      actor: entry.actor?.displayName ?? "System",
      time: entry.createdAt.replace("T", " ").slice(0, 16),
      filled: index === 0,
    }));

  return {
    id: `INC-${api.id}`,
    resident: api.resident.displayName,
    room,
    incidentType: typeLabels[api.incidentType] ?? api.incidentType,
    severity: api.severity.levelName,
    status: statusLabels[api.status] ?? api.status,
    reported: api.reportedAt ? api.reportedAt.replace("T", " ").slice(0, 16) : "",
    reportedBy: api.reporter?.displayName,
    location: api.location,
    description: api.description,
    witnesses: api.witnesses,
    chartStatus: api.isLocked ? "Locked" : "Unlocked",
    sla: api.slaCountDown == null ? "—" : api.slaCountDown < 0 ? "OVERDUE" : `${api.slaCountDown}h left`,
    slaDeadlineHours: api.slaDeadlineHours,
    timeline: timeline.length > 0 ? timeline : undefined,
  };
}
