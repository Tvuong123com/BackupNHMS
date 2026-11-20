package com.eldercare.companion.ui.activities

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eldercare.companion.data.model.EnrichedTaskRow
import com.eldercare.companion.data.model.ResidentDto
import com.eldercare.companion.data.repository.CareRepository
import com.eldercare.companion.localization.AppLanguage
import com.eldercare.companion.ui.components.ResidentDetailBottomSheet
import com.eldercare.companion.ui.components.TaskDetailBottomSheet
import kotlinx.coroutines.launch

@Composable
fun ActivitiesScreen(
    repository: CareRepository,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val isVi = language == AppLanguage.VIETNAMESE
    val coroutineScope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(true) }
    var residents by remember { mutableStateOf<List<ResidentDto>>(emptyList()) }
    var tasks by remember { mutableStateOf<List<EnrichedTaskRow>>(emptyList()) }
    var selectedResidentId by remember { mutableStateOf<Long?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Bottom sheets state
    var selectedResidentForProfile by remember { mutableStateOf<ResidentDto?>(null) }
    var selectedTaskForDetail by remember { mutableStateOf<EnrichedTaskRow?>(null) }

    val loadData: () -> Unit = {
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            val rRes = repository.fetchResidents()
            val tRes = repository.fetchShiftTasks()

            if (rRes.isSuccess) {
                residents = rRes.getOrNull() ?: emptyList()
            }
            if (tRes.isSuccess) {
                tasks = tRes.getOrNull() ?: emptyList()
            }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    val filteredTasks = remember(tasks, selectedResidentId) {
        if (selectedResidentId == null) tasks
        else tasks.filter { it.residentId == selectedResidentId }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Shortened Title Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isVi) "Lịch Hoạt Động & Sinh Hoạt (20 mục)" else "Resident Activities Schedule (20 items)",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = if (isVi) "Lịch sinh hoạt cộng đồng, tập phục hồi & theo dõi toàn diện" else "Comprehensive recreational, mobility & round schedule",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            IconButton(onClick = { loadData() }) {
                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = "Refresh",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Resident Filter Strip (Horizontal chips with real resident names)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isVi) "Cư dân được phân công (${residents.size}):" else "Assigned Residents (${residents.size}):",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            if (selectedResidentId != null) {
                val current = residents.find { it.id == selectedResidentId }
                Text(
                    text = if (isVi) "Bấm để xem hồ sơ" else "Tap chip to view profile",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedResidentId == null,
                    onClick = { selectedResidentId = null },
                    label = { Text(if (isVi) "Tất cả (${residents.size})" else "All (${residents.size})") },
                    shape = RoundedCornerShape(8.dp)
                )
            }
            items(residents, key = { it.id }) { res ->
                val isSelected = selectedResidentId == res.id
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        if (isSelected) {
                            // If already selected, tap again to view full 360 profile!
                            selectedResidentForProfile = res
                        } else {
                            selectedResidentId = res.id
                        }
                    },
                    label = { Text("${res.fullName} (P${res.roomNumber ?: "101"})") },
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }

        // Active Resident Summary Banner with Direct 360 View Button
        if (selectedResidentId != null) {
            val res = residents.find { it.id == selectedResidentId }
            if (res != null) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable { selectedResidentForProfile = res }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.Person,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = res.fullName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${if (isVi) "Phòng" else "Room"} ${res.roomNumber ?: "101"} • Cấp độ: ${res.currentCareLevel ?: "Level 2"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isVi) "Hồ sơ 360°" else "Profile 360°",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Outlined.ChevronRight,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(strokeWidth = 2.5.dp)
            }
        } else if (filteredTasks.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                tonalElevation = 1.dp,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (isVi) "Không có nhiệm vụ nào tồn đọng cho cư dân này" else "No pending care tasks for this resident",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        } else {
            val displayedActivities = remember(filteredTasks) {
                filteredTasks.take(20)
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isVi) "Hiển thị ${displayedActivities.size}/20 hoạt động chăm sóc" else "Displaying ${displayedActivities.size}/20 activities",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = if (isVi) "Toàn viện" else "All Ward",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }

                items(displayedActivities, key = { it.id }) { task ->
                    val isDone = task.isCompleted
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(
                            1.dp,
                            if (task.isAbnormalFlagged == true) Color(0xFFEF4444).copy(alpha = 0.5f)
                            else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
                        ),
                        tonalElevation = 1.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedTaskForDetail = task }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (isDone) Color(0xFF059669).copy(alpha = 0.15f) else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (isDone) Icons.Outlined.CheckCircle else Icons.Outlined.Schedule,
                                        contentDescription = null,
                                        tint = if (isDone) Color(0xFF059669) else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = task.residentDisplayName ?: (if (isVi) "Cư dân" else "Resident"),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isDone) Color(0xFFD1FAE5) else Color(0xFFFEF3C7)
                                    ) {
                                        Text(
                                            text = if (isDone) (if (isVi) "ĐÃ XONG" else "DONE") else (if (isVi) "CHỜ LÀM" else "PENDING"),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDone) Color(0xFF065F46) else Color(0xFF92400E)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = "${task.taskType ?: "Chăm sóc"} • ${if (isVi) "Phòng" else "Room"} ${task.roomNumber ?: "101"} • Lịch: ${task.scheduledTime?.take(16) ?: "Trong ca"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.outline
                                )

                                if (!task.goal.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "🎯 ${task.goal}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Icon(
                                imageVector = Icons.Outlined.ChevronRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal Sheet 1: Resident 360 Profile
    if (selectedResidentForProfile != null) {
        val r = selectedResidentForProfile!!
        ResidentDetailBottomSheet(
            residentId = r.id,
            residentName = r.fullName,
            roomNumber = r.roomNumber,
            repository = repository,
            language = language,
            onDismiss = { selectedResidentForProfile = null }
        )
    }

    // Modal Sheet 2: Task Detail
    if (selectedTaskForDetail != null) {
        val t = selectedTaskForDetail!!
        TaskDetailBottomSheet(
            task = t,
            language = language,
            onCompleteTask = { taskId, _ ->
                coroutineScope.launch {
                    repository.completeTask(taskId)
                    loadData()
                }
            },
            onFlagAbnormal = { taskId, _ ->
                coroutineScope.launch {
                    repository.flagAbnormal(taskId, true)
                    loadData()
                }
            },
            onDismiss = { selectedTaskForDetail = null }
        )
    }
}
