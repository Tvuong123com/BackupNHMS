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
};
