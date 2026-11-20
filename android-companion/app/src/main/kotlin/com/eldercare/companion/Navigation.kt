package com.eldercare.companion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.eldercare.companion.data.SessionManager
import com.eldercare.companion.data.repository.CareRepository
import com.eldercare.companion.localization.AppLanguage
import com.eldercare.companion.ui.auth.LoginScreen
import com.eldercare.companion.ui.auth.LoginViewModel
import com.eldercare.companion.ui.navigation.MainScaffold
import com.eldercare.companion.ui.tasks.ShiftTasksViewModel

@Composable
fun MainNavigation() {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val repository = remember { CareRepository(sessionManager) }

    var currentLanguage by remember {
        mutableStateOf(
            if (sessionManager.appLanguage == "en") AppLanguage.ENGLISH else AppLanguage.VIETNAMESE
        )
    }

    val initialRoute = if (sessionManager.isLoggedIn) ShiftTasksRoute else LoginRoute
    val backStack = rememberNavBackStack(initialRoute)

    NavDisplay(
        backStack = backStack,
        onBack = {
            if (backStack.size > 1) {
                backStack.removeLastOrNull()
            }
        },
        entryProvider = entryProvider {
            entry<LoginRoute> {
                val loginViewModel: LoginViewModel = viewModel {
                    LoginViewModel(repository, sessionManager)
                }
                androidx.compose.runtime.LaunchedEffect(Unit) {
                    loginViewModel.resetState()
                }
                LoginScreen(
                    viewModel = loginViewModel,
                    onLoginSuccess = {
                        backStack.clear()
                        backStack.add(ShiftTasksRoute)
                    }
                )
            }
            entry<ShiftTasksRoute> {
                val shiftTasksViewModel: ShiftTasksViewModel = viewModel {
                    ShiftTasksViewModel(repository, sessionManager)
                }
                MainScaffold(
                    repository = repository,
                    sessionManager = sessionManager,
                    shiftTasksViewModel = shiftTasksViewModel,
                    language = currentLanguage,
                    onLanguageChange = { newLang ->
                        currentLanguage = newLang
                    },
                    onLogoutClick = {
                        repository.logout()
                        backStack.clear()
                        backStack.add(LoginRoute)
                    }
                )
            }
        }
    )
}
