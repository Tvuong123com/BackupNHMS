import { useState } from "react";
import { ShieldCheck, Search, Filter, Lock } from "lucide-react";
import { Input } from "@/components/ui/input";
import { Badge } from "@/components/ui/badge";
import { PERMISSIONS } from "@/common/permissions";

interface PermissionGroup {
  module: string;
  permissions: { code: string; name: string; description: string }[];
}

const permissionGroups: PermissionGroup[] = [
  {
    module: "User & Role Management",
    permissions: [
      { code: PERMISSIONS.USER_VIEW, name: "View Users", description: "View list of all system users and accounts" },
      { code: PERMISSIONS.USER_EDIT, name: "Manage Users", description: "Create, update, and change user statuses" },
      { code: PERMISSIONS.ROLE_VIEW, name: "View Roles", description: "View role directory and assigned permissions" },
      { code: PERMISSIONS.PERMISSION_VIEW, name: "View Permission Matrix", description: "View overall system permission matrix" },
    ],
  },
  {
    module: "Resident Intake & Ledger",
    permissions: [
      { code: PERMISSIONS.RESIDENT_VIEW, name: "View Residents", description: "View resident master directory and profiles" },
      { code: PERMISSIONS.SCREENING_VIEW, name: "Pre-Admission Screening", description: "Conduct and approve pre-admission screenings" },
      { code: PERMISSIONS.ADMISSION_VIEW, name: "Admissions Ledger", description: "Manage resident admission & bed assignment" },
      { code: PERMISSIONS.ASSESSMENT_VIEW, name: "Health Assessment", description: "Perform ADL assessment & care level recommendation" },
    ],
  },
  {
    module: "Facility & Compliance",
    permissions: [
      { code: PERMISSIONS.FACILITY_VIEW, name: "Facility Setup", description: "Manage facilities, rooms, beds, and LOC rates" },
      { code: PERMISSIONS.EQUIPMENT_VIEW, name: "Equipment & Supplies", description: "Manage DME equipment and consumable supplies" },
      { code: PERMISSIONS.INCIDENTS_VIEW, name: "Incident Center", description: "Track critical incidents and SLA resolutions" },
      { code: PERMISSIONS.AUDIT_LOG_VIEW, name: "System Audit Logs", description: "View system action audit logs" },
      { code: PERMISSIONS.PHI_ACCESS_LOG_VIEW, name: "PHI Access Logs", description: "View Protected Health Information access trails" },
    ],
  },
  {
    module: "Care Planning & Tasks",
    permissions: [
      { code: PERMISSIONS.CARE_PLAN_VIEW, name: "Care Plan Design", description: "Design, update, and approve resident care plans" },
      { code: PERMISSIONS.CARE_TASK_VIEW, name: "CNA Daily Tasks", description: "Assign and track daily CNA task execution" },
    ],
  },
];

export const PermissionTab = () => {
  const [search, setSearch] = useState("");

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h2 className="text-xl font-bold text-gray-900">System Permission Matrix</h2>
          <p className="text-sm text-gray-500">Read-only reference of granular system permissions</p>
        </div>

        <div className="relative w-full sm:w-72">
          <Search className="absolute left-3 top-2.5 h-4 w-4 text-gray-400" />
          <Input
            placeholder="Search permissions..."
            className="pl-9 text-sm"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>
      </div>

      <div className="space-y-6">
        {permissionGroups.map((group) => {
          const filtered = group.permissions.filter(
            (p) =>
              (p.code || "").toLowerCase().includes(search.toLowerCase()) ||
              (p.name || "").toLowerCase().includes(search.toLowerCase()) ||
              (p.description || "").toLowerCase().includes(search.toLowerCase())
          );

          if (filtered.length === 0) return null;

          return (
            <div key={group.module} className="rounded-xl border border-gray-200 bg-white p-5 shadow-sm space-y-4">
              <div className="flex items-center gap-2 border-b border-gray-100 pb-3">
                <ShieldCheck className="h-5 w-5 text-blue-600" />
                <h3 className="font-bold text-gray-900 text-base">{group.module}</h3>
                <Badge variant="outline" className="ml-auto text-xs">
                  {filtered.length} Permissions
                </Badge>
              </div>

              <div className="grid gap-3 sm:grid-cols-2">
                {filtered.map((perm) => (
                  <div key={perm.code} className="p-3.5 rounded-lg bg-gray-50/70 border border-gray-100 flex items-start justify-between gap-3">
                    <div className="space-y-1">
                      <div className="flex items-center gap-2">
                        <span className="font-semibold text-gray-900 text-sm">{perm.name}</span>
                      </div>
                      <span className="font-mono text-[11px] text-blue-600 bg-blue-50 px-2 py-0.5 rounded inline-block font-semibold">
                        {perm.code}
                      </span>
                      <p className="text-xs text-gray-500 leading-snug">{perm.description}</p>
                    </div>
                    <Lock className="h-4 w-4 text-gray-300 shrink-0 mt-1" />
                  </div>
                ))}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
