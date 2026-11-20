package com.eldercare.companion.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.sp
import com.eldercare.companion.data.model.EnrichedTaskRow
import com.eldercare.companion.localization.AppLanguage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailBottomSheet(
    task: EnrichedTaskRow,
    language: AppLanguage,
    onCompleteTask: (taskId: Long, note: String) -> Unit,
    onFlagAbnormal: (taskId: Long, reason: String) -> Unit,
    onDismiss: () -> Unit
) {
    val isVi = language == AppLanguage.VIETNAMESE
    var noteText by remember { mutableStateOf("") }
    var abnormalReason by remember { mutableStateOf("") }
    var showAbnormalInput by remember { mutableStateOf(false) }

    val isDone = task.isCompleted

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (isDone) Color(0xFF059669).copy(alpha = 0.15f) else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                    modifier = Modifier.size(50.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isDone) Icons.Outlined.CheckCircle else Icons.Outlined.Assignment,
                            contentDescription = null,
                            tint = if (isDone) Color(0xFF059669) else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.residentDisplayName ?: (if (isVi) "Cư dân nội viện" else "Facility Resident"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${if (isVi) "Phòng" else "Room"} ${task.roomNumber ?: "101"} • Nhiệm vụ #${task.id}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Outlined.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Task Type & Status Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = "Loại: ${task.taskType ?: "Chăm sóc ADL"}",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isDone) Color(0xFFD1FAE5) else Color(0xFFFEF3C7)
                ) {
                    Text(
                        text = if (isDone) (if (isVi) "ĐÃ HOÀN TẤT" else "COMPLETED") else (if (isVi) "ĐANG CHỜ THỰC HIỆN" else "PENDING"),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isDone) Color(0xFF065F46) else Color(0xFF92400E)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Schedule & Time Info
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isVi) "Khung giờ thực hiện ca trực" else "Scheduled Shift Time",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = task.scheduledTime?.take(16) ?: (if (isVi) "Trong ca trực hôm nay" else "During shift today"),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Goal & SOP Instructions
            Text(
                text = if (isVi) "🎯 Mục tiêu từ Kế hoạch Chăm sóc" else "🎯 Target Goal from Care Plan",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF0FDF4),
                border = BorderStroke(1.dp, Color(0xFF86EFAC)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = task.goal?.ifBlank { "Duy trì sinh hoạt độc lập an toàn, phòng ngừa té ngã và hỗ trợ vệ sinh thân thể đầy đủ." }
                        ?: "Duy trì sinh hoạt độc lập an toàn, phòng ngừa té ngã và hỗ trợ vệ sinh thân thể đầy đủ.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF14532D),
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Clinical SOP Instructions
            Text(
                text = if (isVi) "📋 Quy trình điều dưỡng SOP chuẩn" else "📋 Standard Clinical SOP Instructions",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                val sops = listOf(
                    "1. Chào hỏi thân mật, giải thích cho cư dân các bước hỗ trợ chuẩn bị thực hiện.",
                    "2. Kiểm tra thanh chắn giường, đảm bảo thảm sàn khô ráo và không có vật cản xung quanh.",
                    "3. Ghi nhận biểu hiện thể chất, nét mặt hoặc dấu hiệu chóng mặt, run rẩy.",
                    "4. Sau khi hoàn tất, đưa nút chuông gọi điều dưỡng trong tầm với của cư dân."
                )
                sops.forEach { s ->
                    Text(
                        text = s,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Optional Note / Observation input
            if (!isDone) {
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text(if (isVi) "Ghi chú điều dưỡng lâm sàng (tuỳ chọn)" else "Clinical Nurse Note (Optional)") },
                    placeholder = { Text(if (isVi) "Vd: Cư dân hợp tác tốt, ăn hết 90% khẩu phần..." else "e.g. Resident cooperative...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (showAbnormalInput) {
                    OutlinedTextField(
                        value = abnormalReason,
                        onValueChange = { abnormalReason = it },
                        label = { Text(if (isVi) "Lý do ghi nhận bất thường" else "Abnormal Observation Reason") },
                        placeholder = { Text(if (isVi) "Vd: Huyết áp tăng nhẹ, cư dân phàn nàn hoa mắt" else "e.g. Mild dizziness noted") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.error,
                            unfocusedBorderColor = MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (abnormalReason.isNotBlank()) {
                                onFlagAbnormal(task.id, abnormalReason)
                                showAbnormalInput = false
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(if (isVi) "Gửi Cảnh Báo Bất Thường Cho Trưởng Ca" else "Send Abnormal Alert to Charge Nurse")
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { showAbnormalInput = !showAbnormalInput },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.WarningAmber,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isVi) "Báo bất thường" else "Flag Alert",
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Button(
                        onClick = {
                            onCompleteTask(task.id, noteText)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isVi) "Xác nhận xong" else "Complete",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFD1FAE5),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Verified,
                            contentDescription = null,
                            tint = Color(0xFF065F46),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isVi) "Nhiệm vụ này đã được xác nhận hoàn tất thành công trong ca trực." else "This care task has been successfully verified for the shift.",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF065F46)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (isVi) "Đóng chi tiết" else "Close")
                }
            }
        }
    }
}
