import axios from "axios";

// Public API client — no authentication token attached
const publicApi = axios.create({
  baseURL: "/api/v1/ai/website",
  headers: { "Content-Type": "application/json" },
});

// ─── Types ───────────────────────────────────────────────────────────
export interface WebsiteChatRequest {
  message: string;
  currentPage?: string;
}

export interface WebsiteChatResponse {
  reply: string;
  success: boolean;
  fallback: boolean;
}

export interface ScreeningRequest {
  age?: number;
  gender?: string;
  mobilityStatus?: string;
  medicalConditions?: string[];
  dailyNeedsHelp?: string[];
  additionalNotes?: string;
}

export interface ScreeningResponse {
  recommendedService: string;
  suggestedCareLevel: string;
  additionalServices: string[];
  estimatedTimeline: string;
  rationale: string;
  nextStep: string;
  success: boolean;
  fallback: boolean;
}

export interface FaqItem {
  q: string;
  a: string;
}

export interface FaqSearchRequest {
  question: string;
  context?: string;
  availableFaqs?: FaqItem[];
}

export interface FaqSearchResponse {
  answer: string;
  matchedFaqQuestion?: string;
  success: boolean;
  fallback: boolean;
}

export interface CareQuizRequest {
  recipient?: string;
  primaryNeed?: string;
  careUrgency?: string;
  mobility?: string;
  symptomsOrGoals?: string[];
}

export interface CareQuizResponse {
  recommendedService: string;
  serviceUrl: string;
  badge: string;
  headline: string;
  summary: string;
  keyBenefits: string[];
  questionsForTeam?: string[];
  recommendedAction: string;
  success: boolean;
  fallback: boolean;
}

export interface MemoryRiskRequest {
  observedSigns: string[];
  duration?: string;
  additionalObservations?: string;
}

export interface MemoryRiskResponse {
  estimatedCategory: string;
  clinicalPerspective: string;
  homeStrategies: string[];
  facilitySupportPlan: string;
  recommendedNextStep: string;
  success: boolean;
  fallback: boolean;
}

// ─── Service ─────────────────────────────────────────────────────────
export const websiteAiService = {
  chat: async (request: WebsiteChatRequest): Promise<WebsiteChatResponse> => {
    const response = await publicApi.post<WebsiteChatResponse>("/chat", request);
    return response.data;
  },

  screenAdmission: async (request: ScreeningRequest): Promise<ScreeningResponse> => {
    const response = await publicApi.post<ScreeningResponse>("/screen", request);
    return response.data;
  },

  searchFaq: async (request: FaqSearchRequest): Promise<FaqSearchResponse> => {
    const response = await publicApi.post<FaqSearchResponse>("/faq", request);
    return response.data;
  },

  evaluateCareQuiz: async (request: CareQuizRequest): Promise<CareQuizResponse> => {
    const response = await publicApi.post<CareQuizResponse>("/care-quiz", request);
    return response.data;
  },

  explainMemoryRisk: async (request: MemoryRiskRequest): Promise<MemoryRiskResponse> => {
    const response = await publicApi.post<MemoryRiskResponse>("/memory-risk", request);
    return response.data;
  },
};
