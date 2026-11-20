package com.eldercare.companion.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eldercare.companion.R
import com.eldercare.companion.data.model.HealthArticle
import com.eldercare.companion.localization.AppLanguage
import com.eldercare.companion.localization.AppStrings
import com.eldercare.companion.ui.components.ArticleDetailDialog
import kotlinx.coroutines.delay
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class ServiceItem(
    val id: String,
    val titleVi: String,
    val titleEn: String,
    val icon: ImageVector,
    val actionType: ServiceAction
)

enum class ServiceAction {
    RESIDENTS,
    CARE_PLANS,
    MEDICATIONS,
    FALL_RISK,
    INCIDENTS,
    SHIFT_HANDOVER,
    VITALS,
    AI_STAFF
}

data class BannerAd(
    val drawableResId: Int,
    val title: String
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    cnaName: String,
    completedTasksCount: Int,
    totalTasksCount: Int,
    language: AppLanguage,
    onNavigateToTasks: () -> Unit,
    onOpenService: (ServiceAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val isVi = language == AppLanguage.VIETNAMESE
    val scrollState = rememberScrollState()

    // Real dynamic shift calculation matching backend database table `shifts`
    val currentShiftInfo = remember {
        val now = LocalTime.now()
        val hour = now.hour
        val timeStr = now.format(DateTimeFormatter.ofPattern("HH:mm"))
        when {
            hour in 7..14 -> Pair("Ca Sáng (DAY) • 07:00 - 15:00", "Day Shift (DAY) • 07:00 - 15:00")
            hour in 15..22 -> Pair("Ca Chiều (EVENING) • 15:00 - 23:00", "Evening Shift (EVENING) • 15:00 - 23:00")
            else -> Pair("Ca Đêm (NIGHT) • 23:00 - 07:00", "Night Shift (NIGHT) • 23:00 - 07:00")
        }
    }

    val pendingCount = (totalTasksCount - completedTasksCount).coerceAtLeast(0)
    val displayCompleted = completedTasksCount
    val totalDisplay = totalTasksCount
    val progressPercent = if (totalDisplay > 0) (displayCompleted * 100) / totalDisplay else 0

    // Selected article for reading
    var selectedArticle by remember { mutableStateOf<HealthArticle?>(null) }

    // 4 High-resolution advertisement banners (Graphics-only, typography designed into images)
    val banners = remember {
        listOf(
            BannerAd(R.drawable.banner_ad_sync, "Đồng bộ điều hành lâm sàng"),
            BannerAd(R.drawable.banner_ad_bedside, "Chăm sóc toàn diện tại giường"),
            BannerAd(R.drawable.banner_ad_garden, "Trị liệu ký ức & Vận động"),
            BannerAd(R.drawable.banner_ad_fallrisk, "Cảnh báo té ngã chủ động")
        )
    }

    // Auto-sliding pager state (4-second interval)
    val pagerState = rememberPagerState(pageCount = { banners.size })
    LaunchedEffect(pagerState) {
        while (true) {
            delay(4000)
            val nextPage = (pagerState.currentPage + 1) % banners.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    // 8 Dedicated Clinical Services from Admin Web
    val services = remember {
        listOf(
            ServiceItem("srv-1", "Hồ sơ cư dân", "Residents", Icons.Outlined.People, ServiceAction.RESIDENTS),
            ServiceItem("srv-2", "Kế hoạch CS", "Care Plans", Icons.Outlined.Assignment, ServiceAction.CARE_PLANS),
            ServiceItem("srv-3", "Thuốc & eMAR", "Medications", Icons.Outlined.Medication, ServiceAction.MEDICATIONS),
            ServiceItem("srv-4", "Đánh giá ngã", "Fall Risk", Icons.Outlined.HealthAndSafety, ServiceAction.FALL_RISK),
            ServiceItem("srv-5", "Sự cố nội viện", "Incidents", Icons.Outlined.ReportProblem, ServiceAction.INCIDENTS),
            ServiceItem("srv-6", "Giao ban SBAR", "Handover", Icons.Outlined.SyncAlt, ServiceAction.SHIFT_HANDOVER),
            ServiceItem("srv-7", "Sinh hiệu", "Vitals", Icons.Outlined.MonitorHeart, ServiceAction.VITALS),
            ServiceItem("srv-8", "Trợ lý AI", "AI Assistant", Icons.Filled.SmartToy, ServiceAction.AI_STAFF)
        )
    }

    val articles = remember { HealthArticle.getSampleArticles() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Introduction & Shift Card with real-time shift
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "${if (isVi) "Xin chào," else "Welcome,"} $cnaName",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Viện Dưỡng Lão NHMS • ${if (isVi) currentShiftInfo.first else currentShiftInfo.second}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Stats: Pending vs Completed tasks
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Pending card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigateToTasks() }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isVi) "Cần làm" else "Pending",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFD97706)
                        )
                        Icon(
                            imageVector = Icons.Outlined.PendingActions,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$pendingCount",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD97706)
                    )
                    Text(
                        text = if (isVi) "Đầu việc còn lại" else "Tasks remaining",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            // Completed card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigateToTasks() }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isVi) "Đã hoàn thành" else "Completed",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF059669)
                        )
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF059669),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$displayCompleted ($progressPercent%)",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF059669)
                    )
                    Text(
                        text = if (isVi) "Tiến độ trong ngày" else "Shift progress",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }

        // Auto-sliding Banner Carousel: pure image graphics with pre-edited artistic text
        Column(modifier = Modifier.fillMaxWidth()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(145.dp)
            ) { page ->
                val banner = banners[page]
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    tonalElevation = 3.dp,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Image(
                        painter = painterResource(id = banner.drawableResId),
                        contentDescription = banner.title,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(18.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // Dot indicators
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(banners.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .size(if (isSelected) 16.dp else 6.dp, 6.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                            )
                    )
                }
            }
        }

        // Section: Clinical Services Grid (8 dedicated screens from Admin Web)
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = if (isVi) "Dịch Vụ Lâm Sàng Nội Viện" else "Clinical Services Hub",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))

            // 4x2 Minimalist Outlined Grid
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                for (row in services.chunked(4)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (item in row) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface,
                                tonalElevation = 1.dp,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onOpenService(item.actionType) }
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = if (isVi) item.titleVi else item.titleEn,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = if (isVi) item.titleVi else item.titleEn,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section: Health & Senior Care News
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isVi) "Tin Tức Y Tế & Dưỡng Lão" else "Healthcare & Senior Care News",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (isVi) "Xem tất cả" else "View all",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(articles, key = { it.id }) { article ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 1.dp,
                        modifier = Modifier
                            .width(220.dp)
                            .clickable { selectedArticle = article }
                    ) {
                        Column {
                            Image(
                                painter = painterResource(id = article.imageRes),
                                contentDescription = article.getTitle(language),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)),
                                contentScale = ContentScale.Crop
                            )

                            Column(modifier = Modifier.padding(10.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                ) {
                                    Text(
                                        text = article.getCategory(language),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = article.getTitle(language),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 2
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = article.date,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                    Text(
                                        text = "3 ${if (isVi) "phút đọc" else "min read"}",
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

    // Article detail dialog
    if (selectedArticle != null) {
        ArticleDetailDialog(
            article = selectedArticle!!,
            language = language,
            onDismiss = { selectedArticle = null }
        )
    }
}
