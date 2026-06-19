package incar.mobile.caring.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import incar.mobile.caring.domain.model.AuthCheckResult
import incar.mobile.caring.domain.usecase.CertifyFromImpUidUseCase
import incar.mobile.caring.domain.usecase.CertifyUseCase
import incar.mobile.caring.domain.usecase.CheckAuthUseCase
import incar.mobile.caring.domain.usecase.SendAgreementUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class AuthUiState {
    data object Loading : AuthUiState()
    data object ShowStartButton : AuthUiState()
    data class ShowAgreement(val type: String) : AuthUiState()
    data object NavigateToMain : AuthUiState()
    data object NavigateToAddCar : AuthUiState()
    data object NavigateToInactiveAccount : AuthUiState()
    data class NeedUpdate(val version: String) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(
    private val checkAuthUseCase: CheckAuthUseCase,
    private val certifyUseCase: CertifyUseCase,
    private val certifyFromImpUidUseCase: CertifyFromImpUidUseCase,
    private val sendAgreementUseCase: SendAgreementUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.ShowStartButton)

    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun resetToStart() {
        _uiState.value = AuthUiState.ShowStartButton
    }

    fun startWithAgreement(type: String) {
        _uiState.value = AuthUiState.ShowAgreement(type)
    }

    fun onAgreementConfirmed(type: String) {
        if (!checkAuthUseCase.hasSession()) {
            // 신규 유저: 약관 확인 후 본인인증 화면으로
            _uiState.value = AuthUiState.ShowStartButton
            return
        }
        // 기존 유저: 서버 전송 후 재인증
        viewModelScope.launch {
            sendAgreementUseCase(type)
                .onSuccess { startAuthCheck() }
                .onFailure { _uiState.value = AuthUiState.Error("약관 동의 처리에 실패했습니다.") }
        }
    }

    /** 비활성 계정 활성화 */
    fun onInactiveAccountActivate() {
        viewModelScope.launch {
            sendAgreementUseCase.activateInactiveAccount()
                .onSuccess { startAuthCheck() }
                .onFailure { _uiState.value = AuthUiState.Error("계정 활성화에 실패했습니다.") }
        }
    }

    /**
     * Iamport 본인인증 완료 콜백 — imp_uid만 받아서 내부에서 전체 처리
     * PassCertActivity → AuthScreen → 이 메서드 순서로 호출
     */
    fun onIamportCertResult(impUid: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            runCatching { certifyFromImpUidUseCase(impUid) }
                .onSuccess { startAuthCheck() }
                .onFailure { e ->
                    _uiState.value = AuthUiState.Error("본인인증 처리에 실패했습니다.\n${e.message}")
                }
        }
    }

    /**
     * 본인인증 완료 콜백 (encodedData는 앱에서 직접 인코딩한 값)
     */
    fun onCertificationSuccess(encodedData: String, certType: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            runCatching { certifyUseCase.sendCertData(encodedData, certType) }
                .onSuccess { _uiState.value = AuthUiState.NavigateToMain }
                .onFailure { _uiState.value = AuthUiState.Error("본인인증 처리에 실패했습니다.") }
        }
    }

    private fun startAuthCheck() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            runCatching { checkAuthUseCase.checkAuth() }
                .onSuccess { result ->
                    _uiState.value = when (result) {
                        is AuthCheckResult.NoSession              -> AuthUiState.ShowStartButton
                        is AuthCheckResult.NavigateToMain         -> AuthUiState.NavigateToMain
                        is AuthCheckResult.NavigateToAddCar       -> AuthUiState.NavigateToAddCar
                        is AuthCheckResult.NavigateToInactiveAccount -> AuthUiState.NavigateToInactiveAccount
                        is AuthCheckResult.NavigateToAgreement    -> AuthUiState.ShowAgreement(result.type)
                        is AuthCheckResult.ClearAndReAuth         -> AuthUiState.ShowStartButton
                        is AuthCheckResult.NeedUpdate             -> AuthUiState.NeedUpdate(result.latestVersion)
                    }
                }
                .onFailure {
                    runCatching { checkAuthUseCase.clearSession() }
                    _uiState.value = AuthUiState.ShowStartButton
                }
        }
    }
}
