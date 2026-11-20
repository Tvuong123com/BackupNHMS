export const TIERS = [
  { id: 1, name: 'Tier 1 — Low', range: '0 - 8', defaultRate: '165.00' },
  { id: 2, name: 'Tier 2 — Moderate', range: '9 - 16', defaultRate: '205.00' },
  { id: 3, name: 'Tier 3 — High', range: '17 - 24', defaultRate: '248.00' },
  { id: 4, name: 'Tier 4 — Total', range: '25 - 32', defaultRate: '310.00' },
];

export const CURRENT_USER = {
  name: 'Victor Alvarez',
  role: 'System Admin',
  initials: 'VA',
};

const getBaseUrl = () => {
  if (import.meta.env.VITE_API_URL !== undefined && import.meta.env.VITE_API_URL !== "") {
    const trimmed = import.meta.env.VITE_API_URL.replace(/\/+$/, "");
    return trimmed.endsWith("/api/v1") ? trimmed : `${trimmed}/api/v1`;
  }
  if (import.meta.env.PROD) {
    return "/api/v1";
  }
  return "http://localhost:8080/api/v1";
};

export const API_BASE_URL = getBaseUrl();
