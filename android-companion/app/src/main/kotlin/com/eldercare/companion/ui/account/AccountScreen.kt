package com.eldercare.companion.ui.account

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eldercare.companion.data.SessionManager
import com.eldercare.companion.data.repository.CareRepository
import com.eldercare.companion.localization.AppLanguage
import com.eldercare.companion.ui.components.EditProfileDialog

data class QuickCareShortcut(
    val id: String,
    val titleVi: String,
    val titleEn: String,
    val subtitleVi: String,
    val subtitleEn: String,
    val icon: ImageVector,
    val targetTab: Int // 0: Home, 1: Activities, 2: Tasks, 3: Alerts
)

@Composable
fun AccountScreen(
    repository: CareRepository,
    sessionManager: SessionManager,
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onLogoutClick: () -> Unit,
    onNavigateTab: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isVi = language == AppLanguage.VIETNAMESE
    val scrollState = rememberScrollState()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showWorkflowInfoDialog by remember { mutableStateOf(false) }

    var displayName by remember { mutableStateOf(sessionManager.cnaName ?: "Daniel Brooks") }
    var displayPhone by remember { mutableStateOf(sessionManager.cnaPhone) }
    var displayNotes by remember { mutableStateOf(sessionManager.cnaNotes) }

    val shortcuts = remember {
        listOf(
            QuickCareShortcut(
                id = "qc-1",
                titleVi = "Cảnh báo ngã & Cảm biến giường",
                titleEn = "Fall Alerts & Bed Sensors",
                subtitleVi = "Giám sát áp lực thảm giường và sự cố",
                subtitleEn = "Bed pressure & incident tracker",
                icon = Icons.Outlined.WarningAmber,
                targetTab = 3
            ),
            QuickCareShortcut(
                id = "qc-2",
                titleVi = "Danh sách ca trực & Nhiệm vụ SOP",
                titleEn = "Shift Tasks & SOP Rounds",
                subtitleVi = "Nhiệm vụ chăm sóc và ghi chú giường",
                subtitleEn = "Bedside rounds & voice notes",
                icon = Icons.Outlined.AssignmentTurnedIn,
                targetTab = 2
            ),
            QuickCareShortcut(
                id = "qc-3",
                titleVi = "Lịch phân công & Hồ sơ 360°",
                titleEn = "Shift Schedule & 360° Profile",
                subtitleVi = "Phân loại theo cư dân toàn phân khu",
                subtitleEn = "Categorized by assigned residents",
                icon = Icons.Outlined.People,
                targetTab = 1
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section 1: Profile Card (Avatar, Name, Phone, Role and Pencil Edit Icon)
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.primary,
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar circle with clean border
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier
                            .size(60.dp)
                            .border(2.dp, Color.White, CircleShape)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(34.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = displayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = displayPhone,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Text(
                            text = "${sessionManager.cnaRole ?: "Điều Dưỡng Viên"} • ${if (isVi) "Ca Trực Nội Viện" else "All-Ward Shift"}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }

                    // Pencil Edit Icon Button
                    IconButton(
                        onClick = { showEditProfileDialog = true },
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color.White.copy(alpha = 0.25f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Edit Profile",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                if (displayNotes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White.copy(alpha = 0.15f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = displayNotes,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.95f)
                        )
                    }
                }
            }
        }

        // Section 2: Clinical Workflow & Human-in-the-loop Explanation Card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showWorkflowInfoDialog = true }
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFEFF6FF),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.Security,
                            contentDescription = null,
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isVi) "Quy Trình Phân Công AI & An Toàn Y Tế" else "AI Task Approval & Clinical Protocol",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isVi) "Cơ chế Human-in-the-loop: Trưởng ca phê duyệt trên Web" else "Human-in-the-loop: Charge Nurse approval on Web",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Section 3: Clean Vertical Quick Care Shortcuts
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = if (isVi) "Tiện Ích Chăm Sóc Nhanh" else "Quick Care Shortcuts",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                shortcuts.forEach { item ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                        tonalElevation = 1.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateTab(item.targetTab) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isVi) item.titleVi else item.titleEn,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isVi) item.subtitleVi else item.subtitleEn,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }

                            Icon(
                                imageVector = Icons.Outlined.ChevronRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // Section 4: Vertical Preferences & Settings
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = if (isVi) "Cài Đặt & Ngôn Ngữ" else "Preferences & Language",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                tonalElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Language Switch Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val nextLang = if (isVi) AppLanguage.ENGLISH else AppLanguage.VIETNAMESE
                                sessionManager.appLanguage = nextLang.code
                                onLanguageChange(nextLang)
                            }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Language,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isVi) "Ngôn ngữ ứng dụng" else "App Language",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = if (isVi) "Tiếng Việt (VI)" else "English (EN)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = if (isVi) "Chuyển EN" else "Switch VI",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                    // Clinical Guidelines Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.LocalHospital,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isVi) "Tiêu chuẩn lâm sàng NHMS" else "NHMS Clinical Guidelines",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = if (isVi) "Quy trình điều dưỡng Bộ Y Tế & SBAR" else "MOH & SBAR Nursing Standards",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                    // App Version Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "NHMS CareCompanion",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = if (isVi) "Phiên bản v5.0.0 • Kết nối SQL Server Backend" else "Version v5.0.0 • Connected to SQL Server",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
        }

        // Section 5: Logout Button
        Button(
            onClick = onLogoutClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.Logout,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isVi) "Đăng xuất tài khoản" else "Sign Out",
                fontWeight = FontWeight.Bold
            )
        }
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        EditProfileDialog(
            initialName = displayName,
            initialPhone = displayPhone,
            initialNotes = displayNotes,
            language = language,
            onSave = { newName, newPhone, newNotes ->
                displayName = newName
                displayPhone = newPhone
                displayNotes = newNotes
                sessionManager.cnaName = newName
                sessionManager.cnaPhone = newPhone
                sessionManager.cnaNotes = newNotes
                showEditProfileDialog = false
            },
            onDismiss = { showEditProfileDialog = false }
        )
    }

    // Workflow Explanation Dialog
    if (showWorkflowInfoDialog) {
        AlertDialog(
            onDismissRequest = { showWorkflowInfoDialog = false },
            title = {
                Text(
                    text = if (isVi) "Quy Trình Phân Công AI Lâm Sàng" else "Clinical AI Workflow Protocol",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (isVi)
                            "1. Trên Web Admin, các nhiệm vụ hàng ngày được quản lý tại mục 'Nhiệm vụ Điều dưỡng (CNA Daily Tasks)' và trong hồ sơ từng cư dân."
                        else
                            "1. On Admin Web, daily tasks are managed under 'CNA Daily Tasks' and within resident charts.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = if (isVi)
                            "2. AI đề xuất can thiệp (DFRS phòng ngừa ngã, SBAR giao ca) KHÔNG tự động ghi thẳng vào bảng công việc nhằm đảm bảo an toàn y tế (Human-in-the-loop)."
                        else
                            "2. AI recommendations (DFRS fall prevention, SBAR handover) do NOT automatically write to tasks for medical safety (Human-in-the-loop).",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = if (isVi)
                            "3. Điều dưỡng trưởng (Charge Nurse / DON) xem đề xuất trên Web Admin tại '/admin/ai/shift-handover' hoặc '/admin/ai/fall-risk-monitoring' và bấm 'Gán nhiệm vụ cho Điều dưỡng' -> Backend lưu vào bảng 'care_tasks' và xuất hiện ngay trên điện thoại của CNA."
                        else
                            "3. Charge Nurse reviews AI proposals on Web Admin and clicks 'Assign to CNA' -> Stored into 'care_tasks' and instantly displayed on mobile.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showWorkflowInfoDialog = false }) {
                    Text(if (isVi) "Đã hiểu" else "Understood")
                }
            }
        )
    }
}
