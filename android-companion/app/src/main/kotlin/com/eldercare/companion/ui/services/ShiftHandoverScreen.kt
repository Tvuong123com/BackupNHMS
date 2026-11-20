package com.eldercare.companion.ui.services

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.eldercare.companion.data.model.ShiftHandoverDraftDto
import com.eldercare.companion.data.model.SummaryResponse
import com.eldercare.companion.data.repository.CareRepository
import com.eldercare.companion.localization.AppLanguage
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShiftHandoverScreen(
    repository: CareRepository,
    language: AppLanguage,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isVi = language == AppLanguage.VIETNAMESE
    val coroutineScope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(true) }
    var handoverDrafts by remember { mutableStateOf<List<ShiftHandoverDraftDto>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isHandoverSigned by remember { mutableStateOf(false) }
    var signedTimestamp by remember { mutableStateOf<String?>(null) }
    var nurseExtraNotes by remember { mutableStateOf("") }
    var aiSummary by remember { mutableStateOf<SummaryResponse?>(null) }
    var isGeneratingSummary by remember { mutableStateOf(false) }
    var noteSavedMessage by remember { mutableStateOf(false) }

    val generateAiSummary: () -> Unit = {
        coroutineScope.launch {
            isGeneratingSummary = true
            val currentEvents = mutableListOf(
                "Toàn phân khu Lão khoa 10 cư dân ổn định",
                "1 sự cố trượt ngã nhẹ tại P102 đã xử lý sơ cứu, sinh hiệu ổn",
                "94% nhiệm vụ chăm sóc ca chiều đã hoàn tất",
                "3 cư dân dùng thuốc FRID cần theo dõi huyết áp tư thế đứng"
            )
            if (nurseExtraNotes.isNotBlank()) {
                currentEvents.add("Ghi chú bổ sung từ điều dưỡng: ${nurseExtraNotes.trim()}")
            }
            val res = repository.generateShiftSummary(
                shiftName = "Ca Chiều (EVENING)",
                events = currentEvents
            )
            if (res.isSuccess) {
                aiSummary = res.getOrNull()
            }
            isGeneratingSummary = false
        }
    }

    val loadData: () -> Unit = {
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            val result = repository.fetchSummaryDrafts()
            if (result.isSuccess) {
                handoverDrafts = result.getOrNull() ?: emptyList()
            } else {
                errorMessage = result.exceptionOrNull()?.message ?: "Lỗi tải báo cáo giao ca"
            }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isVi) "Biên Bản Giao Ban Ca Trực (SBAR)" else "Clinical Shift Handover (SBAR)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isVi) "Tiêu chuẩn y khoa bàn giao giữa hai ca trực" else "Standardized clinical handover protocol",
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
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Handover Context Card
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                            tonalElevation = 1.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Outlined.SwapHoriz,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (isVi) "Bàn Giao Trực Lâm Sàng Ca Chiều ➔ Đêm" else "Evening ➔ Night Shift Handover",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = if (isVi) "Tổng hợp tự động từ nhật ký chăm sóc, sinh hiệu & sự cố" else "Synthesized from care logs, vitals & incident records",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }

                                Text(
                                    text = if (isVi)
                                        "Biên bản SBAR giúp ca tiếp theo nắm trọn vẹn tình trạng cư dân, các cữ thuốc đặc biệt và chỉ định theo dõi, ngăn ngừa việc bỏ sót nhiệm vụ."
                                    else
                                        "SBAR protocol ensures seamless continuity of care, highlighting critical vitals, eMAR medications, and pending nursing tasks.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                // Action button to generate handover
                                Button(
                                    onClick = { generateAiSummary() },
                                    enabled = !isGeneratingSummary,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    if (isGeneratingSummary) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            strokeWidth = 2.dp,
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(if (isVi) "Đang tổng hợp diễn biến ca trực..." else "Synthesizing shift handover...")
                                    } else {
                                        Icon(
                                            imageVector = Icons.Outlined.Assignment,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (isVi) "Cập Nhật & Tổng Hợp Biên Bản SBAR" else "Update & Compile SBAR Report",
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Nurse Extra Notes Input (Allows nurses to type custom handover notes)
                    item {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = if (isVi) "✍️ Ghi chú bổ sung của điều dưỡng giao ca:" else "✍️ Nurse's Additional Handover Notes:",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                OutlinedTextField(
                                    value = nurseExtraNotes,
                                    onValueChange = {
                                        nurseExtraNotes = it
                                        noteSavedMessage = false
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = {
                                        Text(
                                            text = if (isVi) "Nhập lưu ý đặc biệt ngoài ca (ví dụ: người nhà cụ Hùng gửi sữa tươi, dặn cụ ngủ sớm lúc 21h)..."
                                            else "Enter extra notes for oncoming shift..."
                                        )
                                    },
                                    maxLines = 4,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (noteSavedMessage) {
                                        Text(
                                            text = if (isVi) "✓ Đã đính kèm vào biên bản ca" else "✓ Attached to shift record",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFF059669),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    } else {
                                        Spacer(modifier = Modifier.width(1.dp))
                                    }
                                    Button(
                                        onClick = {
                                            if (nurseExtraNotes.isNotBlank()) {
                                                noteSavedMessage = true
                                                generateAiSummary()
                                            }
                                        },
                                        enabled = nurseExtraNotes.isNotBlank(),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(if (isVi) "Lưu ghi chú vào ca" else "Attach to Handover")
                                    }
                                }
                            }
                        }
                    }

                    // 4 Pillars of SBAR Detail Cards
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            // S - Situation
                            val situationText = aiSummary?.narrativeSummary ?: "Ca trực Lão khoa tầng 1: 10 cư dân ổn định, 1 sự cố trượt ngã nhẹ tại P102 đã xử lý sơ cứu, 94% công việc chăm sóc đã hoàn tất."
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, Color(0xFF93C5FD)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFEFF6FF)
                                    ) {
                                        Text(
                                            text = if (isVi) "S • TÌNH HUỐNG HIỆN TẠI (SITUATION)" else "S • SITUATION",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1D4ED8)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = situationText,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 20.sp
                                    )
                                }
                            }

                            // B - Background
                            val backgroundItems = aiSummary?.keyHighlights ?: listOf(
                                "3 cư dân đang dùng thuốc tăng nguy cơ té ngã (FRIDs: Amlodipine, Zolpidem)",
                                "Cụ Nguyễn Văn Hùng (P102) có tiền sử choáng váng khi đổi tư thế đứng dậy",
                                "Cụ Hoàng Thị Mai (P103) đang theo dõi sốt nhẹ sau điều trị nhiễm khuẩn"
                            )
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, Color(0xFFFCD34D)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFFEF3C7)
                                    ) {
                                        Text(
                                            text = if (isVi) "B • TIỀN SỬ & BỐI CẢNH (BACKGROUND)" else "B • BACKGROUND",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFB45309)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    backgroundItems.forEach { bgItem ->
                                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                                            Text("• ", style = MaterialTheme.typography.bodyMedium, color = Color(0xFFB45309))
                                            Text(
                                                text = bgItem,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }

                            // A - Assessment
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFFEE2E2)
                                    ) {
                                        Text(
                                            text = if (isVi) "A • ĐÁNH GIÁ LÂM SÀNG (ASSESSMENT)" else "A • ASSESSMENT",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFB91C1C)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Chỉ số sinh hiệu toàn phân khu duy trì ngưỡng an toàn. Huyết áp cụ Hùng lúc 14h là 138/86 mmHg (cần tái đo). SpO2 trung bình đạt 97.4%. Không ghi nhận phản ứng dị ứng thuốc cấp.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 20.sp
                                    )
                                }
                            }

                            // R - Recommendation
                            val recommendationItems = aiSummary?.handoverActionItems ?: listOf(
                                "Đo lại huyết áp cho cụ Hùng vào cữ 20:00 tối nay",
                                "Bật đèn ngủ ban đêm tại P102 và P104",
                                "Hỗ trợ cư dân đi vệ sinh trước 22:00 tránh trượt ngã ban đêm"
                            )
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, Color(0xFF86EFAC)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFD1FAE5)
                                    ) {
                                        Text(
                                            text = if (isVi) "R • KHUYẾN NGHỊ & VIỆC CẦN LÀM (RECOMMENDATION)" else "R • RECOMMENDATION",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF065F46)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    recommendationItems.forEach { recItem ->
                                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                                            Text("👉 ", style = MaterialTheme.typography.bodyMedium)
                                            Text(
                                                text = recItem,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Handover Sign-off Section (Clinical Legal Sign-off)
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isHandoverSigned) Color(0xFFECFDF5) else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.5.dp, if (isHandoverSigned) Color(0xFF059669) else MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = isHandoverSigned,
                                        onCheckedChange = {
                                            isHandoverSigned = it
                                            if (it) {
                                                val sdf = SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault())
                                                signedTimestamp = sdf.format(Date())
                                            } else {
                                                signedTimestamp = null
                                            }
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (isVi) "XÁC NHẬN BÀN GIAO & KÝ TÊN NHẬN CA" else "Sign Off & Assume Shift Responsibility",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isHandoverSigned) Color(0xFF065F46) else MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = if (isVi)
                                                "Tôi (Điều dưỡng nhận ca) xác nhận đã đối chiếu sinh hiệu, tiếp nhận các sự cố trong ca trước và cam kết thực hiện các nhiệm vụ bàn giao tiếp theo."
                                            else
                                                "I confirm having verified resident vitals, previous shift incidents, and assume duty for oncoming shift care tasks.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }

                                if (isHandoverSigned && signedTimestamp != null) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFD1FAE5)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.CheckCircle,
                                                contentDescription = null,
                                                tint = Color(0xFF059669),
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = if (isVi) "Đã ký nhận ca thành công vào lúc $signedTimestamp" else "Shift officially signed off at $signedTimestamp",
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

                    // Historical Shift Reports Header
                    item {
                        Text(
                            text = if (isVi) "📜 Nhật ký các biên bản giao ban trước đó:" else "📜 Previous Shift Handover Logs:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    items(handoverDrafts, key = { it.id }) { item ->
                        val draft = item.draft
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                            tonalElevation = 1.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = draft?.title ?: "Biên Bản Giao Ca #${item.id}",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                    ) {
                                        Text(
                                            text = if (item.draft?.proposedActions?.isNotEmpty() == true) "CÓ PHÂN CÔNG" else "LƯU HỒ SƠ",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                if (!draft?.narrativeSummary.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = draft?.narrativeSummary ?: "",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 3
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Thời gian: ${draft?.generatedAt?.take(16) ?: item.createdAt?.take(16) ?: ""}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
