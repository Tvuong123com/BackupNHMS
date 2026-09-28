export interface AiChatRequest {
  message: string;
  context?: string;
}

export interface AiChatResponse {
  reply: string;
  model: string;
  success: boolean;
}

export interface IncidentAnalysisRequest {
  description: string;
  residentInfo?: string;
  location?: string;
}

export interface IncidentAnalysisResponse {
  suggestedSeverity: "LOW" | "MEDIUM" | "HIGH" | "CRITICAL" | "EMERGENCY";
  suggestedIncidentType: "FALL" | "MEDICATION_ERROR" | "ELOPEMENT" | "BEHAVIORAL" | "MEDICAL_EMERGENCY" | "INJURY" | "OTHER";
  rationale: string;
  recommendedActions: string[];
  confidence: number;
}

export interface AiHealthResponse {
  status: string;
  provider: string;
  model: string;
  endpoint: string;
  message: string;
}
