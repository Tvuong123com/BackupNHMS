import { apiClient } from "@/lib/api-client";
import type {
  AiChatRequest,
  AiChatResponse,
  AiHealthResponse,
  IncidentAnalysisRequest,
  IncidentAnalysisResponse,
} from "../types/ai.types";

export const aiService = {
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
    request: import("../types/ai.types").CarePlanSuggestionRequest
  ): Promise<import("../types/ai.types").CarePlanSuggestionResponse> => {
    const response = await apiClient.post<import("../types/ai.types").CarePlanSuggestionResponse>(
      "/ai/careplan/suggest",
      request
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
};

