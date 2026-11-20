package com.eldercare.companion.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eldercare.companion.data.SessionManager
import com.eldercare.companion.data.repository.CareRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val serverUrl: String = SessionManager.DEFAULT_BASE_URL,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

class LoginViewModel(
    private val repository: CareRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        LoginUiState(
            serverUrl = sessionManager.baseUrl
        )
    )
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onUsernameChange(value: String) {
        _uiState.update { it.copy(username = value, errorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
    }

    fun onServerUrlChange(value: String) {
        _uiState.update { it.copy(serverUrl = value, errorMessage = null) }
    }

    fun resetState() {
        _uiState.update {
            LoginUiState(
                username = "",
                password = "",
                serverUrl = sessionManager.baseUrl,
                isLoading = false,
                errorMessage = null,
                isSuccess = false
            )
        }
    }

    fun login() {
        val state = _uiState.value
        if (state.username.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Vui lòng nhập đầy đủ tài khoản và mật khẩu") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.login(state.username, state.password, state.serverUrl)
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage ?: "Đăng nhập thất bại. Vui lòng kiểm tra lại kết nối mạng hoặc thông tin tài khoản."
                        )
                    }
                }
            )
        }
    }
}
