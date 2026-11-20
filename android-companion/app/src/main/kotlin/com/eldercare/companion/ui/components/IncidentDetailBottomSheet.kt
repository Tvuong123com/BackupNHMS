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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eldercare.companion.data.model.IncidentDto
import com.eldercare.companion.localization.AppLanguage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentDetailBottomSheet(
    incident: IncidentDto,
    language: AppLanguage,
    onDismiss: () -> Unit
) {
    val isVi = language == AppLanguage.VIETNAMESE
    val severityName = incident.severity?.levelName ?: "Trung bình"
    val isCritical = severityName.contains("Critical", ignoreCase = true) || severityName.contains("Nghiêm trọng", ignoreCase = true) || severityName.contains("Cao", ignoreCase = true)

    val badgeBg = if (isCritical) Color(0xFFFEE2E2) else Color(0xFFFEF3C7)
    val badgeFg = if (isCritical) Color(0xFFB91C1C) else Color(0xFF92400E)

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
                    color = badgeBg,
                    modifier = Modifier.size(50.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.WarningAmber,
                            contentDescription = null,
                            tint = badgeFg,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = incident.incidentType,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Mã sự cố: #${incident.id} • ${incident.resident?.displayName ?: "Cư dân chưa định danh"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Outlined.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Severity & Status Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = badgeBg
                ) {
                    Text(
                        text = "Mức độ: $severityName",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = badgeFg
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = "Trạng thái: ${incident.status}",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Location & Resident Info
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Outlined.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Vị trí: ${incident.location ?: "Phòng nghỉ cư dân (P102)"}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Outlined.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Cư dân liên quan: ${incident.resident?.displayName ?: "Nguyễn Văn Hùng (P102)"}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Incident Description
            Text(
                text = if (isVi) "📝 Diễn biến sự cố chi tiết" else "📝 Incident Description & Chronology",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = incident.description?.ifBlank { "Cư dân trượt ngã nhẹ khi di chuyển từ giường sang ghế tựa. Điều dưỡng ca trực phát hiện tức thì và hỗ trợ sơ cứu." }
                        ?: "Cư dân trượt ngã nhẹ khi di chuyển từ giường sang ghế tựa. Điều dưỡng ca trực phát hiện tức thì và hỗ trợ sơ cứu.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Immediate First Aid & Interventions
            Text(
                text = if (isVi) "🩺 Sơ cấp cứu & Can thiệp tức thời" else "🩺 Immediate First Aid & Actions Taken",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf(
                    "• Đỡ cư dân về tư thế an toàn, kiểm tra vết thương hở và đo lại toàn bộ 6 chỉ số sinh hiệu.",
                    "• Không ghi nhận gãy xương hay chấn thương sọ não, đã chườm lạnh vị trí va quẹt nhẹ.",
                    "• Thông báo bác sĩ trực viện và gọi điện thoại cập nhật tình hình cho gia đình cư dân.",
                    "• Tăng tần suất tuần tra phòng mỗi 30 phút trong ca đêm."
                ).forEach { item ->
                    Text(text = item, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // SLA & Investigation Resolution
            Text(
                text = if (isVi) "⚖️ Kết luận điều tra & Cam kết SLA" else "⚖️ Investigation & SLA Follow-up",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFEFF6FF),
                border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "SLA xử lý: Hoàn tất đánh giá trong 24h theo chuẩn Bộ Y Tế",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D4ED8)
                    )
                    Text(
                        text = "Kết luận: Sàn phòng bị đọng nước nhẹ sau lau dọn. Đã yêu cầu bộ phận tạp vụ đặt biển cảnh báo sàn trơn và bổ sung thảm chống trượt.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF1E3A8A)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (isVi) "Đóng chi tiết sự cố" else "Close Incident Detail", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
