package com.eldercare.companion.data.repository

import com.eldercare.companion.data.SessionManager
import com.eldercare.companion.data.model.*
import com.eldercare.companion.data.network.ApiClient
import com.eldercare.companion.data.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class CareRepository(private val sessionManager: SessionManager) {

    private fun getApi(): ApiService = ApiClient.getService(sessionManager)

    suspend fun ensureAuthenticated(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val api = getApi()
                val targetUser = sessionManager.cnaEmail?.ifBlank { null } ?: "daniel.brooks@nhms-demo.local"
                val targetPwd = sessionManager.userPassword?.ifBlank { null }
                    ?: if (targetUser.contains("daniel.brooks", ignoreCase = true)) "Nhms@Demo2026" else "123456"
                val loginRes = api.login(LoginRequest(username = targetUser.trim(), password = targetPwd))
                if (loginRes.isSuccessful && loginRes.body() != null) {
                    val body = loginRes.body()!!
                    sessionManager.saveLogin(
                        token = body.token,
                        sessionId = body.sessionId,
                        role = body.role,
                        name = body.name
                    )
                    sessionManager.cnaEmail = targetUser
                    sessionManager.userPassword = targetPwd
                    sessionManager.cnaName = body.name
                    sessionManager.cnaRole = body.role
                    if (sessionManager.cnaId <= 0) {
                        sessionManager.cnaId = 2L
                    }
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                false
            }
        }
    }

    suspend fun login(username: String, password: String, customBaseUrl: String? = null): Result<CurrentUser> {
        return withContext(Dispatchers.IO) {
            try {
                if (!customBaseUrl.isNullOrBlank()) {
                    sessionManager.baseUrl = customBaseUrl.trim()
                    ApiClient.invalidateCache()
                }

                val api = getApi()
                val loginRes = api.login(LoginRequest(username = username.trim(), password = password))
                if (!loginRes.isSuccessful || loginRes.body() == null) {
                    val errorMsg = loginRes.errorBody()?.string() ?: "Đăng nhập không thành công (${loginRes.code()})"
                    return@withContext Result.failure(Exception(errorMsg))
                }

                val body = loginRes.body()!!
                sessionManager.saveLogin(
                    token = body.token,
                    sessionId = body.sessionId,
                    role = body.role,
                    name = body.name
                )
                sessionManager.cnaEmail = username.trim()
                sessionManager.userPassword = password

                // Fetch current user details (id, roleName, email)
                val meRes = api.getCurrentUser()
                if (meRes.isSuccessful && meRes.body() != null) {
                    val me = meRes.body()!!
                    sessionManager.saveCurrentUser(
                        id = me.id,
                        email = me.email,
                        fullName = me.fullName.ifBlank { body.name },
                        role = me.roleName.ifBlank { body.role }
                    )
                    Result.success(me)
                } else {
                    // Fallback using data from LoginResponse
                    val fallbackUser = CurrentUser(
                        id = 2L,
                        email = username,
                        firstName = body.name,
                        lastName = "",
                        roleName = body.role
                    )
                    sessionManager.cnaId = 2L
                    Result.success(fallbackUser)
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun fetchShiftTasks(): Result<List<EnrichedTaskRow>> {
        return withContext(Dispatchers.IO) {
            try {
                if (sessionManager.authToken.isNullOrBlank()) {
                    ensureAuthenticated()
                }

                val targetCnaId = if (sessionManager.cnaId > 0) sessionManager.cnaId else 2L
                var res = getApi().getTasksByCna(assignedCnaId = targetCnaId, size = 100)

                if (res.code() == 401 || res.code() == 403) {
                    ensureAuthenticated()
                    res = getApi().getTasksByCna(assignedCnaId = targetCnaId, size = 100)
                }

                if (!res.isSuccessful || res.body() == null) {
                    return@withContext Result.failure(Exception("Không thể tải danh sách ca trực (${res.code()})"))
                }

                val paged = res.body()!!
                val cnaCards = paged.data ?: emptyList()

                val allTasks = mutableListOf<EnrichedTaskRow>()
                for (card in cnaCards) {
                    allTasks.addAll(card.tasks)
                }

                Result.success(allTasks)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun completeTask(taskId: Long): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val nowIso = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                var res = getApi().completeTask(taskId, CompleteTaskRequest(completedAt = nowIso))
                if (res.code() == 401 || res.code() == 403) {
                    ensureAuthenticated()
                    res = getApi().completeTask(taskId, CompleteTaskRequest(completedAt = nowIso))
                }

                if (res.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Lỗi cập nhật hoàn thành: ${res.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun flagAbnormal(taskId: Long, isAbnormal: Boolean): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                var res = getApi().flagAbnormal(taskId, FlagAbnormalRequest(isAbnormalFlagged = isAbnormal))
                if (res.code() == 401 || res.code() == 403) {
                    ensureAuthenticated()
                    res = getApi().flagAbnormal(taskId, FlagAbnormalRequest(isAbnormalFlagged = isAbnormal))
                }

                if (res.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Lỗi gắn cờ bất thường: ${res.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // --- REAL CLINICAL WEB REPOSITORY APIS ---

    suspend fun fetchResidents(): Result<List<ResidentDto>> {
        return withContext(Dispatchers.IO) {
            try {
                var res = getApi().getResidents(page = 1, pageSize = 50)
                if (res.code() == 401 || res.code() == 403) {
                    ensureAuthenticated()
                    res = getApi().getResidents(page = 1, pageSize = 50)
                }
                if (res.isSuccessful && res.body() != null) {
                    val list = res.body()!!.data?.residents ?: emptyList()
                    Result.success(list)
                } else {
                    Result.failure(Exception("Lỗi tải danh sách cư dân: ${res.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun fetchCarePlans(): Result<List<CarePlanItemDto>> {
        return withContext(Dispatchers.IO) {
            try {
                var res = getApi().getCarePlans(page = 0, size = 50)
                if (res.code() == 401 || res.code() == 403) {
                    ensureAuthenticated()
                    res = getApi().getCarePlans(page = 0, size = 50)
                }
                if (res.isSuccessful && res.body() != null) {
                    val list = res.body()!!.data?.list ?: emptyList()
                    Result.success(list)
                } else {
                    Result.failure(Exception("Lỗi tải danh sách kế hoạch chăm sóc: ${res.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun fetchCarePlanDetail(carePlanId: Long): Result<CarePlanDetailDto> {
        return withContext(Dispatchers.IO) {
            try {
                var res = getApi().getCarePlanDetail(carePlanId)
                if (res.code() == 401 || res.code() == 403) {
                    ensureAuthenticated()
                    res = getApi().getCarePlanDetail(carePlanId)
                }
                if (res.isSuccessful && res.body()?.data != null) {
                    Result.success(res.body()!!.data!!)
                } else {
                    Result.failure(Exception("Lỗi tải chi tiết kế hoạch chăm sóc: ${res.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun fetchFallPredictions(): Result<List<DynamicFallRiskDto>> {
        return withContext(Dispatchers.IO) {
            try {
                var res = getApi().getFallPredictions()
                if (res.code() == 401 || res.code() == 403) {
                    ensureAuthenticated()
                    res = getApi().getFallPredictions()
                }
                if (res.isSuccessful && res.body() != null) {
                    Result.success(res.body()!!)
                } else {
                    Result.failure(Exception("Lỗi tải đánh giá nguy cơ té ngã: ${res.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun fetchIncidents(): Result<List<IncidentDto>> {
        return withContext(Dispatchers.IO) {
            try {
                var res = getApi().getIncidents(page = 0, size = 50)
                if (res.code() == 401 || res.code() == 403) {
                    ensureAuthenticated()
                    res = getApi().getIncidents(page = 0, size = 50)
                }
                if (res.isSuccessful && res.body() != null) {
                    Result.success(res.body()!!.content)
                } else {
                    Result.failure(Exception("Lỗi tải báo cáo sự cố: ${res.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun fetchResidentChartContext(residentId: Long): Result<ResidentChartContextDto> {
        return withContext(Dispatchers.IO) {
            try {
                var res = getApi().getResidentChartContext(residentId)
                if (res.code() == 401 || res.code() == 403) {
                    ensureAuthenticated()
                    res = getApi().getResidentChartContext(residentId)
                }
                if (res.isSuccessful && res.body() != null) {
                    Result.success(res.body()!!)
                } else {
                    Result.failure(Exception("Lỗi tải hồ sơ bệnh án: ${res.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun fetchSummaryDrafts(): Result<List<ShiftHandoverDraftDto>> {
        return withContext(Dispatchers.IO) {
            try {
                var res = getApi().getSummaryDrafts()
                if (res.code() == 401 || res.code() == 403) {
                    ensureAuthenticated()
                    res = getApi().getSummaryDrafts()
                }
                if (res.isSuccessful && res.body() != null) {
                    Result.success(res.body()!!)
                } else {
                    Result.failure(Exception("Lỗi tải báo cáo giao ca: ${res.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun parseVoiceNote(spokenText: String): Result<VoiceCareParseResponse> {
        return withContext(Dispatchers.IO) {
            try {
                var res = getApi().parseVoiceNote(VoiceParseRequest(spokenText = spokenText))
                if (res.code() == 401 || res.code() == 403) {
                    ensureAuthenticated()
                    res = getApi().parseVoiceNote(VoiceParseRequest(spokenText = spokenText))
                }
                if (res.isSuccessful && res.body() != null) {
                    Result.success(res.body()!!)
                } else {
                    Result.failure(Exception("Lỗi bóc tách ghi chú giọng nói: ${res.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun confirmVoiceObservation(
        residentId: Long,
        taskId: Long?,
        transcript: String,
        parsed: VoiceCareParseResponse? = null
    ): Result<VoiceConfirmResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val req = VoiceConfirmRequest(
                    residentId = residentId,
                    taskId = taskId,
                    transcript = transcript,
                    parsed = parsed
                )
                var res = getApi().confirmVoiceObservation(req)
                if (res.code() == 401 || res.code() == 403) {
                    ensureAuthenticated()
                    res = getApi().confirmVoiceObservation(req)
                }
                if (res.isSuccessful && res.body() != null) {
                    Result.success(res.body()!!)
                } else {
                    Result.failure(Exception("Lỗi lưu ghi chú lên Admin Web: ${res.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun sendAiChat(message: String, conversationId: String? = null): Result<AiChatResponse> {
        return withContext(Dispatchers.IO) {
            try {
                var res = getApi().chat(AiChatRequest(message = message, conversationId = conversationId))
                if (res.code() == 401 || res.code() == 403) {
                    ensureAuthenticated()
                    res = getApi().chat(AiChatRequest(message = message, conversationId = conversationId))
                }
                if (res.isSuccessful && res.body() != null) {
                    Result.success(res.body()!!)
                } else {
                    Result.failure(Exception("Lỗi giao tiếp trợ lý AI: ${res.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun checkMedicationSafety(
        residentId: Long,
        residentName: String?,
        currentMeds: List<String>
    ): Result<MedicationCheckResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val req = MedicationCheckRequest(
                    residentId = residentId,
                    residentName = residentName,
                    currentMedications = currentMeds
                )
                var res = getApi().checkMedicationSafety(req)
                if (res.code() == 401 || res.code() == 403) {
                    ensureAuthenticated()
                    res = getApi().checkMedicationSafety(req)
                }
                if (res.isSuccessful && res.body() != null) {
                    Result.success(res.body()!!)
                } else {
                    Result.failure(Exception("Lỗi rà soát an toàn thuốc: ${res.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun generateShiftSummary(
        shiftName: String,
        events: List<String>
    ): Result<SummaryResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val req = SummaryRequest(
                    type = "SHIFT_HANDOVER",
                    tone = "CLINICAL",
                    shiftName = shiftName,
                    events = events
                )
                var res = getApi().generateSummary(req)
                if (res.code() == 401 || res.code() == 403) {
                    ensureAuthenticated()
                    res = getApi().generateSummary(req)
                }
                if (res.isSuccessful && res.body() != null) {
                    Result.success(res.body()!!)
                } else {
                    Result.failure(Exception("Lỗi tổng hợp giao ban ca: ${res.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    fun logout() {
        sessionManager.clearSession()
        ApiClient.invalidateCache()
    }
}
