export interface AiChatRequest {
  message: string;
  context?: string;
  conversationId?: string;
}

export interface AiChatResponse {
  reply: string;
  model: string;
  success: boolean;
  conversationId?: string;
  sources?: string[];
}

export interface AiChatTurn {
  role: "user" | "assistant";
  content: string;
}

export interface IncidentAnalysisRequest {
  description: string;
  residentInfo?: string;
  location?: string;
}

export interface IncidentAnalysisResponse {
  suggestedSeverity: "LOW" | "MEDIUM" | "HIGH" | "CRITICAL" | "EMERGENCY" | "REVIEW_REQUIRED";
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

export interface ResidentReviewPriority {
  residentId: number;
  residentName: string;
  incidentCount: number;
  recentIncidentCount: number;
  currentAdlScore: number | null;
  assessedAt: string | null;
  assessmentStatus: "CONFIRMED" | "UNVERIFIED" | null;
  morseScore: number | null;
  morseAssessedAt: string | null;
  priorityScore: number;
  priority: "HIGH" | "REVIEW";
  factors: string[];
  recommendedAction: string;
  generatedAt: string;
  methodVersion: string;
  sourceIncidentIds: number[];
  sourceAssessmentId: number | null;
  sourceScreeningId: number | null;
  dataGaps: string[];
  dfrsScore?: number;
  fallRiskLevel?: "HIGH" | "MODERATE" | "LOW";
  fallInterventions?: string[];
  incidentSubscore?: number;
  medicationSubscore?: number;
  adlSubscore?: number;
  vitalSubscore?: number;
  activeFridNames?: string[];
}

export interface FallIntervention {
  role: "CNA" | "NURSE" | "DOCTOR" | "PT";
  priority: "IMMEDIATE" | "HIGH" | "ROUTINE";
  action: string;
  rationale: string;
}

export interface FridMatch {
  medicationName: string;
  category: string;
  riskWeight: number;
  clinicalWarning: string;
}

export interface DynamicFallRiskResult {
  residentId: number;
  residentName: string;
  roomNumber: string;
  dfrsScore: number;
  riskLevel: "HIGH" | "MODERATE" | "LOW";
  incidentSubscore: number;
  medicationSubscore: number;
  adlSubscore: number;
  vitalSubscore: number;
  recentFallsCount: number;
  activeFridCount: number;
  contributingFactors: string[];
  activeFridMedications: FridMatch[];
  recommendedInterventions: FallIntervention[];
  clinicalDisclaimer: string;
  assessedAt: string;
  methodVersion: string;
}

export interface FallScreeningRequest {
  residentId: number;
  historyOfFalling: number;
  secondaryDiagnosis: number;
  ambulatoryAid: number;
  ivTherapy: number;
  gait: number;
  mentalStatus: number;
}

export interface FallScreeningResult {
  id: number;
  residentId: number;
  totalScore: number;
  assessedAt: string;
  assessedBy: string;
  interpretation: string;
  scaleName: string;
  sourceUrl: string;
}

export interface SemanticSearchHit {
  sourceType: "WORKFLOW" | "INCIDENT" | "CARE_GOAL";
  sourceId: string;
  residentId: number | null;
  title: string;
  excerpt: string;
  similarity: number;
}

export interface CarePlanSuggestionRequest {
  residentId: number;
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
  generationMethod?: "AI" | "UNAVAILABLE";
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
  generationMethod?: "AI" | "RECORDED_INPUT_FALLBACK";
  proposedActions?: HandoverSuggestion[];
}

export interface HandoverSource {
  reference: string;
  residentId: number;
  residentName: string;
  description: string;
}
export interface HandoverSuggestion {
  residentId: number;
  description: string;
  taskType: string;
  sourceRefs: string[];
}
export interface HandoverAction extends HandoverSuggestion {
  id: string;
  careTaskId: number | null;
  assignedBy: string | null;
  assignedAt: string | null;
}
export interface HandoverDetail {
  id: number;
  status: "DRAFT" | "APPROVED";
  createdBy: string;
  approvedBy: string | null;
  approvedAt: string | null;
  start: string;
  end: string;
  summary: SummaryResponse;
  sources: HandoverSource[];
  actions: { action: HandoverAction; residentName: string; taskStatus: string | null;
    assignedCnaId: number | null; assignedCnaName: string | null; scheduledTime: string | null }[];
  canApprove: boolean;
}

export interface ShiftDraftRequest {
  start: string;
  end: string;
  residentId?: number;
  audience: "STAFF" | "FAMILY";
  verifiedNotes?: string;
}

export interface ShiftDraftResponse {
  draftId: number;
  sourceEvents: string[];
  draft: SummaryResponse;
  reviewRequired: boolean;
}

export interface SavedSummaryDraft {
  id: number;
  audience: "STAFF" | "FAMILY";
  residentId: number | null;
  start: string;
  end: string;
  createdAt: string;
  sourceEvents: string[];
  draft: SummaryResponse;
}

export interface ElopementReviewResponse {
  riskLevel: string;
  riskScore: number | null;
  triggerDoorLockAlert: boolean;
  behavioralAnalysis: string;
  immediateSafeguards: string[];
}

export interface AcbScoreDetail {
  medicationName: string;
  score: number;
  therapeuticClass: string;
  alternativeOption: string;
}

export interface BeersAlert {
  medicationName: string;
  category: string;
  severity: "HIGH" | "MODERATE" | "CAUTION" | string;
  rationale: string;
  recommendation: string;
  saferAlternative: string;
  evidenceLevel: string;
}

export interface StoppAlert {
  ruleCode: string;
  primaryMedication: string;
  secondaryMedication: string;
  interactionType: string;
  severity: "HIGH" | "CRITICAL" | "MODERATE" | string;
  clinicalExplanation: string;
  actionRecommendation: string;
}

export interface DeprescribingPlan {
  targetMedication: string;
  indicationReview: string;
  proposedAction: "TAPER_DISCONTINUE" | "SWITCH_SAFER_AGENT" | "DOSE_REDUCTION" | string;
  alternativeMedication: string;
  clinicalMonitoringNotes: string;
}

export interface MedicationReviewResponse {
  riskLevel: string;
  isSafeToAdminister: boolean;
  summaryWarning: string;
  interactions: { interactingSubstance: string; severity: string; mechanism: string; recommendation: string }[];
  clinicalPrecautions: string[];
  chartMedications: string[];
  manuallyReportedMedications: string[];
  chartAllergies: string[];
  chartConditions: string[];
  recordChecks: string[];
  generatedAt: string;
  methodVersion: string;
  interactionReferenceStatus: "NOT_CONNECTED" | "ACTIVE_AGS_BEERS_2023_STOPP_V3" | string;
  sourceOrderIds: number[];
  sourceAllergyRecordIds: number[];
  sourceDiagnosisRecordIds: number[];
  dataGaps: string[];

  // Geriatric CDSS additions (AGS Beers 2023, STOPP v3, ACB Score)
  totalAcbScore?: number;
  acbRiskTier?: "MINIMAL" | "MODERATE" | "HIGH";
  acbClinicalImpact?: string;
  acbContributingMedications?: AcbScoreDetail[];
  beersCriteriaAlerts?: BeersAlert[];
  stoppAlerts?: StoppAlert[];
  deprescribingGuidance?: DeprescribingPlan[];
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

export interface FallRiskSnapshotItem {
  id: number;
  dfrsScore: number;
  riskLevel: "HIGH" | "MODERATE" | "LOW";
  incidentSubscore: number;
  medicationSubscore: number;
  adlSubscore: number;
  vitalSubscore: number;
  recentFallsCount: number;
  activeFridCount: number;
  assessedAt: string;
  assessedBy?: string;
}

export interface FallRiskHistoryResponse {
  residentId: number;
  residentName: string;
  currentScore: number;
  currentRiskLevel: "HIGH" | "MODERATE" | "LOW";
  trendDirection: "IMPROVING" | "WORSENING" | "STABLE";
  snapshots: FallRiskSnapshotItem[];
}

export interface ApplyFallInterventionRequest {
  residentId: number;
  action: string;
  role?: string;
  priority?: string;
  rationale?: string;
  scheduledTime?: string;
}

export interface ApplyFallInterventionResponse {
  success: boolean;
  message: string;
  carePlanId: number;
  careGoalId: number;
  careInterventionId: number;
  careTaskId: number;
  assignedRole: string;
  action: string;
  taskStatus: string;
  scheduledTime: string;
}

export interface ChartMedication {
  id: number;
  medicationName: string;
  dosage?: string;
  route?: string;
  frequency?: string;
  status?: string;
  isFrid: boolean;
  fridCategory?: string;
  clinicalWarning?: string;
}

export interface ChartIncident {
  id: number;
  incidentType: string;
  severity: string;
  occurredAt?: string;
  description: string;
  location?: string;
}

export interface ResidentChartContext {
  residentId: number;
  residentName: string;
  roomNumber?: string;
  age?: number;
  gender?: string;
  careLevel?: string;
  adlScore?: number;
  cognitiveScore?: number;
  activeMedications: ChartMedication[];
  recentIncidents: ChartIncident[];
  diagnoses: string[];
  documentedBehaviors: string[];
}