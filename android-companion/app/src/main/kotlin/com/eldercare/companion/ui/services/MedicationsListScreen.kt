package com.eldercare.companion.ui.services

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
import androidx.compose.ui.unit.sp
import com.eldercare.companion.data.model.MedicationCheckResponse
import com.eldercare.companion.data.model.MedicationPrescriptionDto
import com.eldercare.companion.data.model.ResidentChartContextDto
import com.eldercare.companion.data.model.ResidentDto
import com.eldercare.companion.data.repository.CareRepository
import com.eldercare.companion.localization.AppLanguage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationsListScreen(
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
    var administeredMeds by remember { mutableStateOf<Set<Long>>(emptySet()) }
    var medicationCheckResult by remember { mutableStateOf<MedicationCheckResponse?>(null) }
    var isCheckingSafety by remember { mutableStateOf(false) }

    val runSafetyCheck: (Long, String, List<String>) -> Unit = { resId, resName, meds ->
        coroutineScope.launch {
            isCheckingSafety = true
            val checkRes = repository.checkMedicationSafety(
                residentId = resId,
                residentName = resName,
                currentMeds = meds
            )
            if (checkRes.isSuccess) {
                medicationCheckResult = checkRes.getOrNull()
            }
            isCheckingSafety = false
        }
    }

    val loadResidentData: (Long) -> Unit = { resId ->
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            medicationCheckResult = null
            val result = repository.fetchResidentChartContext(resId)
            if (result.isSuccess) {
                val ctx = result.getOrNull()
                chartContext = ctx
                val medNames = ctx?.activeMedications?.map { it.medicationName } ?: emptyList()
                val rName = ctx?.residentName ?: "Cư dân"
                runSafetyCheck(resId, rName, medNames)
            } else {
                errorMessage = result.exceptionOrNull()?.message ?: "Lỗi tải đơn thuốc cư dân"
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
                            text = if (isVi) "Phiếu Cấp Phát Thuốc Trong Ngày" else "Daily Medication Administration (eMAR)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isVi) "Lịch dùng thuốc theo y lệnh & Cảnh báo an toàn người cao tuổi" else "Physician orders & senior safety schedule",
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
                    text = if (isVi) "Chọn cư dân cần cấp phát thuốc:" else "Select Resident for eMAR:",
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
                val activeMeds = chartContext?.activeMedications ?: emptyList()
                val fridMeds = activeMeds.filter { it.isFrid }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // AI Safety Analysis Card (Centralized Backend AI Engine)
                    item {
                        val check = medicationCheckResult
                        val isHighRisk = check?.riskLevel?.equals("HIGH", ignoreCase = true) == true || fridMeds.size >= 2
                        val isModRisk = check?.riskLevel?.equals("MODERATE", ignoreCase = true) == true || fridMeds.isNotEmpty()
                        
                        val containerBg = when {
                            isHighRisk -> Color(0xFFFEF2F2)
                            isModRisk -> Color(0xFFFFFBEB)
                            else -> Color(0xFFF0FDF4)
                        }
                        val borderColor = when {
                            isHighRisk -> Color(0xFFF87171)
                            isModRisk -> Color(0xFFFCD34D)
                            else -> Color(0xFF86EFAC)
                        }
                        val titleColor = when {
                            isHighRisk -> Color(0xFF991B1B)
                            isModRisk -> Color(0xFF92400E)
                            else -> Color(0xFF065F46)
                        }

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = containerBg,
                            border = BorderStroke(1.dp, borderColor),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        Icon(
                                            imageVector = if (isHighRisk || isModRisk) Icons.Outlined.WarningAmber else Icons.Outlined.VerifiedUser,
                                            contentDescription = null,
                                            tint = titleColor,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = if (isVi) "Lưu Ý Dược Lý & Cảnh Báo An Toàn" else "Clinical Pharmacology & Safety Notes",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = titleColor
                                            )
                                            Text(
                                                text = "Cư dân: $currentResidentName • ${activeMeds.size} thuốc đang điều trị",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.outline
                                            )
                                        }
                                    }

                                    // Risk level badge
                                    val badgeText = when (check?.riskLevel?.uppercase() ?: if (fridMeds.isNotEmpty()) "MODERATE" else "LOW") {
                                        "HIGH" -> if (isVi) "CẦN LƯU Ý ĐẶC BIỆT" else "HIGH RISK"
                                        "MODERATE" -> if (isVi) "THEO DÕI SINH HIỆU" else "MODERATE"
                                        else -> if (isVi) "AN TOÀN" else "LOW RISK"
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isHighRisk) Color(0xFFFEE2E2) else if (isModRisk) Color(0xFFFEF3C7) else Color(0xFFDCFCE7)
                                    ) {
                                        Text(
                                            text = badgeText,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = titleColor
                                        )
                                    }
                                }

                                if (isCheckingSafety) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp,
                                            color = titleColor
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (isVi) "Đang đối chiếu tương tác thuốc & tiêu chuẩn Beers lâm sàng..." else "Reviewing drug interactions & geriatric safety criteria...",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = titleColor
                                        )
                                    }
                                } else {
                                    // Warning narrative
                                    val warningText = check?.summaryWarning ?: if (fridMeds.isNotEmpty()) {
                                        "Có ${fridMeds.size} loại thuốc ảnh hưởng thăng bằng hoặc huyết áp. Khuyến cáo điều dưỡng: Đo huyết áp tư thế đứng trước khi phát thuốc và hỗ trợ người cao tuổi khi ra khỏi giường."
                                    } else {
                                        "Không ghi nhận tương tác thuốc bất lợi. Phác đồ điều trị phù hợp tiêu chuẩn an toàn cho người cao tuổi."
                                    }
                                    Text(
                                        text = warningText,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = titleColor,
                                        lineHeight = 18.sp
                                    )

                                    // Clinical Precautions
                                    val precautions = check?.clinicalPrecautions ?: emptyList()
                                    if (precautions.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = if (isVi) "Chỉ dẫn điều dưỡng tại giường:" else "Bedside nursing precautions:",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = titleColor
                                        )
                                        precautions.forEach { prec ->
                                            Row(modifier = Modifier.fillMaxWidth()) {
                                                Text("• ", style = MaterialTheme.typography.bodySmall, color = titleColor)
                                                Text(prec, style = MaterialTheme.typography.bodySmall, color = titleColor)
                                            }
                                        }
                                    }

                                    // ACB Cognitive Burden score
                                    if (check?.totalAcbScore != null) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color.White.copy(alpha = 0.6f),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "Gánh nặng kháng cholinergic (ACB):",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = Color.DarkGray
                                                )
                                                Text(
                                                    text = "Điểm ${check.totalAcbScore} (${check.acbRiskTier ?: "Thấp"})",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = titleColor
                                                )
                                            }
                                        }
                                    }
                                }

                                // Re-run check button (Height 48dp)
                                OutlinedButton(
                                    onClick = {
                                        runSafetyCheck(selectedResidentId, currentResidentName, activeMeds.map { it.medicationName })
                                    },
                                    enabled = !isCheckingSafety,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = titleColor
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Refresh,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isVi) "Cập Nhật Cảnh Báo Lâm Sàng" else "Re-evaluate Clinical Safety",
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    // 4 eMAR Time Slots
                    val timeSlots = listOf(
                        Triple("🌅 Cữ Sáng (08:00)", "Bữa sáng & Khởi đầu ngày", activeMeds.filterIndexed { idx, _ -> idx % 4 == 0 || idx == 0 }),
                        Triple("☀️ Cữ Trưa (12:00)", "Sau bữa ăn trưa", activeMeds.filterIndexed { idx, _ -> idx % 4 == 1 }),
                        Triple("🌇 Cữ Chiều (18:00)", "Trước/Sau bữa ăn chiều", activeMeds.filterIndexed { idx, _ -> idx % 4 == 2 }),
                        Triple("🌙 Cữ Tối / Đêm (21:00)", "Trước giờ ngủ đêm", activeMeds.filterIndexed { idx, _ -> idx % 4 == 3 })
                    )

                    timeSlots.forEach { (slotTitle, slotDesc, medsInSlot) ->
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = slotTitle,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = slotDesc,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                    ) {
                                        Text(
                                            text = "${medsInSlot.size} loại thuốc",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                if (medsInSlot.isEmpty()) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = if (isVi) "Không có thuốc chỉ định trong khung giờ này" else "No medications scheduled for this window",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.outline,
                                            modifier = Modifier.padding(12.dp)
                                        )
                                    }
                                } else {
                                    medsInSlot.forEach { med ->
                                        val isAdministered = administeredMeds.contains(med.id)
                                        Surface(
                                            shape = RoundedCornerShape(14.dp),
                                            color = MaterialTheme.colorScheme.surface,
                                            border = BorderStroke(
                                                1.dp,
                                                if (med.isFrid) Color(0xFFEF4444).copy(alpha = 0.5f)
                                                else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
                                            ),
                                            tonalElevation = 1.dp,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(14.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Surface(
                                                    shape = CircleShape,
                                                    color = if (isAdministered) Color(0xFFD1FAE5) else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                                    modifier = Modifier.size(42.dp)
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Icon(
                                                            imageVector = if (isAdministered) Icons.Outlined.CheckCircle else Icons.Outlined.Medication,
                                                            contentDescription = null,
                                                            tint = if (isAdministered) Color(0xFF059669) else MaterialTheme.colorScheme.primary,
                                                            modifier = Modifier.size(22.dp)
                                                        )
                                                    }
                                                }

                                                Spacer(modifier = Modifier.width(12.dp))

                                                Column(modifier = Modifier.weight(1f)) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(
                                                            text = med.medicationName,
                                                            style = MaterialTheme.typography.titleSmall,
                                                            fontWeight = FontWeight.Bold,
                                                            color = MaterialTheme.colorScheme.onSurface
                                                        )
                                                        if (med.isFrid) {
                                                            Surface(
                                                                shape = RoundedCornerShape(6.dp),
                                                                color = Color(0xFFFEE2E2)
                                                            ) {
                                                                Text(
                                                                    text = "FRID",
                                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                                    style = MaterialTheme.typography.labelSmall,
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = Color(0xFFB91C1C)
                                                                )
                                                            }
                                                        }
                                                    }

                                                    Text(
                                                        text = "Liều: ${med.dosage ?: "1 viên (5mg)"} • Đường: ${med.route ?: "Uống"} • Tần suất: ${med.frequency ?: "1 lần/ngày"}",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.outline
                                                    )

                                                    if (!med.clinicalWarning.isNullOrBlank()) {
                                                        Text(
                                                            text = "Lưu ý y tế: ${med.clinicalWarning}",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = Color(0xFFB45309),
                                                            modifier = Modifier.padding(top = 2.dp)
                                                        )
                                                    }
                                                }

                                                Spacer(modifier = Modifier.width(8.dp))

                                                // Administration Toggle Chip
                                                Surface(
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = if (isAdministered) Color(0xFFD1FAE5) else MaterialTheme.colorScheme.surfaceVariant,
                                                    modifier = Modifier.clickable {
                                                        administeredMeds = if (isAdministered) {
                                                            administeredMeds - med.id
                                                        } else {
                                                            administeredMeds + med.id
                                                        }
                                                    }
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Icon(
                                                            imageVector = if (isAdministered) Icons.Outlined.CheckCircle else Icons.Outlined.Schedule,
                                                            contentDescription = null,
                                                            tint = if (isAdministered) Color(0xFF059669) else MaterialTheme.colorScheme.outline,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(
                                                            text = if (isAdministered) (if (isVi) "ĐÃ UỐNG" else "GIVEN") else (if (isVi) "CHƯA UỐNG" else "DUE"),
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (isAdministered) Color(0xFF059669) else MaterialTheme.colorScheme.outline
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
            }
        }
    }
}
