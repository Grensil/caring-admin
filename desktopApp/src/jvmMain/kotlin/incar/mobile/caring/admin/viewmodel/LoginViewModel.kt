package incar.mobile.caring.admin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import incar.mobile.caring.admin.data.AdminApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoginUiState {
    data object Idle : LoginUiState()
    data object Loading : LoginUiState()
    data class Success(val token: String) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModel(
    private val adminApiService: AdminApiService,
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    fun login(id: String, pw: String) {
        if (id.isBlank() || pw.isBlank()) {
            _uiState.value = LoginUiState.Error("아이디와 비밀번호를 입력해주세요.")
            return
        }
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            runCatching { adminApiService.login(id, pw) }
                .onSuccess { dto ->
                    if (dto.result == "ok" && dto.token != null) {
                        _uiState.value = LoginUiState.Success(dto.token)
                    } else {
                        _uiState.value = LoginUiState.Error(dto.errorMessage ?: "로그인 실패")
                    }
                }
                .onFailure { e ->
                    _uiState.value = LoginUiState.Error(e.message ?: "네트워크 오류")
                }
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}
