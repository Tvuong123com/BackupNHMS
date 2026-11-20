package com.eldercare.companion.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.eldercare.companion.localization.AppLanguage
import com.eldercare.companion.ui.theme.NHMSTheme

@Composable
fun VoiceNoteDialog(
    language: AppLanguage,
    onSyncToWeb: (noteText: String) -> Unit,
    onDismiss: () -> Unit
) {
    val isVi = language == AppLanguage.VIETNAMESE

    var isRecording by remember { mutableStateOf(false) }
    var noteText by remember {
        mutableStateOf(
            if (isVi) {
                "Cụ Nguyễn Văn An phòng 101 huyết áp 120/80, đã uống thuốc trưa và ăn hết suất cháo mềm, không có biểu hiện chóng mặt hay loét tì đè."
            } else {
                "Resident Nguyen Van An room 101 BP 120/80, completed noon medications and soft porridge, no signs of dizziness or pressure sores."
            }
        )
    }

    var isSynced by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isVi) "Ghi chú giọng nói tại giường" else "Bedside Voice Care Note",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Text(
                    text = if (isVi) "Nói trực tiếp để bóc tách thực thể lâm sàng và đồng bộ lên Web Admin" else "Speak directly to extract clinical entities and sync with Web Admin",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                // Recording Button & Wave indicator
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = CircleShape,
                            color = if (isRecording) Color(0xFFE11D48) else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(68.dp),
                            onClick = { isRecording = !isRecording }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isRecording) Icons.Outlined.Stop else Icons.Outlined.Mic,
                                    contentDescription = "Mic",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (isRecording) {
                                if (isVi) "Đang lắng nghe... Chạm để dừng" else "Listening... Tap to stop"
                            } else {
                                if (isVi) "Chạm để bắt đầu ghi âm" else "Tap microphone to dictate"
                            },
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            color = if (isRecording) Color(0xFFE11D48) else MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Dictated Text Field
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text(if (isVi) "Nội dung ghi nhận lâm sàng" else "Transcribed Clinical Notes") },
                    maxLines = 5,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Extracted Clinical Entities Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = if (isVi) "Thực thể lâm sàng trích xuất tự động:" else "Automatically Extracted Entities:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SuggestionChip(
                                onClick = {},
                                label = { Text("Cụ An • P101", style = MaterialTheme.typography.labelSmall) },
                                shape = RoundedCornerShape(6.dp)
                            )
                            SuggestionChip(
                                onClick = {},
                                label = { Text("HA: 120/80", style = MaterialTheme.typography.labelSmall) },
                                shape = RoundedCornerShape(6.dp)
                            )
                            SuggestionChip(
                                onClick = {},
                                label = { Text(if (isVi) "Thuốc trưa: Đã uống" else "Meds: Taken", style = MaterialTheme.typography.labelSmall) },
                                shape = RoundedCornerShape(6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action Sync Button
                Button(
                    onClick = {
                        isSynced = true
                        onSyncToWeb(noteText)
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSynced) Color(0xFF059669) else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = if (isSynced) Icons.Outlined.CheckCircle else Icons.Outlined.CloudUpload,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isSynced) {
                            if (isVi) "Đã đồng bộ lên Web Admin thành công!" else "Synced to Web Admin Successfully!"
                        } else {
                            if (isVi) "Đồng bộ lên Web Admin" else "Sync to Web Admin"
                        },
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VoiceNoteDialogPreview() {
    NHMSTheme {
        VoiceNoteDialog(
            language = AppLanguage.VIETNAMESE,
            onSyncToWeb = {},
            onDismiss = {}
        )
    }
}
