package com.eldercare.companion.data.model

import com.google.gson.annotations.SerializedName

// --- RESIDENTS ---
data class ResidentDto(
    @SerializedName("id") val id: Long,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("currentCareLevel") val currentCareLevel: String? = null,
    @SerializedName("gender") val gender: String? = null,
    @SerializedName("roomNumber") val roomNumber: String? = null,
    @SerializedName("bedNumber") val bedNumber: String? = null
) {
    val fullName: String get() = "$firstName $lastName"
}

data class ResidentContainerDto(
    @SerializedName("residents") val residents: List<ResidentDto> = emptyList()
)

data class ResidentApiResponse(
    @SerializedName("statusCode") val statusCode: Int? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: ResidentContainerDto? = null
)

// --- CARE PLANS ---
data class CarePlanItemDto(
    @SerializedName("id") val id: Long,
    @SerializedName("definition") val definition: CarePlanDefinition? = null,
    @SerializedName("LOCTier") val locTier: Int? = null,
    @SerializedName("goalCount") val goalCount: Int = 0,
    @SerializedName("interventionCount") val interventionCount: Int = 0,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("createdBy") val createdBy: CarePlanAuthor? = null,
    @SerializedName("status") val status: String? = "Active",
    @SerializedName("resident") val resident: CarePlanResidentDto? = null
)

data class CarePlanResidentDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("fullname") val fullname: String? = null
)

data class CarePlanDefinition(
    @SerializedName("room") val room: String? = null,
    @SerializedName("bed") val bed: String? = null
)

data class CarePlanAuthor(
    @SerializedName("fullname") val fullname: String? = null,
    @SerializedName("role") val role: String? = null
)

data class CarePlanContainerDto(
    @SerializedName("list") val list: List<CarePlanItemDto> = emptyList()
)

data class CarePlanApiResponse(
    @SerializedName("statusCode") val statusCode: Int? = null,
    @SerializedName("data") val data: CarePlanContainerDto? = null
)

// --- FALL PREDICTIONS (DFRS 2.0) ---
data class DynamicFallRiskDto(
    @SerializedName("residentId") val residentId: Long,
    @SerializedName("residentName") val residentName: String,
    @SerializedName("roomNumber") val roomNumber: String? = null,
    @SerializedName("dfrsScore") val dfrsScore: Int = 0,
    @SerializedName("riskLevel") val riskLevel: String = "LOW",
    @SerializedName("recentFallsCount") val recentFallsCount: Int = 0,
    @SerializedName("activeFridCount") val activeFridCount: Int = 0,
    @SerializedName("contributingFactors") val contributingFactors: List<String> = emptyList(),
    @SerializedName("incidentSubscore") val incidentSubscore: Int = 0,
    @SerializedName("medicationSubscore") val medicationSubscore: Int = 0,
    @SerializedName("adlSubscore") val adlSubscore: Int = 0,
    @SerializedName("vitalSubscore") val vitalSubscore: Int = 0,
    @SerializedName("recommendedInterventions") val recommendedInterventions: List<FallInterventionDto> = emptyList()
)

data class FallInterventionDto(
    @SerializedName("role") val role: String? = null,
    @SerializedName("priority") val priority: String? = null,
    @SerializedName("action") val action: String? = null,
    @SerializedName("rationale") val rationale: String? = null
)

// --- INCIDENTS ---
data class IncidentDto(
    @SerializedName("id") val id: Long,
    @SerializedName("incidentType") val incidentType: String,
    @SerializedName("status") val status: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("location") val location: String? = null,
    @SerializedName("severity") val severity: IncidentSeverityDto? = null,
    @SerializedName("resident") val resident: IncidentResidentDto? = null
)

data class IncidentSeverityDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("levelName") val levelName: String? = null
)

data class IncidentResidentDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("displayName") val displayName: String? = null
)

data class PagedIncidentsResponse(
    @SerializedName("content") val content: List<IncidentDto> = emptyList()
)

// --- RESIDENT CHART CONTEXT (Medications & Vitals) ---
data class ResidentChartContextDto(
    @SerializedName("residentId") val residentId: Long,
    @SerializedName("residentName") val residentName: String? = null,
    @SerializedName("roomNumber") val roomNumber: String? = null,
    @SerializedName("age") val age: Int? = null,
    @SerializedName("gender") val gender: String? = null,
    @SerializedName("careLevel") val careLevel: String? = null,
    @SerializedName("adlScore") val adlScore: Int? = null,
    @SerializedName("cognitiveScore") val cognitiveScore: Int? = null,
    @SerializedName("activeMedications") val activeMedications: List<MedicationPrescriptionDto> = emptyList(),
    @SerializedName("recentVitals") val recentVitals: List<VitalRecordDto> = emptyList(),
    @SerializedName("recentIncidents") val recentIncidents: List<RecentChartIncidentDto> = emptyList(),
    @SerializedName("diagnoses") val diagnoses: List<String> = emptyList(),
    @SerializedName("documentedBehaviors") val documentedBehaviors: List<String> = emptyList()
)

// --- CARE PLAN DETAILS ---
data class CarePlanDetailDto(
    @SerializedName("id") val id: Long,
    @SerializedName("status") val status: String? = null,
    @SerializedName("locTier") val locTier: Int? = null,
    @SerializedName("significantFlag") val significantFlag: Boolean? = null,
    @SerializedName("resident") val resident: CarePlanResidentOutputDto? = null,
    @SerializedName("definition") val definition: CarePlanDefinitionOutputDto? = null,
    @SerializedName("lastReviewedBy") val lastReviewedBy: String? = null,
    @SerializedName("lastReviewedDateTime") val lastReviewedDateTime: String? = null,
    @SerializedName("nextReviewDateTime") val nextReviewDateTime: String? = null,
    @SerializedName("cycle") val cycle: Int? = null,
    @SerializedName("goals") val goals: List<CareGoalDetailDto> = emptyList(),
    @SerializedName("createdAt") val createdAt: String? = null
)

data class CarePlanResidentOutputDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("fullname") val fullname: String? = null,
    @SerializedName("fullName") val fullName: String? = null
) {
    val displayName: String get() = fullname ?: fullName ?: "Cư dân"
}

data class CarePlanDefinitionOutputDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("category") val category: String? = null
)

data class CareGoalDetailDto(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String? = null,
    @SerializedName("goalDescription") val goalDescription: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("interventions") val interventions: List<CareInterventionDetailDto> = emptyList()
)

data class CareInterventionDetailDto(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String? = null,
    @SerializedName("assignedRole") val assignedRole: String? = null
)

data class RecentChartIncidentDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("incidentType") val incidentType: String? = null,
    @SerializedName("severity") val severity: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("location") val location: String? = null
)

data class MedicationPrescriptionDto(
    @SerializedName("id") val id: Long,
    @SerializedName("medicationName") val medicationName: String,
    @SerializedName("dosage") val dosage: String? = null,
    @SerializedName("route") val route: String? = null,
    @SerializedName("frequency") val frequency: String? = null,
    @SerializedName("status") val status: String? = "ACTIVE",
    @SerializedName("frid") val isFrid: Boolean = false,
    @SerializedName("fridCategory") val fridCategory: String? = null,
    @SerializedName("clinicalWarning") val clinicalWarning: String? = null
)

data class VitalRecordDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("bloodPressure") val bloodPressure: String? = null,
    @SerializedName("heartRate") val heartRate: Int? = null,
    @SerializedName("spo2") val spo2: Int? = null,
    @SerializedName("temperature") val temperature: Double? = null,
    @SerializedName("recordedAt") val recordedAt: String? = null
)

// --- SHIFT HANDOVER DRAFTS ---
data class ShiftHandoverDraftDto(
    @SerializedName("id") val id: Long,
    @SerializedName("audience") val audience: String? = null,
    @SerializedName("start") val start: String? = null,
    @SerializedName("end") val end: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("sourceEvents") val sourceEvents: List<String> = emptyList(),
    @SerializedName("draft") val draft: ShiftHandoverContentDto? = null
)

data class ShiftHandoverContentDto(
    @SerializedName("title") val title: String? = null,
    @SerializedName("narrativeSummary") val narrativeSummary: String? = null,
    @SerializedName("keyHighlights") val keyHighlights: List<String> = emptyList(),
    @SerializedName("handoverActionItems") val handoverActionItems: List<String> = emptyList(),
    @SerializedName("generatedAt") val generatedAt: String? = null,
    @SerializedName("proposedActions") val proposedActions: List<HandoverProposedActionDto>? = emptyList(),
    @SerializedName("generationMethod") val generationMethod: String? = null
)

data class HandoverProposedActionDto(
    @SerializedName("residentId") val residentId: Long? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("taskType") val taskType: String? = null
)

// --- VOICE OBSERVATION ---
data class VoiceCareParseResponse(
    @SerializedName("residentIdentifier") val residentIdentifier: String? = null,
    @SerializedName("roomNumber") val roomNumber: String? = null,
    @SerializedName("dietaryIntakePercentage") val dietaryIntakePercentage: Int? = null,
    @SerializedName("bloodPressureSystolic") val bloodPressureSystolic: Int? = null,
    @SerializedName("bloodPressureDiastolic") val bloodPressureDiastolic: Int? = null,
    @SerializedName("heartRateBpm") val heartRateBpm: Int? = null,
    @SerializedName("bodyTemperatureCelsius") val bodyTemperatureCelsius: Double? = null,
    @SerializedName("medicationTaken") val medicationTaken: Boolean? = null,
    @SerializedName("extractedNotes") val extractedNotes: String? = null,
    @SerializedName("confidence") val confidence: Double? = null
)

data class VoiceParseRequest(
    @SerializedName("spokenText") val spokenText: String,
    @SerializedName("language") val language: String = "vi"
)

data class VoiceConfirmRequest(
    @SerializedName("residentId") val residentId: Long = 1L,
    @SerializedName("taskId") val taskId: Long? = null,
    @SerializedName("transcript") val transcript: String,
    @SerializedName("parsed") val parsed: VoiceCareParseResponse? = null
)

data class VoiceConfirmResponse(
    @SerializedName("observationId") val observationId: Long? = null,
    @SerializedName("completedTaskId") val completedTaskId: Long? = null,
    @SerializedName("vitalSignId") val vitalSignId: Long? = null
)

// --- AI CHATBOT ---
data class AiChatRequest(
    @SerializedName("message") val message: String,
    @SerializedName("context") val context: String? = null,
    @SerializedName("conversationId") val conversationId: String? = null
)

data class AiChatResponse(
    @SerializedName("reply") val reply: String? = null,
    @SerializedName("model") val model: String? = null,
    @SerializedName("success") val success: Boolean = true,
    @SerializedName("conversationId") val conversationId: String? = null,
    @SerializedName("sources") val sources: List<String> = emptyList()
)

// --- AI MEDICATION CHECK ---
data class MedicationCheckRequest(
    @SerializedName("residentId") val residentId: Long,
    @SerializedName("residentName") val residentName: String? = null,
    @SerializedName("currentMedications") val currentMedications: List<String> = emptyList(),
    @SerializedName("newMedication") val newMedication: String? = null,
    @SerializedName("allergies") val allergies: List<String> = emptyList(),
    @SerializedName("medicalConditions") val medicalConditions: List<String> = emptyList()
)

data class MedicationCheckResponse(
    @SerializedName("riskLevel") val riskLevel: String? = null,
    @SerializedName("isSafeToAdminister") val isSafeToAdminister: Boolean? = true,
    @SerializedName("summaryWarning") val summaryWarning: String? = null,
    @SerializedName("clinicalPrecautions") val clinicalPrecautions: List<String> = emptyList(),
    @SerializedName("totalAcbScore") val totalAcbScore: Int? = null,
    @SerializedName("acbRiskTier") val acbRiskTier: String? = null,
    @SerializedName("acbClinicalImpact") val acbClinicalImpact: String? = null
)

// --- AI SUMMARY GENERATION ---
data class SummaryRequest(
    @SerializedName("type") val type: String = "SHIFT_HANDOVER",
    @SerializedName("tone") val tone: String = "CLINICAL",
    @SerializedName("shiftName") val shiftName: String? = null,
    @SerializedName("events") val events: List<String> = emptyList(),
    @SerializedName("residentName") val residentName: String? = null,
    @SerializedName("extraNotes") val extraNotes: String? = null
)

data class SummaryResponse(
    @SerializedName("title") val title: String? = null,
    @SerializedName("narrativeSummary") val narrativeSummary: String? = null,
    @SerializedName("keyHighlights") val keyHighlights: List<String> = emptyList(),
    @SerializedName("handoverActionItems") val handoverActionItems: List<String> = emptyList(),
    @SerializedName("generationMethod") val generationMethod: String? = null
)
