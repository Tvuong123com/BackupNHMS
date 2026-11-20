package com.eldercare.companion.ui.services

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eldercare.companion.data.model.DynamicFallRiskDto
import com.eldercare.companion.data.repository.CareRepository
import com.eldercare.companion.localization.AppLanguage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FallRiskListScreen(
    repository: CareRepository,
    language: AppLanguage,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isVi = language == AppLanguage.VIETNAMESE
    val coroutineScope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(true) }
    var fallPredictions by remember { mutableStateOf<List<DynamicFallRiskDto>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedRiskFilter by remember { mutableStateOf("ALL") }
    var selectedRiskForDetail by remember { mutableStateOf<DynamicFallRiskDto?>(null) }

    val loadData: () -> Unit = {
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            val result = repository.fetchFallPredictions()
            if (result.isSuccess) {
                fallPredictions = result.getOrNull() ?: emptyList()
            } else {
                errorMessage = result.exceptionOrNull()?.message ?: "Lỗi tải đánh giá nguy cơ té ngã"
            }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    val filteredList = remember(fallPredictions, searchQuery, selectedRiskFilter) {
        fallPredictions.filter { item ->
            val matchSearch = searchQuery.isBlank() ||
                item.residentName.contains(searchQuery, ignoreCase = true) ||
                (item.roomNumber != null && item.roomNumber.contains(searchQuery, ignoreCase = true))

            val matchFilter = when (selectedRiskFilter) {
                "HIGH" -> item.riskLevel.equals("HIGH", ignoreCase = true)
                "MODERATE" -> item.riskLevel.equals("MODERATE", ignoreCase = true) || item.riskLevel.equals("MEDIUM", ignoreCase = true)
                "LOW" -> item.riskLevel.equals("LOW", ignoreCase = true)
                else -> true
            }
            matchSearch && matchFilter
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isVi) "Đánh Giá Nguy Cơ Té Ngã (DFRS)" else "Dynamic Fall Risk (DFRS 2.0)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isVi) "${filteredList.size}/${fallPredictions.size} cư dân • Chạm để xem phân tích chi tiết" else "${filteredList.size}/${fallPredictions.size} residents • Tap for details",
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
                    IconButton(onClick = { loadData() }) {
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
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                placeholder = {
                    Text(if (isVi) "Tìm cư dân theo tên hoặc số phòng..." else "Search by name or room...")
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.Search, contentDescription = null)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Outlined.Close, contentDescription = null)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Filter Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf(
                    "ALL" to (if (isVi) "Tất cả" else "All"),
                    "HIGH" to (if (isVi) "Nguy cơ cao" else "High Risk"),
                    "MODERATE" to (if (isVi) "Nguy cơ vừa" else "Moderate"),
                    "LOW" to (if (isVi) "Nguy cơ thấp" else "Low Risk")
                )
                items(filters) { (key, label) ->
                    FilterChip(
                        selected = selectedRiskFilter == key,
                        onClick = { selectedRiskFilter = key },
                        label = { Text(label) },
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
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
                        Button(onClick = { loadData() }) {
                            Text(if (isVi) "Thử lại" else "Retry")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredList, key = { it.residentId }) { risk ->
                        val riskColor = when (risk.riskLevel.uppercase()) {
                            "HIGH" -> Color(0xFFDC2626)
                            "MODERATE", "MEDIUM" -> Color(0xFFD97706)
                            else -> Color(0xFF059669)
                        }

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 2.dp,
                            border = BorderStroke(1.dp, riskColor.copy(alpha = 0.25f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedRiskForDetail = risk }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = risk.residentName,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${if (isVi) "Phòng" else "Room"}: ${risk.roomNumber ?: "101"} • Mã hồ sơ: #${risk.residentId}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = riskColor.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = "${risk.riskLevel} (${risk.dfrsScore}/100)",
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = riskColor
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Text(
                                        text = "⚠️ ${risk.recentFallsCount} ${if (isVi) "lần ngã gần đây" else "recent falls"}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "💊 ${risk.activeFridCount} ${if (isVi) "thuốc FRID" else "active FRIDs"}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                if (risk.contributingFactors.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = if (isVi) "Yếu tố chính: ${risk.contributingFactors.firstOrNull() ?: ""}" else "Main factor: ${risk.contributingFactors.firstOrNull() ?: ""}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.outline,
                                        maxLines = 1
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (isVi) "👉 Chạm vào để xem chi tiết điểm thành phần & can thiệp" else "👉 Tap to view score breakdown & care interventions",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Detail Bottom Sheet
    selectedRiskForDetail?.let { detail ->
        val riskColor = when (detail.riskLevel.uppercase()) {
            "HIGH" -> Color(0xFFDC2626)
            "MODERATE", "MEDIUM" -> Color(0xFFD97706)
            else -> Color(0xFF059669)
        }

        ModalBottomSheet(
            onDismissRequest = { selectedRiskForDetail = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = detail.residentName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${if (isVi) "Phòng" else "Room"} ${detail.roomNumber ?: "101"} • Mã cư dân: #${detail.residentId}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = riskColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "${detail.riskLevel} (${detail.dfrsScore}/100)",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = riskColor
                        )
                    }
                }

                HorizontalDivider()

                // 4 Subscores Breakdown
                Text(
                    text = if (isVi) "Phân Tích 4 Trục Điểm DFRS (Thang 100):" else "DFRS 4-Dimensional Subscore Breakdown:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Incident History
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = if (isVi) "Tiền sử ngã" else "Incidents", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            Text(text = "${detail.incidentSubscore}/35", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                        }
                    }

                    // FRID Meds
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = if (isVi) "Thuốc FRID" else "FRID Drugs", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            Text(text = "${detail.medicationSubscore}/30", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                        }
                    }

                    // ADL Mobility
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = if (isVi) "Vận động" else "Mobility", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            Text(text = "${detail.adlSubscore}/25", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                        }
                    }

                    // Vitals
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = if (isVi) "Sinh hiệu" else "Vitals", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            Text(text = "${detail.vitalSubscore}/10", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
                        }
                    }
                }

                // Contributing Factors
                if (detail.contributingFactors.isNotEmpty()) {
                    Text(
                        text = if (isVi) "Yếu tố lâm sàng gia tăng nguy cơ:" else "Contributing clinical risk factors:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        detail.contributingFactors.forEach { factor ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text("• ", style = MaterialTheme.typography.bodyMedium, color = Color(0xFFDC2626))
                                Text(factor, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }

                // Recommended Interventions
                if (detail.recommendedInterventions.isNotEmpty()) {
                    Text(
                        text = if (isVi) "Phác đồ can thiệp phòng ngừa ngã:" else "Recommended Fall Interventions:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        detail.recommendedInterventions.forEach { itv ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = itv.role ?: "CNA",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = itv.priority ?: "ROUTINE",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = itv.action ?: "",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    if (!itv.rationale.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = itv.rationale,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = { selectedRiskForDetail = null },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (isVi) "Đóng" else "Close")
                }
            }
        }
    }
}
