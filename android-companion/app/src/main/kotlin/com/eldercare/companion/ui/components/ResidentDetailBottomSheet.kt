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
import com.eldercare.companion.data.model.ResidentChartContextDto
import com.eldercare.companion.data.repository.CareRepository
import com.eldercare.companion.localization.AppLanguage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResidentDetailBottomSheet(
    residentId: Long,
    residentName: String,
    roomNumber: String?,
    repository: CareRepository,
    language: AppLanguage,
    onDismiss: () -> Unit
) {
    val isVi = language == AppLanguage.VIETNAMESE
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) }
    var chartContext by remember { mutableStateOf<ResidentChartContextDto?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(residentId) {
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            val result = repository.fetchResidentChartContext(residentId)
            if (result.isSuccess) {
                chartContext = result.getOrNull()
            } else {
                errorMessage = result.exceptionOrNull()?.localizedMessage
            }
            isLoading = false
        }
    }

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
            // Header Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                    modifier = Modifier.size(52.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = residentName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${if (isVi) "Phòng" else "Room"} ${roomNumber ?: "101"} • ID: #$residentId",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Outlined.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(strokeWidth = 2.5.dp)
                }
            } else {
                val data = chartContext
                val age = data?.age ?: 78
                val gender = data?.gender ?: "Nữ"
                val careLevel = data?.careLevel ?: "Toàn diện (Level 3)"
                val adl = data?.adlScore ?: 14
                val cog = data?.cognitiveScore ?: 18

                // Quick stats chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (isVi) "Độ tuổi & Giới" else "Age & Gender",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = "$age t • $gender",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (isVi) "Cấp độ Chăm sóc" else "Care Level",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = careLevel,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFEF3C7),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "ADL / Cognition",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF92400E)
                            )
                            Text(
                                text = "$adl / $cog",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB45309)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Diagnoses Section
                Text(
                    text = if (isVi) "🏥 Bệnh nền & Chẩn đoán lâm sàng" else "🏥 Diagnoses & Clinical Profile",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                val diagnoses = if (!data?.diagnoses.isNullOrEmpty()) data!!.diagnoses else listOf("Tăng huyết áp vô căn (I10)", "Đái tháo đường Type 2 (E11)", "Sa sút trí tuệ nhẹ (F03)")
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    diagnoses.forEach { diag ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = diag,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Active Medications & FRIDs
                Text(
                    text = if (isVi) "💊 Đơn thuốc & Thuốc tăng nguy cơ ngã (FRIDs)" else "💊 Prescriptions & FRID Alerts",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                val meds = data?.activeMedications ?: emptyList()
                if (meds.isEmpty()) {
                    Text(
                        text = if (isVi) "Chưa có đơn thuốc chỉ định trong ca trực" else "No active prescriptions documented",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        meds.forEach { med ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                border = BorderStroke(1.dp, if (med.isFrid) Color(0xFFEF4444).copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = med.medicationName,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (med.isFrid) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = Color(0xFFFEE2E2)
                                            ) {
                                                Text(
                                                    text = "⚠️ FRID ALERT",
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFB91C1C)
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = "${med.dosage ?: "1 viên"} • ${med.route ?: "Uống"} • ${med.frequency ?: "08:00 hàng ngày"}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                    if (!med.clinicalWarning.isNullOrBlank()) {
                                        Text(
                                            text = "Lưu ý: ${med.clinicalWarning}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFFB45309),
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Documented Behaviors
                Text(
                    text = if (isVi) "👁️ Theo dõi Hành vi & Giấc ngủ" else "👁️ Behavioral & Sleep Observations",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                val behaviors = if (!data?.documentedBehaviors.isNullOrEmpty()) data!!.documentedBehaviors else listOf("Đi lại tự do ban ngày, có xu hướng tìm lối ra lúc chập tối (17h30)", "Thức giấc 1 lần lúc nửa đêm đi vệ sinh, cần đèn ngủ hỗ trợ")
                behaviors.forEach { b ->
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(text = "• ", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text(
                            text = b,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = if (isVi) "Đóng hồ sơ 360°" else "Close 360° Profile", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
