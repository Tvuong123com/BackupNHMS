package com.eldercare.companion.ui.notifications

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eldercare.companion.data.SessionManager
import com.eldercare.companion.localization.AppLanguage

enum class NotificationCategory(val labelVi: String, val labelEn: String) {
    ALL("Tất cả", "All"),
    NEW_TASK("Việc mới", "New Tasks"),
    ASSIGNMENT("Phân công", "Assignments"),
    MEAL("Ăn uống", "Meals"),
    MEDICATION("Thuốc men", "Meds & FRID"),
    FALL("Báo ngã", "Falls")
}

data class ClinicalNotification(
    val id: String,
    val category: NotificationCategory,
    val timeAgo: String,
    val titleVi: String,
    val titleEn: String,
    val messageVi: String,
    val messageEn: String,
    val icon: ImageVector,
    val accentColor: Color,
    val isRead: Boolean = false
) {
    fun getTitle(lang: AppLanguage) = if (lang == AppLanguage.VIETNAMESE) titleVi else titleEn
    fun getMessage(lang: AppLanguage) = if (lang == AppLanguage.VIETNAMESE) messageVi else messageEn
}

@Composable
fun NotificationsScreen(
    sessionManager: SessionManager,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val isVi = language == AppLanguage.VIETNAMESE
    val userRole = sessionManager.cnaRole ?: "CNA"
    val isAdmin = userRole.contains("ADMIN", ignoreCase = true) || (sessionManager.cnaName?.contains("Admin", ignoreCase = true) == true)

    val notificationsList = remember(isAdmin) {
        mutableStateListOf<ClinicalNotification>().apply {
            if (isAdmin) {
                // Administrator / DON notifications
                addAll(
                    listOf(
                        ClinicalNotification(
                            id = "admin-notif-1",
                            category = NotificationCategory.FALL,
                            timeAgo = "5 phút trước",
                            titleVi = "Sự cố té ngã P102: Chờ Ban Giám Đốc duyệt SLA",
                            titleEn = "Incident P102: Pending DON SLA Approval",
                            messageVi = "Biên bản điều tra sự cố trượt ngã tại P102 đã được Điều dưỡng trưởng đệ trình. Cần phê duyệt trong vòng 24h.",
                            messageEn = "Investigation report for fall in P102 submitted by Charge Nurse. 24h SLA sign-off required.",
                            icon = Icons.Outlined.Gavel,
                            accentColor = Color(0xFFDC2626)
                        ),
                        ClinicalNotification(
                            id = "admin-notif-2",
                            category = NotificationCategory.ASSIGNMENT,
                            titleVi = "Bàn giao SBAR toàn viện đang chờ ký xác nhận",
                            titleEn = "Facility-wide SBAR Handover Awaiting Sign-off",
                            timeAgo = "18 phút trước",
                            messageVi = "Biên bản giao ban ca sáng của 20 cư dân đã hoàn tất. Cần Trưởng ca kiểm duyệt các can thiệp tồn đọng.",
                            messageEn = "Morning shift handover for 20 residents completed. Charge Nurse review required.",
                            icon = Icons.Outlined.AssignmentTurnedIn,
                            accentColor = Color(0xFF2563EB)
                        ),
                        ClinicalNotification(
                            id = "admin-notif-3",
                            category = NotificationCategory.NEW_TASK,
                            timeAgo = "35 phút trước",
                            titleVi = "Kế hoạch chăm sóc Care Plan 30 ngày cần rà soát",
                            titleEn = "30-Day Care Plan Periodic Clinical Review Due",
                            messageVi = "4 kế hoạch chăm sóc tại phân khu Lão khoa Tầng 1 đã đến hạn rà soát định kỳ theo quy định Bộ Y Tế.",
                            messageEn = "4 care plans in Ward 1 due for mandatory periodic clinical re-assessment.",
                            icon = Icons.Outlined.Rule,
                            accentColor = Color(0xFFD97706)
                        ),
                        ClinicalNotification(
                            id = "admin-notif-4",
                            category = NotificationCategory.MEDICATION,
                            timeAgo = "1 giờ trước",
                            titleVi = "Báo cáo an toàn dược lý: Tỉ lệ tuân thủ eMAR đạt 94.2%",
                            titleEn = "Medication Safety Audit: 94.2% eMAR Adherence",
                            messageVi = "Toàn viện đã cấp phát 86/91 cữ thuốc an toàn. Không phát hiện phản ứng phụ của thuốc nhóm FRID.",
                            messageEn = "86/91 doses administered safely across facility. Zero adverse FRID reactions.",
                            icon = Icons.Outlined.Medication,
                            accentColor = Color(0xFF059669)
                        )
                    )
                )
            } else {
                // CNA / Bedside Staff notifications
                addAll(
                    listOf(
                        ClinicalNotification(
                            id = "cna-notif-1",
                            category = NotificationCategory.FALL,
                            timeAgo = "2 phút trước",
                            titleVi = "Báo động thảm giường: Cụ Nguyễn Văn Hùng (P102)",
                            titleEn = "Bed Pressure Mat Alert: Resident Nguyen Van Hung (P102)",
                            messageVi = "Cảm biến giường phát hiện cụ Hùng đang cố gắng rời giường không có hỗ trợ. Đề nghị đến hỗ trợ ngay!",
                            messageEn = "Pressure sensor indicates unassisted egress attempt in Room 102. Immediate bedside assist requested!",
                            icon = Icons.Outlined.WarningAmber,
                            accentColor = Color(0xFFE11D48)
                        ),
                        ClinicalNotification(
                            id = "cna-notif-2",
                            category = NotificationCategory.NEW_TASK,
                            timeAgo = "12 phút trước",
                            titleVi = "Nhiệm vụ mới được Trưởng ca phân công",
                            titleEn = "New Clinical Task Assigned by Charge Nurse",
                            messageVi = "Chỉ định can thiệp bổ sung: Đo lại huyết áp và bật đèn ngủ ban đêm tại phòng P102 và P104.",
                            messageEn = "Intervention added: Retake BP and maintain night lighting in Room 102 and 104.",
                            icon = Icons.Outlined.AssignmentTurnedIn,
                            accentColor = Color(0xFF0D9488)
                        ),
                        ClinicalNotification(
                            id = "cna-notif-3",
                            category = NotificationCategory.MEDICATION,
                            timeAgo = "25 phút trước",
                            titleVi = "Nhắc nhở cữ thuốc eMAR 12:00 & Cảnh báo FRID",
                            titleEn = "eMAR 12:00 Reminder & FRID Precautions",
                            messageVi = "Cụ Lê Thị Mai (P104) có đơn thuốc Amlodipine 5mg. Chú ý nguy cơ hạ huyết áp tư thế khi cụ đứng dậy.",
                            messageEn = "Resident Le Thi Mai (P104) order includes Amlodipine 5mg. Monitor for orthostatic dizziness.",
                            icon = Icons.Outlined.Medication,
                            accentColor = Color(0xFFD97706)
                        ),
                        ClinicalNotification(
                            id = "cna-notif-4",
                            category = NotificationCategory.MEAL,
                            timeAgo = "45 phút trước",
                            titleVi = "Theo dõi khẩu phần ăn trưa: Cụ Trần Thị Bích (P101)",
                            titleEn = "Meal Intake Tracking: Resident Tran Thi Bich (P101)",
                            messageVi = "Cụ Bích đã dùng 85% khẩu phần súp gà dinh dưỡng và uống 250ml nước lọc. Ghi nhận dinh dưỡng hoàn tất.",
                            messageEn = "Resident Bich finished 85% nutritional soup and 250ml water. Intake record updated.",
                            icon = Icons.Outlined.Restaurant,
                            accentColor = Color(0xFF10B981)
                        ),
                        ClinicalNotification(
                            id = "cna-notif-5",
                            category = NotificationCategory.ASSIGNMENT,
                            timeAgo = "1 giờ trước",
                            titleVi = "Phân công phụ trách buồng bệnh ca trực hôm nay",
                            titleEn = "Shift Ward Assignment for Today",
                            messageVi = "Bạn được phân công phụ trách chính các phòng P101 đến P104 (10 cư dân). Vui lòng kiểm tra danh sách nhiệm vụ.",
                            messageEn = "You are assigned to Rooms 101 to 104 (10 residents). Please review your shift tasks.",
                            icon = Icons.Outlined.People,
                            accentColor = Color(0xFF3B82F6)
                        )
                    )
                )
            }
        }
    }

    var selectedCategory by remember { mutableStateOf(NotificationCategory.ALL) }
    var selectedNotification by remember { mutableStateOf<ClinicalNotification?>(null) }

    val filteredList = remember(notificationsList, selectedCategory) {
        if (selectedCategory == NotificationCategory.ALL) notificationsList
        else notificationsList.filter { it.category == selectedCategory }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Spacious Header - Never squeezed or wrapped
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text(
                    text = if (isVi) "Thông Báo Lâm Sàng" else "Clinical Notifications",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${if (isAdmin) "Tài khoản Quản trị (Admin)" else "Điều dưỡng (CNA)"} • Cập nhật tức thời",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }

            // Compact Pill: "Đã xem hết"
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                modifier = Modifier.clickable {
                    val updated = notificationsList.map { it.copy(isRead = true) }
                    notificationsList.clear()
                    notificationsList.addAll(updated)
                }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DoneAll,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isVi) "Đã xem hết" else "Read All",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Category Filter Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(NotificationCategory.values()) { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { selectedCategory = cat },
                    label = { Text(if (isVi) cat.labelVi else cat.labelEn) },
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Notifications List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredList, key = { it.id }) { notif ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (notif.isRead) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                    tonalElevation = if (notif.isRead) 1.dp else 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedNotification = notif
                            val index = notificationsList.indexOfFirst { it.id == notif.id }
                            if (index != -1 && !notificationsList[index].isRead) {
                                notificationsList[index] = notificationsList[index].copy(isRead = true)
                            }
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = notif.accentColor.copy(alpha = 0.15f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = notif.icon,
                                    contentDescription = null,
                                    tint = notif.accentColor,
                                    modifier = Modifier.size(20.dp)
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
                                    text = notif.getTitle(language),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = if (notif.isRead) FontWeight.SemiBold else FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )

                                if (!notif.isRead) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = notif.getMessage(language),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = notif.timeAgo,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
        }

        // Notification Detail Dialog
        selectedNotification?.let { notif ->
            AlertDialog(
                onDismissRequest = { selectedNotification = null },
                icon = {
                    Icon(
                        imageVector = notif.icon,
                        contentDescription = null,
                        tint = notif.accentColor,
                        modifier = Modifier.size(32.dp)
                    )
                },
                title = {
                    Text(
                        text = notif.getTitle(language),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = notif.getMessage(language),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Phân loại: ${if (isVi) notif.category.labelVi else notif.category.labelEn}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = notif.timeAgo,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { selectedNotification = null }) {
                        Text(if (isVi) "Đã hiểu" else "Acknowledge")
                    }
                }
            )
        }
    }
}
