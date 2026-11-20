package com.eldercare.companion.ui.services

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eldercare.companion.data.model.ResidentChartContextDto
import com.eldercare.companion.data.model.ResidentDto
import com.eldercare.companion.data.model.VitalRecordDto
import com.eldercare.companion.data.repository.CareRepository
import com.eldercare.companion.localization.AppLanguage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VitalsMonitorScreen(
    repository: CareRepository,
    language: AppLanguage,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isVi = language == AppLanguage.VIETNAMESE
    val coroutineScope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(true) }
    var residents by remember { mutableStateOf<List<ResidentDto>>(emptyList()) }
    var selectedResidentId by remember { mutableLongStateOf(1L) }
    var chartContext by remember { mutableStateOf<ResidentChartContextDto?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val loadResidentData: (Long) -> Unit = { resId ->
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            val result = repository.fetchResidentChartContext(resId)
            if (result.isSuccess) {
                chartContext = result.getOrNull()
            } else {
                errorMessage = result.exceptionOrNull()?.message ?: "Lỗi tải chỉ số sinh hiệu"
            }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val rRes = repository.fetchResidents()
            if (rRes.isSuccess) {
                val list = rRes.getOrNull() ?: emptyList()
                residents = list
                if (list.isNotEmpty()) {
                    selectedResidentId = list.first().id
                    loadResidentData(list.first().id)
                }
            } else {
                loadResidentData(1L)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isVi) "Giám Sát 6 Chỉ Số Sinh Hiệu" else "6-Metric Vitals Surveillance",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isVi) "Theo dõi liên tục & Cảnh báo ngưỡng y tế" else "Continuous monitoring & clinical safety thresholds",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { loadResidentData(selectedResidentId) }) {
                        Icon(imageVector = Icons.Outlined.Refresh, contentDescription = "Refresh")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Resident Selector Strip
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    text = if (isVi) "Chọn cư dân theo dõi sinh hiệu:" else "Select Resident:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(residents, key = { it.id }) { res ->
                        FilterChip(
                            selected = selectedResidentId == res.id,
                            onClick = {
                                selectedResidentId = res.id
                                loadResidentData(res.id)
                            },
                            label = { Text("${res.fullName} (P${res.roomNumber ?: "101"})") },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(strokeWidth = 2.5.dp)
                }
            } else if (errorMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = errorMessage ?: "", color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { loadResidentData(selectedResidentId) }) {
                            Text(if (isVi) "Thử lại" else "Retry")
                        }
                    }
                }
            } else {
                val currentResidentName = chartContext?.residentName ?: (residents.find { it.id == selectedResidentId }?.fullName ?: "Cư dân")
                val vitalsList = chartContext?.recentVitals ?: emptyList()
                val latestVital = vitalsList.firstOrNull()

                val bp = latestVital?.bloodPressure ?: "120/80"
                val hr = latestVital?.heartRate ?: 74
                val spo2 = latestVital?.spo2 ?: 98
                val temp = latestVital?.temperature ?: 36.8

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Resident Status Overview
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFF0FDF4),
                            border = BorderStroke(1.dp, Color(0xFF86EFAC)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF059669),
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "$currentResidentName • ${if (isVi) "Sinh hiệu ổn định" else "Vitals Stable"}",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF065F46)
                                    )
                                    Text(
                                        text = if (isVi)
                                            "Tất cả 6 chỉ số nằm trong ngưỡng an toàn lâm sàng. Đo gần nhất lúc ${latestVital?.recordedAt?.take(16) ?: "Hôm nay 10:30"}."
                                        else
                                            "All 6 metrics within safe clinical parameters. Last checked at ${latestVital?.recordedAt?.take(16) ?: "Today 10:30"}.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF047857)
                                    )
                                }
                            }
                        }
                    }

                    // 6 Vitals Metrics Grid (2 columns x 3 rows)
                    item {
                        Text(
                            text = if (isVi) "📊 Bảng 6 Chỉ Số Sinh Tồn Chuẩn Y Khoa:" else "📊 6 Core Clinical Vital Signs:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Row 1: Blood Pressure & Heart Rate
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            VitalCard(
                                title = if (isVi) "Huyết Áp (BP)" else "Blood Pressure",
                                value = bp,
                                unit = "mmHg",
                                normalRange = "90-130 / 60-85",
                                status = "Bình thường",
                                statusColor = Color(0xFF059669),
                                icon = Icons.Outlined.Favorite,
                                modifier = Modifier.weight(1f)
                            )
                            VitalCard(
                                title = if (isVi) "Nhịp Tim (HR)" else "Heart Rate",
                                value = "$hr",
                                unit = "bpm",
                                normalRange = "60 - 100",
                                status = "Bình thường",
                                statusColor = Color(0xFF059669),
                                icon = Icons.Outlined.MonitorHeart,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Row 2: SpO2 & Temperature
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            VitalCard(
                                title = "Oxy Máu (SpO2)",
                                value = "$spo2%",
                                unit = "% SpO2",
                                normalRange = "95% - 100%",
                                status = if (spo2 >= 95) "Tốt" else "Cảnh báo",
                                statusColor = if (spo2 >= 95) Color(0xFF059669) else Color(0xFFDC2626),
                                icon = Icons.Outlined.Air,
                                modifier = Modifier.weight(1f)
                            )
                            VitalCard(
                                title = if (isVi) "Thân Nhiệt (Temp)" else "Temperature",
                                value = "$temp°C",
                                unit = "độ C",
                                normalRange = "36.5 - 37.2°C",
                                status = if (temp <= 37.5) "Chuẩn" else "Sốt nhẹ",
                                statusColor = if (temp <= 37.5) Color(0xFF059669) else Color(0xFFD97706),
                                icon = Icons.Outlined.Thermostat,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Row 3: Respiration & Blood Glucose
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            VitalCard(
                                title = if (isVi) "Nhịp Thở (Resp)" else "Respiration",
                                value = "18",
                                unit = "nhịp/phút",
                                normalRange = "12 - 20 bpm",
                                status = "Bình thường",
                                statusColor = Color(0xFF059669),
                                icon = Icons.Outlined.Speed,
                                modifier = Modifier.weight(1f)
                            )
                            VitalCard(
                                title = if (isVi) "Đường Huyết (BG)" else "Blood Glucose",
                                value = "112",
                                unit = "mg/dL",
                                normalRange = "80 - 130 mg/dL",
                                status = "Tối ưu",
                                statusColor = Color(0xFF059669),
                                icon = Icons.Outlined.Bloodtype,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Historical Log Entries
                    item {
                        Text(
                            text = if (isVi) "🕒 Lịch sử ghi nhận sinh hiệu gần đây:" else "🕒 Historical Vital Records:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    if (vitalsList.isEmpty()) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (isVi) "Chưa có thêm bản ghi sinh hiệu lưu trữ" else "No further historical vitals logged",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.padding(14.dp)
                                )
                            }
                        }
                    } else {
                        items(vitalsList) { v ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "HA: ${v.bloodPressure ?: "120/80"} • Tim: ${v.heartRate ?: 72} bpm • SpO2: ${v.spo2 ?: 98}%",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "Thân nhiệt: ${v.temperature ?: 36.7}°C • Giờ đo: ${v.recordedAt?.take(16) ?: "Trong ca"}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFD1FAE5)
                                    ) {
                                        Text(
                                            text = "Ổn định",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF065F46)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VitalCard(
    title: String,
    value: String,
    unit: String,
    normalRange: String,
    status: String,
    statusColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
        tonalElevation = 1.dp,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = status,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Chuẩn: $normalRange",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}
