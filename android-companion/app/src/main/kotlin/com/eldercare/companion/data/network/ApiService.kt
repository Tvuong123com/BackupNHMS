package com.eldercare.companion.data.network

import com.eldercare.companion.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @GET("auth/me")
    suspend fun getCurrentUser(): Response<CurrentUser>

    @GET("tasks/by-cna")
    suspend fun getTasksByCna(
        @Query("assignedCnaId") assignedCnaId: Long? = null,
        @Query("status") status: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 100
    ): Response<PagedResponse<List<GroupedByCnaCard>>>

    @PATCH("tasks/{taskId}/completed")
    suspend fun completeTask(
        @Path("taskId") taskId: Long,
        @Body request: CompleteTaskRequest
    ): Response<ApiResponse<Any>>

    @PATCH("tasks/{taskId}/flag-abnormal")
    suspend fun flagAbnormal(
        @Path("taskId") taskId: Long,
        @Body request: FlagAbnormalRequest
    ): Response<ApiResponse<Any>>

    // --- REAL CLINICAL WEB ENDPOINTS ---

    @GET("residents")
    suspend fun getResidents(
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 50
    ): Response<ResidentApiResponse>

    @GET("care-plans")
    suspend fun getCarePlans(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 50
    ): Response<CarePlanApiResponse>

    @GET("care-plans/{carePlanId}")
    suspend fun getCarePlanDetail(
        @Path("carePlanId") carePlanId: Long
    ): Response<ApiResponse<CarePlanDetailDto>>

    @GET("ai/risk/fall-predictions")
    suspend fun getFallPredictions(): Response<List<DynamicFallRiskDto>>

    @GET("admin/incidents")
    suspend fun getIncidents(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 50
    ): Response<PagedIncidentsResponse>

    @GET("ai/resident-chart-context/{residentId}")
    suspend fun getResidentChartContext(
        @Path("residentId") residentId: Long
    ): Response<ResidentChartContextDto>

    @GET("ai/summary/drafts")
    suspend fun getSummaryDrafts(): Response<List<ShiftHandoverDraftDto>>

    @POST("ai/voice/parse")
    suspend fun parseVoiceNote(
        @Body request: VoiceParseRequest
    ): Response<VoiceCareParseResponse>

    @POST("ai/voice/confirm")
    suspend fun confirmVoiceObservation(
        @Body request: VoiceConfirmRequest
    ): Response<VoiceConfirmResponse>

    @POST("ai/chat")
    suspend fun chat(
        @Body request: AiChatRequest
    ): Response<AiChatResponse>

    @POST("ai/medication/check")
    suspend fun checkMedicationSafety(
        @Body request: MedicationCheckRequest
    ): Response<MedicationCheckResponse>

    @POST("ai/summary/generate")
    suspend fun generateSummary(
        @Body request: SummaryRequest
    ): Response<SummaryResponse>
}
