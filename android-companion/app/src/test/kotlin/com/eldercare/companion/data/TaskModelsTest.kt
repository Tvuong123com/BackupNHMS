package com.eldercare.companion.data

import com.eldercare.companion.data.model.EnrichedTaskRow
import com.eldercare.companion.ui.tasks.ShiftTasksUiState
import com.eldercare.companion.ui.tasks.TaskFilter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TaskModelsTest {

    @Test
    fun testTaskCompletionStatus() {
        val pendingTask = EnrichedTaskRow(
            id = 1L,
            status = "PENDING",
            completedAt = null
        )
        assertTrue(pendingTask.isPending)
        assertFalse(pendingTask.isCompleted)

        val completedTask = EnrichedTaskRow(
            id = 2L,
            status = "COMPLETED",
            completedAt = "2026-11-19T14:30:00Z"
        )
        assertFalse(completedTask.isPending)
        assertTrue(completedTask.isCompleted)
    }

    @Test
    fun testShiftTasksUiStateMetrics() {
        val tasks = listOf(
            EnrichedTaskRow(id = 1L, status = "COMPLETED", completedAt = "2026-11-19T10:00:00Z"),
            EnrichedTaskRow(id = 2L, status = "PENDING", isAbnormalFlagged = true),
            EnrichedTaskRow(id = 3L, status = "PENDING", isAbnormalFlagged = false)
        )

        val state = ShiftTasksUiState(
            tasks = tasks,
            filter = TaskFilter.PENDING
        )

        assertEquals(3, state.totalCount)
        assertEquals(1, state.completedCount)
        assertEquals(2, state.pendingCount)
        assertEquals(1, state.abnormalCount)

        // Filtered tasks should only show the 2 pending tasks
        assertEquals(2, state.displayedTasks.size)
        assertTrue(state.displayedTasks.all { it.isPending })
    }
}
