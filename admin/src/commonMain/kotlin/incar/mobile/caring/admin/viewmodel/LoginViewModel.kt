package incar.mobile.caring.admin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import incar.mobile.caring.admin.data.AdminApiService
import incar.mobile.caring.admin.storage.AdminStorage
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
    private val adminStorage: AdminStorage,
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    // ID만 저장 (편의용, 보안 위험 없음)
    val savedId: String = adminStorage.read("admin_id") ?: ""

    // 저장된 토큰이 있으면 자동 로그인 활성화 상태로 간주
    val autoLoginEnabled: Boolean = adminStorage.read("admin_token") != null

    init {
        // 저장된 토큰이 있으면 바로 성공 상태로 전환 (토큰 유효성은 서버가 검증)
        val savedToken = adminStorage.read("admin_token")
        if (savedToken != null) {
            _uiState.value = LoginUiState.Success(savedToken)
        }
    }

    fun login(id: String, pw: String, saveLogin: Boolean = false) {
        if (id.isBlank() || pw.isBlank()) {
            _uiState.value = LoginUiState.Error("아이디와 비밀번호를 입력해주세요.")
            return
        }
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            runCatching { adminApiService.login(id, pw) }
                .onSuccess { dto ->
                    if (dto.result == "success" && dto.token != null) {
                        if (saveLogin) {
                            // 비밀번호는 절대 저장하지 않음 — 토큰만 저장
                            adminStorage.save("admin_id", id)
                            adminStorage.save("admin_token", dto.token)
                        } else {
                            // 자동 로그인 해제 시 저장된 정보 삭제
                            adminStorage.delete("admin_id")
                            adminStorage.delete("admin_token")
                        }
                        _uiState.value = LoginUiState.Success(dto.token)
                    } else {
                        _uiState.value = LoginUiState.Error(dto.errorMessage ?: "로그인 실패")
                    }
                }
                .onFailure {
                    _uiState.value = LoginUiState.Error("아이디 또는 비밀번호가 올바르지 않습니다.")
                }
        }
    }

    fun logout() {
        adminStorage.delete("admin_token")
        adminStorage.delete("admin_id")
        _uiState.value = LoginUiState.Idle
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}
