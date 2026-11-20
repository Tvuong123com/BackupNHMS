import { PERMISSIONS, type PermissionCode } from "@/common/permissions";

const nurse: PermissionCode[] = [
  PERMISSIONS.RESIDENT_VIEW,
  PERMISSIONS.RESIDENT_SENSITIVE_VIEW,
  PERMISSIONS.ASSESSMENT_VIEW,
  PERMISSIONS.CARE_PLAN_VIEW,
  PERMISSIONS.CARE_TASK_VIEW,
  PERMISSIONS.NOTIFICATION_VIEW,
];

const cna: PermissionCode[] = [
  PERMISSIONS.RESIDENT_VIEW,
  PERMISSIONS.CARE_TASK_VIEW,
  PERMISSIONS.NOTIFICATION_VIEW,
];

const admissionStaff: PermissionCode[] = [
  PERMISSIONS.RESIDENT_VIEW,
  PERMISSIONS.RESIDENT_SENSITIVE_VIEW,
  PERMISSIONS.SCREENING_VIEW,
  PERMISSIONS.ADMISSION_VIEW,
  PERMISSIONS.FACILITY_VIEW,
  PERMISSIONS.NOTIFICATION_VIEW,
];

const clinicalRoles = new Set([
  "SYSTEM_ADMINISTRATOR", "ADMIN", "NHA_ADMIN", "DON",
  "DON_-_DIRECTOR_OF_NURSING", "NURSE", "NURSE_(LPN/RN)",
]);

const normalizeRole = (roleName: string) => roleName.trim().toUpperCase().replace(/\s+/g, "_");

export const isClinicalReviewer = (roleName: string) => clinicalRoles.has(normalizeRole(roleName));

export const permissionsForRole = (roleName: string): PermissionCode[] => {
  const role = normalizeRole(roleName);
  if (["SYSTEM_ADMINISTRATOR", "ADMIN", "NHA_ADMIN", "DON", "DON_-_DIRECTOR_OF_NURSING"].includes(role)) {
    return Object.values(PERMISSIONS);
  }
  if (["NURSE", "NURSE_(LPN/RN)"].includes(role)) return nurse;
  if (["CNA", "CNA_(CAREGIVER)"].includes(role)) return cna;
  if (role === "ADMISSION_STAFF") return admissionStaff;
  return [];
};
