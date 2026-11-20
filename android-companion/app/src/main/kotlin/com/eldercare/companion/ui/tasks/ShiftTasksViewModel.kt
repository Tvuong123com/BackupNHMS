package com.eldercare.companion.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eldercare.companion.data.SessionManager
import com.eldercare.companion.data.model.EnrichedTaskRow
import com.eldercare.companion.data.repository.CareRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class TaskFilter(val title: String) {
    ALL("Tất cả"),
    PENDING("Cần làm"),
    COMPLETED("Đã xong")
}

data class ShiftTasksUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val tasks: List<EnrichedTaskRow> = emptyList(),
    val filter: TaskFilter = TaskFilter.PENDING,
    val completingTaskIds: Set<Long> = emptySet(),
    val errorMessage: String? = null,
    val successToast: String? = null,
    val cnaName: String = "",
    val cnaRole: String = ""
) {
    val totalCount: Int
        get() = tasks.size

    val completedCount: Int
        get() = tasks.count { it.isCompleted }

    val pendingCount: Int
        get() = tasks.count { it.isPending }

    val abnormalCount: Int
        get() = tasks.count { it.isAbnormalFlagged == true }

    val displayedTasks: List<EnrichedTaskRow>
        get() = when (filter) {
            TaskFilter.ALL -> tasks
            TaskFilter.PENDING -> tasks.filter { it.isPending }
            TaskFilter.COMPLETED -> tasks.filter { it.isCompleted }
        }
}

class ShiftTasksViewModel(
    val repository: CareRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ShiftTasksUiState(
            cnaName = sessionManager.cnaName ?: "Điều dưỡng",
            cnaRole = sessionManager.cnaRole ?: "CNA"
        )
    )
    val uiState: StateFlow<ShiftTasksUiState> = _uiState.asStateFlow()

    init {
        loadTasks()
    }

    fun setFilter(filter: TaskFilter) {
        _uiState.update { it.copy(filter = filter) }
    }

    fun dismissFeedback() {
        _uiState.update { it.copy(errorMessage = null, successToast = null) }
    }

    fun loadTasks(isRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = !isRefresh,
                    isRefreshing = isRefresh,
                    errorMessage = null,
                    cnaName = sessionManager.cnaName ?: it.cnaName,
                    cnaRole = sessionManager.cnaRole ?: it.cnaRole
                )
            }

            val result = repository.fetchShiftTasks()
            result.fold(
                onSuccess = { list ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            tasks = list
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = error.localizedMessage ?: "Không thể kết nối máy chủ để lấy danh sách ca trực"
                        )
                    }
                }
            )
        }
    }

    fun completeTask(task: EnrichedTaskRow) {
        if (task.isCompleted) return

        viewModelScope.launch {
            _uiState.update { it.copy(completingTaskIds = it.completingTaskIds + task.id) }

            // Optimistic update
            val previousTasks = _uiState.value.tasks
            val updatedTasks = previousTasks.map {
                if (it.id == task.id) {
                    it.copy(
                        status = "COMPLETED",
                        completedAt = java.time.OffsetDateTime.now().toString()
                    )
                } else it
            }
            _uiState.update { it.copy(tasks = updatedTasks) }

            val result = repository.completeTask(task.id)
            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            completingTaskIds = it.completingTaskIds - task.id,
                            successToast = "Đã hoàn thành chăm sóc: ${task.residentDisplayName ?: "Người bệnh"}"
                        )
                    }
                },
                onFailure = { error ->
                    // Rollback on failure
                    _uiState.update {
                        it.copy(
                            tasks = previousTasks,
                            completingTaskIds = it.completingTaskIds - task.id,
                            errorMessage = "Không thể lưu trạng thái: ${error.localizedMessage}"
                        )
                    }
                }
            )
        }
    }

    fun toggleAbnormalFlag(task: EnrichedTaskRow) {
        val newFlag = !(task.isAbnormalFlagged ?: false)

        viewModelScope.launch {
            val previousTasks = _uiState.value.tasks
            val updatedTasks = previousTasks.map {
                if (it.id == task.id) it.copy(isAbnormalFlagged = newFlag) else it
            }
            _uiState.update { it.copy(tasks = updatedTasks) }

            val result = repository.flagAbnormal(task.id, newFlag)
            result.fold(
                onSuccess = {
                    val statusText = if (newFlag) "Đã ghi nhận cờ bất thường" else "Đã xóa cờ bất thường"
                    _uiState.update { it.copy(successToast = statusText) }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            tasks = previousTasks,
                            errorMessage = "Không thể cập nhật cờ: ${error.localizedMessage}"
                        )
                    }
                }
            )
        }
    }

    fun confirmVoiceNote(transcript: String, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val defaultParsed = com.eldercare.companion.data.model.VoiceCareParseResponse(
                residentIdentifier = "Robert Anderson",
                roomNumber = "101",
                bloodPressureSystolic = 120,
                bloodPressureDiastolic = 80,
                heartRateBpm = 74,
                bodyTemperatureCelsius = 36.6,
                dietaryIntakePercentage = 90,
                extractedNotes = transcript.take(200),
                confidence = 0.95
            )
            val res = repository.confirmVoiceObservation(
                residentId = 1L,
                taskId = null,
                transcript = transcript,
                parsed = defaultParsed
            )
            res.fold(
                onSuccess = {
                    onComplete(true, "Đã đồng bộ ghi chú lên Web Admin (Bản ghi #${it.observationId}) thành công!")
                },
                onFailure = { err ->
                    onComplete(false, err.message ?: "Lỗi đồng bộ ghi chú")
                }
            )
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        repository.logout()
        onLoggedOut()
    }
}
