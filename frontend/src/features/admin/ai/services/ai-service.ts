import { apiClient } from "@/lib/api-client";
import type { HandoverDetail, HandoverSuggestion } from "../types/ai.types";
import type {
  AiChatRequest,
  AiChatResponse,
  AiChatTurn,
  AiHealthResponse,
  IncidentAnalysisRequest,
  IncidentAnalysisResponse,
  ResidentReviewPriority,
  FallScreeningRequest,
  FallScreeningResult,
  ShiftDraftRequest,
  ShiftDraftResponse,
  SavedSummaryDraft,
  ElopementReviewResponse,
  MedicationReviewResponse,
  SemanticSearchHit,
  DynamicFallRiskResult,
  FallRiskHistoryResponse,
  ApplyFallInterventionRequest,
  ApplyFallInterventionResponse,
  ResidentChartContext,
} from "../types/ai.types";

export const aiService = {
  getResidentChartContext: async (residentId: number): Promise<ResidentChartContext> => {
    const response = await apiClient.get<ResidentChartContext>(`/ai/resident-chart-context/${residentId}`);
    return response.data;
  },  getHandoverResidents: async (): Promise<{ id: number; name: string }[]> =>
    (await apiClient.get("/ai/handover/residents")).data,
  getSharedHandovers: async (): Promise<HandoverDetail[]> =>
    (await apiClient.get("/ai/handover/shared")).data,
  getHandover: async (id: number): Promise<HandoverDetail> =>
    (await apiClient.get(`/ai/handover/${id}`)).data,
  approveHandover: async (id: number, review: { title: string; narrativeSummary: string; actions: HandoverSuggestion[] }): Promise<HandoverDetail> =>
    (await apiClient.post(`/ai/handover/${id}/approve`, review)).data,
  assignHandoverTask: async (id: number, actionId: string, assignment: { assignedCnaId: number; scheduledTime: string }): Promise<HandoverDetail> =>
    (await apiClient.post(`/ai/handover/${id}/actions/${actionId}/task`, assignment)).data,
  getLatestFallScreening: async (residentId: number): Promise<FallScreeningResult | null> => {
    const response = await apiClient.get<{ screening: FallScreeningResult | null }>("/ai/risk/fall-screenings/latest", { params: { residentId } });
    return response.data.screening;
  },
  recordFallScreening: async (request: FallScreeningRequest): Promise<FallScreeningResult> => {
    const response = await apiClient.post<FallScreeningResult>("/ai/risk/fall-screenings", request);
    return response.data;
  },
  getDynamicFallRisk: async (residentId: number): Promise<DynamicFallRiskResult> => {
    const response = await apiClient.get<DynamicFallRiskResult>(`/ai/risk/fall-prediction/${residentId}`);
    return response.data;
  },
  getAllDynamicFallRisks: async (): Promise<DynamicFallRiskResult[]> => {
    const response = await apiClient.get<DynamicFallRiskResult[]>("/ai/risk/fall-predictions");
    return response.data;
  },
  getFallRiskHistory: async (residentId: number, limit = 15): Promise<FallRiskHistoryResponse> => {
    const response = await apiClient.get<FallRiskHistoryResponse>(`/ai/risk/fall-history/${residentId}`, { params: { limit } });
    return response.data;
  },
  recordFallSnapshot: async (residentId: number): Promise<void> => {
    await apiClient.post(`/ai/risk/fall-snapshot/${residentId}`);
  },
  applyFallIntervention: async (request: ApplyFallInterventionRequest): Promise<ApplyFallInterventionResponse> => {
    const response = await apiClient.post<ApplyFallInterventionResponse>("/ai/risk/fall-intervention/apply", request);
    return response.data;
  },
  getMySummaryDrafts: async (): Promise<SavedSummaryDraft[]> => {
    const response = await apiClient.get<SavedSummaryDraft[]>("/ai/summary/drafts");
    return response.data;
  },
  confirmVoiceObservation: async (request: {
    residentId: number;
    taskId?: number;
    transcript: string;
    parsed: import("../types/ai.types").VoiceCareParseResponse;
  }): Promise<{ observationId: number; completedTaskId: number | null; vitalSignId: number | null }> => {
    const response = await apiClient.post<{ observationId: number; completedTaskId: number | null; vitalSignId: number | null }>("/ai/voice/confirm", request);
    return response.data;
  },
  semanticSearch: async (q: string, residentId?: number): Promise<SemanticSearchHit[]> => {
    const response = await apiClient.get<SemanticSearchHit[]>("/ai/search", { params: { q, residentId } });
    return response.data;
  },
  reindexSemanticSearch: async (): Promise<number> => {
    const response = await apiClient.post<{ indexed: number }>("/ai/search/reindex");
    return response.data.indexed;
  },
  getChatHistory: async (conversationId: string): Promise<AiChatTurn[]> => {
    const response = await apiClient.get<AiChatTurn[]>("/ai/chat/history", { params: { conversationId } });
    return response.data;
  },
  reviewElopement: async (request: { residentId: number; cognitiveScore?: number; recentBehaviors: string[]; timeOfDay?: string }): Promise<ElopementReviewResponse> => {
    const response = await apiClient.post<ElopementReviewResponse>("/ai/elopement/evaluate", request);
    return response.data;
  },
  reviewMedication: async (request: { residentId: number; newMedication: string; currentMedications: string[] }): Promise<MedicationReviewResponse> => {
    const response = await apiClient.post<MedicationReviewResponse>("/ai/medication/check", request);
    return response.data;
  },
  draftFromRecords: async (request: ShiftDraftRequest): Promise<ShiftDraftResponse> => {
    const response = await apiClient.post<ShiftDraftResponse>("/ai/summary/records-draft", request);
    return response.data;
  },
  getReviewPriorities: async (): Promise<ResidentReviewPriority[]> => {
    const response = await apiClient.get<ResidentReviewPriority[]>("/ai/risk/review-priorities");
    return response.data;
  },
  checkHealth: async (): Promise<AiHealthResponse> => {
    const response = await apiClient.get<AiHealthResponse>("/ai/health");
    return response.data;
  },

  chat: async (request: AiChatRequest): Promise<AiChatResponse> => {
    const response = await apiClient.post<AiChatResponse>("/ai/chat", request);
    return response.data;
  },

  classifyIncident: async (
    request: IncidentAnalysisRequest
  ): Promise<IncidentAnalysisResponse> => {
    const response = await apiClient.post<IncidentAnalysisResponse>(
      "/ai/incident/classify",
      request
    );
    return response.data;
  },

  suggestCarePlan: async (
    request: import("../types/ai.types").CarePlanSuggestionRequest,
    signal?: AbortSignal
  ): Promise<import("../types/ai.types").CarePlanSuggestionResponse> => {
    const response = await apiClient.post<import("../types/ai.types").CarePlanSuggestionResponse>(
      "/ai/careplan/suggest",
      request,
      { timeout: 90000, signal }
    );
    return response.data;
  },

  generateSummary: async (
    request: import("../types/ai.types").SummaryRequest
  ): Promise<import("../types/ai.types").SummaryResponse> => {
    const response = await apiClient.post<import("../types/ai.types").SummaryResponse>(
      "/ai/summary/generate",
      request
    );
    return response.data;
  },

  parseVoiceNote: async (
    request: import("../types/ai.types").VoiceCareParseRequest
  ): Promise<import("../types/ai.types").VoiceCareParseResponse> => {
    const response = await apiClient.post<import("../types/ai.types").VoiceCareParseResponse>(
      "/ai/voice/parse",
      request
    );
    return response.data;
  },

  getSettings: async (): Promise<import("../types/ai.types").AiSettings> => {
    const response = await apiClient.get<import("../types/ai.types").AiSettings>("/ai/settings");
    return response.data;
  },

  updateSettings: async (
    settings: import("../types/ai.types").AiSettings
  ): Promise<import("../types/ai.types").AiSettings> => {
    const response = await apiClient.put<import("../types/ai.types").AiSettings>("/ai/settings", settings);
    return response.data;
  },

  testConnection: async (
    settings?: Partial<import("../types/ai.types").AiSettings>
  ): Promise<import("../types/ai.types").AiTestResult> => {
    const response = await apiClient.post<import("../types/ai.types").AiTestResult>(
      "/ai/settings/test",
      settings || {}
    );
    return response.data;
  },

  getAvailableModels: async (apiKey?: string): Promise<{ models: string[] }> => {
    const response = await apiClient.get<{ models: string[] }>("/ai/models", {
      params: apiKey ? { apiKey } : undefined,
    });
    return response.data;
  },
};
