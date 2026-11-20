package com.eldercare.companion.data.model

import com.google.gson.annotations.SerializedName

data class EnrichedTaskRow(
    @SerializedName("id") val id: Long,
    @SerializedName("careInterventionId") val careInterventionId: Long? = null,
    @SerializedName("carePlanId") val carePlanId: Long? = null,
    @SerializedName("residentId") val residentId: Long? = null,
    @SerializedName("residentDisplayName") val residentDisplayName: String? = null,
    @SerializedName("roomNumber") val roomNumber: String? = null,
    @SerializedName("assignedCnaId") val assignedCnaId: Long? = null,
    @SerializedName("assignedCnaDisplayName") val assignedCnaDisplayName: String? = null,
    @SerializedName("scheduledTime") val scheduledTime: String? = null,
    @SerializedName("completedAt") val completedAt: String? = null,
    @SerializedName("taskType") val taskType: String? = null,
    @SerializedName("status") val status: String = "PENDING",
    @SerializedName("isAbnormalFlagged") val isAbnormalFlagged: Boolean? = false,
    @SerializedName("goal") val goal: String? = null
) {
    val isCompleted: Boolean
        get() = status.equals("COMPLETED", ignoreCase = true) || !completedAt.isNullOrBlank()

    val isMissed: Boolean
        get() = status.equals("MISSED", ignoreCase = true)

    val isPending: Boolean
        get() = !isCompleted && !isMissed
}

data class GroupedByCnaCard(
    @SerializedName("cnaId") val cnaId: Long,
    @SerializedName("cnaDisplayName") val cnaDisplayName: String? = null,
    @SerializedName("totalTasks") val totalTasks: Int = 0,
    @SerializedName("completedTasks") val completedTasks: Int = 0,
    @SerializedName("missedTasks") val missedTasks: Int = 0,
    @SerializedName("tasks") val tasks: List<EnrichedTaskRow> = emptyList()
)

data class PagedResponse<T>(
    @SerializedName("statusCode") val statusCode: Int = 200,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: T? = null
)

data class ApiResponse<T>(
    @SerializedName("statusCode") val statusCode: Int = 200,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: T? = null
)

data class CompleteTaskRequest(
    @SerializedName("completedAt") val completedAt: String
)

data class FlagAbnormalRequest(
    @SerializedName("isAbnormalFlagged") val isAbnormalFlagged: Boolean
)
