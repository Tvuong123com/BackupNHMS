package com.eldercare.companion.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eldercare.companion.data.model.EnrichedTaskRow
import com.eldercare.companion.localization.AppLanguage
import com.eldercare.companion.localization.AppStrings
import com.eldercare.companion.ui.components.AiChatbotDialog
import com.eldercare.companion.ui.components.TaskCard
import com.eldercare.companion.ui.components.TaskDetailBottomSheet
import com.eldercare.companion.ui.components.VoiceCareBottomSheet
import com.eldercare.companion.ui.components.VoiceNoteDialog
import kotlinx.coroutines.launch

@Composable
fun ShiftTasksScreen(
    viewModel: ShiftTasksViewModel,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier,
    language: AppLanguage = AppLanguage.VIETNAMESE
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val isVi = language == AppLanguage.VIETNAMESE

    var showVoiceNoteDialog by remember { mutableStateOf(false) }
    var showAiChatbotDialog by remember { mutableStateOf(false) }
    var selectedTaskForDetail by remember { mutableStateOf<EnrichedTaskRow?>(null) }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissFeedback()
        }
    }

    LaunchedEffect(uiState.successToast) {
        uiState.successToast?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissFeedback()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Bedside Quick Actions Header (Voice Note + Refresh)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Voice Note shortcut button
                FilledTonalButton(
                    onClick = { showVoiceNoteDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.height(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Mic,
                        contentDescription = "Voice Note",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isVi) "Ghi chú giọng nói tại giường" else "Bedside Voice Note",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Refresh Button
                IconButton(
                    onClick = { viewModel.loadTasks(isRefresh = true) },
                    enabled = !uiState.isLoading && !uiState.isRefreshing
                ) {
                    if (uiState.isRefreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = "Tải lại danh sách",
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            // Shift Progress Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = AppStrings.shiftProgress(language),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        val progressPercent = if (uiState.totalCount > 0) {
                            (uiState.completedCount * 100) / uiState.totalCount
                        } else 0
                        Text(
                            text = "${uiState.completedCount}/${uiState.totalCount} ${if (isVi) "hoàn thành" else "completed"} ($progressPercent%)",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val progressFraction = if (uiState.totalCount > 0) {
                        uiState.completedCount.toFloat() / uiState.totalCount.toFloat()
                    } else 0f

                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF059669),
                        trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    )

                    if (uiState.abnormalCount > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.WarningAmber,
                                contentDescription = null,
                                tint = Color(0xFFE11D48),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isVi) "Có ${uiState.abnormalCount} mục ghi nhận bất thường cần bàn giao ca" else "${uiState.abnormalCount} abnormal items flagged for shift handover",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFE11D48),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = uiState.filter == TaskFilter.PENDING,
                    onClick = { viewModel.setFilter(TaskFilter.PENDING) },
                    label = { Text("${AppStrings.filterPending(language)} (${uiState.pendingCount})") },
                    shape = RoundedCornerShape(8.dp)
                )

                FilterChip(
                    selected = uiState.filter == TaskFilter.COMPLETED,
                    onClick = { viewModel.setFilter(TaskFilter.COMPLETED) },
                    label = { Text("${AppStrings.filterCompleted(language)} (${uiState.completedCount})") },
                    shape = RoundedCornerShape(8.dp)
                )

                FilterChip(
                    selected = uiState.filter == TaskFilter.ALL,
                    onClick = { viewModel.setFilter(TaskFilter.ALL) },
                    label = { Text("${AppStrings.filterAll(language)} (${uiState.totalCount})") },
                    shape = RoundedCornerShape(8.dp)
                )
            }

            // Task List or Loading / Empty States
            if (uiState.isLoading && uiState.tasks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(strokeWidth = 3.dp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (isVi) "Đang đồng bộ danh sách ca trực..." else "Synchronizing shift care tasks...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else if (uiState.displayedTasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (uiState.filter == TaskFilter.PENDING && uiState.completedCount > 0) {
                            Icon(
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF059669),
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (isVi) "Ca trực đã hoàn tất!" else "Shift Tasks Completed!",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = if (isVi) "Bạn đã hoàn thành mọi công việc chăm sóc trong ca này." else "All care tasks in this shift have been confirmed.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        } else {
                            Text(
                                text = if (isVi) "Không có đầu việc nào trong mục này" else "No tasks in this category",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = if (isVi) "Nhấn nút làm mới ở góc trên để cập nhật dữ liệu mới nhất từ hệ thống." else "Tap the refresh icon above to synchronize latest tasks from web.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            } else {
                val tasksToDisplay = remember(uiState.displayedTasks) {
                    uiState.displayedTasks.take(7)
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isVi) "7 Nhiệm Vụ Trọng Tâm Ca Trực" else "7 Core Shift Tasks",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "${tasksToDisplay.size} việc được giao",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }

                    items(
                        items = tasksToDisplay,
                        key = { it.id }
                    ) { task ->
                        TaskCard(
                            task = task,
                            isCompleting = uiState.completingTaskIds.contains(task.id),
                            onCompleteClick = { viewModel.completeTask(it) },
                            onToggleFlagClick = { viewModel.toggleAbnormalFlag(it) },
                            onCardClick = { selectedTaskForDetail = it },
                            language = language
                        )
                    }
                }
            }
        }

        // Floating Action Button for AI Chatbot (Restored Icons.Filled.SmartToy)
        FloatingActionButton(
            onClick = { showAiChatbotDialog = true },
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 20.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.SmartToy,
                contentDescription = "AI Chatbot",
                modifier = Modifier.size(26.dp)
            )
        }

        // Snackbar host overlay
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // Bedside Voice Care Bottom Sheet (AI Extraction & EHR Sync)
        if (showVoiceNoteDialog) {
            VoiceCareBottomSheet(
                repository = viewModel.repository,
                language = language,
                onObservationSaved = { msg ->
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(msg)
                    }
                },
                onDismiss = { showVoiceNoteDialog = false }
            )
        }

        // AI Chatbot Dialog (Connected to Central LLM & Clinical Protocols)
        if (showAiChatbotDialog) {
            AiChatbotDialog(
                repository = viewModel.repository,
                language = language,
                onDismiss = { showAiChatbotDialog = false }
            )
        }

        // Task Detail Bottom Sheet
        if (selectedTaskForDetail != null) {
            val t = selectedTaskForDetail!!
            TaskDetailBottomSheet(
                task = t,
                language = language,
                onCompleteTask = { taskId, note ->
                    viewModel.completeTask(t)
                },
                onFlagAbnormal = { taskId, reason ->
                    viewModel.toggleAbnormalFlag(t)
                },
                onDismiss = { selectedTaskForDetail = null }
            )
        }
    }
}
