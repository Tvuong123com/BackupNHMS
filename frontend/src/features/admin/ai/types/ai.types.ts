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
  features?: string[];
}

export interface CarePlanSuggestionRequest {
  residentName?: string;
  age?: number;
  gender?: string;
  careLevel?: string;
  adlScore?: number;
  diagnoses?: string[];
  recentIncidents?: string[];
  existingGoals?: string[];
}

export interface SuggestedIntervention {
  name: string;
  assignedRole: string;
}

export interface SuggestedGoal {
  goalName: string;
  description: string;
  priority: "HIGH" | "MEDIUM" | "LOW";
  rationale: string;
  interventions: SuggestedIntervention[];
}

export interface CarePlanSuggestionResponse {
  residentSummary: string;
  suggestedGoals: SuggestedGoal[];
}

export interface SummaryRequest {
  type?: "SHIFT_HANDOVER" | "INCIDENT_REPORT" | "FAMILY_UPDATE";
  tone?: "CLINICAL" | "FAMILY_FRIENDLY" | "EXECUTIVE";
  shiftName?: string;
  events?: string[];
  residentName?: string;
  extraNotes?: string;
}

export interface SummaryResponse {
  title: string;
  narrativeSummary: string;
  keyHighlights: string[];
  handoverActionItems: string[];
  generatedAt: string;
}

export interface VoiceCareParseRequest {
  spokenText: string;
  expectedResidentName?: string;
  expectedRoomNumber?: string;
}

export interface VoiceCareParseResponse {
  residentIdentifier?: string;
  roomNumber?: string;
  dietaryIntakePercentage?: number;
  bloodPressureSystolic?: number;
  bloodPressureDiastolic?: number;
  heartRateBpm?: number;
  bodyTemperatureCelsius?: number;
  medicationTaken?: boolean;
  extractedNotes?: string;
  confidence: number;
}

export interface AiSettings {
  provider: "LOCAL_OLLAMA" | "GOOGLE_GEMINI";
  geminiApiKey?: string;
  geminiModel?: string;
  ollamaBaseUrl?: string;
  ollamaModel?: string;
  temperature?: number;
  maxTokens?: number;
  systemRules?: string;
  features?: Record<string, boolean>;
}

export interface AiTestResult {
  success: boolean;
  latencyMs: number;
  provider: string;
  model: string;
  message: string;
  availableModels?: string[];
}

