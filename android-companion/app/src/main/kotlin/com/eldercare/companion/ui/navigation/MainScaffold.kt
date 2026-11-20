package com.eldercare.companion.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eldercare.companion.data.SessionManager
import com.eldercare.companion.data.repository.CareRepository
import com.eldercare.companion.localization.AppLanguage
import com.eldercare.companion.localization.AppStrings
import com.eldercare.companion.ui.account.AccountScreen
import com.eldercare.companion.ui.activities.ActivitiesScreen
import com.eldercare.companion.ui.components.AiChatbotDialog
import com.eldercare.companion.ui.home.HomeScreen
import com.eldercare.companion.ui.home.ServiceAction
import com.eldercare.companion.ui.notifications.NotificationsScreen
import com.eldercare.companion.ui.services.*
import com.eldercare.companion.ui.tasks.ShiftTasksScreen
import com.eldercare.companion.ui.tasks.ShiftTasksViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    repository: CareRepository,
    sessionManager: SessionManager,
    shiftTasksViewModel: ShiftTasksViewModel,
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // Default to Home tab
    var activeServiceAction by remember { mutableStateOf<ServiceAction?>(null) }
    val tasksState by shiftTasksViewModel.uiState.collectAsState()
    var showGlobalAiChat by remember { mutableStateOf(false) }

    // If an individual clinical service is opened, show its dedicated screen
    if (activeServiceAction != null) {
        when (activeServiceAction!!) {
            ServiceAction.RESIDENTS -> ResidentsListScreen(
                repository = repository,
                language = language,
                onBackClick = { activeServiceAction = null }
            )
            ServiceAction.CARE_PLANS -> CarePlansListScreen(
                repository = repository,
                language = language,
                onBackClick = { activeServiceAction = null }
            )
            ServiceAction.MEDICATIONS -> MedicationsListScreen(
                repository = repository,
                language = language,
                onBackClick = { activeServiceAction = null }
            )
            ServiceAction.FALL_RISK -> FallRiskListScreen(
                repository = repository,
                language = language,
                onBackClick = { activeServiceAction = null }
            )
            ServiceAction.INCIDENTS -> IncidentsListScreen(
                repository = repository,
                language = language,
                onBackClick = { activeServiceAction = null }
            )
            ServiceAction.SHIFT_HANDOVER -> ShiftHandoverScreen(
                repository = repository,
                language = language,
                onBackClick = { activeServiceAction = null }
            )
            ServiceAction.VITALS -> VitalsMonitorScreen(
                repository = repository,
                language = language,
                onBackClick = { activeServiceAction = null }
            )
            ServiceAction.AI_STAFF -> {
                // Handled via dialog
                activeServiceAction = null
                showGlobalAiChat = true
            }
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "+",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = AppStrings.appName(language),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    // Quick Language Switcher in TopBar
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clip(CircleShape)
                            .clickable {
                                val nextLang = if (language == AppLanguage.VIETNAMESE) AppLanguage.ENGLISH else AppLanguage.VIETNAMESE
                                sessionManager.appLanguage = nextLang.code
                                onLanguageChange(nextLang)
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = language.flag,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (language == AppLanguage.VIETNAMESE) "VI" else "EN",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.shadow(8.dp)
            ) {
                // Tab 0: Home (Minimalist Outlined)
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Home,
                            contentDescription = null,
                            tint = if (selectedTab == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                    },
                    label = {
                        Text(
                            AppStrings.tabHome(language),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )

                // Tab 1: Activities (Minimalist Outlined)
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.EventNote,
                            contentDescription = null,
                            tint = if (selectedTab == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                    },
                    label = {
                        Text(
                            AppStrings.tabActivities(language),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )

                // Tab 2: Tasks (PRIMARY CENTER ACTION)
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        Surface(
                            shape = CircleShape,
                            color = if (selectedTab == 2) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.AssignmentTurnedIn,
                                    contentDescription = null,
                                    tint = if (selectedTab == 2) Color.White else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    },
                    label = {
                        Text(
                            AppStrings.tabTasks(language),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 2) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )

                // Tab 3: Alerts / Notifications (Minimalist Outlined)
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = {
                        BadgedBox(
                            badge = {
                                Badge(containerColor = Color(0xFFE11D48)) { Text("5") }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = null,
                                tint = if (selectedTab == 3) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            )
                        }
                    },
                    label = {
                        Text(
                            AppStrings.tabNotifications(language),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )

                // Tab 4: Account (Minimalist Outlined)
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null,
                            tint = if (selectedTab == 4) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                    },
                    label = {
                        Text(
                            AppStrings.tabAccount(language),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (selectedTab == 4) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> HomeScreen(
                    cnaName = sessionManager.cnaName ?: "Daniel Brooks",
                    completedTasksCount = tasksState.completedCount,
                    totalTasksCount = tasksState.totalCount,
                    language = language,
                    onNavigateToTasks = { selectedTab = 2 },
                    onOpenService = { action ->
                        if (action == ServiceAction.AI_STAFF) {
                            showGlobalAiChat = true
                        } else {
                            activeServiceAction = action
                        }
                    }
                )
                1 -> ActivitiesScreen(
                    repository = repository,
                    language = language
                )
                2 -> ShiftTasksScreen(
                    viewModel = shiftTasksViewModel,
                    language = language,
                    onLogoutClick = onLogoutClick
                )
                3 -> NotificationsScreen(
                    sessionManager = sessionManager,
                    language = language
                )
                4 -> AccountScreen(
                    repository = repository,
                    sessionManager = sessionManager,
                    language = language,
                    onLanguageChange = onLanguageChange,
                    onLogoutClick = onLogoutClick,
                    onNavigateTab = { selectedTab = it }
                )
            }

            if (showGlobalAiChat) {
                AiChatbotDialog(
                    repository = repository,
                    language = language,
                    onDismiss = { showGlobalAiChat = false }
                )
            }
        }
    }
}
