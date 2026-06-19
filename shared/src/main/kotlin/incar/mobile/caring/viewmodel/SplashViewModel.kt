package incar.mobile.caring.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import incar.mobile.caring.domain.model.AuthCheckResult
import incar.mobile.caring.domain.usecase.CheckAuthUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SplashUiState {
    data object Loading : SplashUiState()
    data object NavigateToEntry : SplashUiState()
    data object NavigateToMain : SplashUiState()
    data class NavigateToAgreement(val type: String) : SplashUiState()
    data object NavigateToAddCar : SplashUiState()
    data object NavigateToInactiveAccount : SplashUiState()
    data class NeedUpdate(val latestVersion: String) : SplashUiState()
    data class Error(val message: String) : SplashUiState()
}

class SplashViewModel(
    private val checkAuthUseCase: CheckAuthUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)

    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        checkAuth()
    }

    fun checkAuth() {
        viewModelScope.launch {
            _uiState.value = SplashUiState.Loading
            runCatching { checkAuthUseCase.checkAuth() }
                .onSuccess { result ->
                    _uiState.value = result.toUiState()
                }
                .onFailure { error ->
                    checkAuthUseCase.clearSession()
                    _uiState.value = SplashUiState.Error(error.message ?: "인증 확인 실패")
                }
        }
    }

    private fun AuthCheckResult.toUiState(): SplashUiState = when (this) {
        is AuthCheckResult.NoSession              -> SplashUiState.NavigateToEntry
        is AuthCheckResult.NavigateToMain         -> SplashUiState.NavigateToMain
        is AuthCheckResult.NavigateToAddCar       -> SplashUiState.NavigateToAddCar
        is AuthCheckResult.NavigateToInactiveAccount -> SplashUiState.NavigateToInactiveAccount
        is AuthCheckResult.NavigateToAgreement    -> SplashUiState.NavigateToAgreement(type)
        is AuthCheckResult.ClearAndReAuth         -> SplashUiState.NavigateToEntry
        is AuthCheckResult.NeedUpdate             -> SplashUiState.NeedUpdate(latestVersion)
    }
}
