package incar.mobile.caring.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import incar.mobile.caring.domain.model.FAInfo
import incar.mobile.caring.domain.model.SignupDoneData
import incar.mobile.caring.domain.usecase.SignupUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SignupUiState {
    data object Loading : SignupUiState()
    data class ShowFASearch(val query: String = "", val results: List<FAInfo> = emptyList()) : SignupUiState()
    data class ShowSignupDone(val signupDoneData: SignupDoneData) : SignupUiState()
    data class Error(val message: String) : SignupUiState()
}

class SignupViewModel(
    private val signupUseCase: SignupUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<SignupUiState>(SignupUiState.Loading)
    val uiState: StateFlow<SignupUiState> = _uiState.asStateFlow()

    /** FA 선택 없이 바로 회원가입 완료로 진행 */
    fun loadSignupDone() {
        viewModelScope.launch {
            _uiState.value = SignupUiState.Loading
            runCatching { signupUseCase.getSignupDone() }
                .onSuccess { _uiState.value = SignupUiState.ShowSignupDone(it) }
                .onFailure { _uiState.value = SignupUiState.Error(it.message ?: "오류가 발생했습니다") }
        }
    }

    /** FA 검색 UI 시작 */
    fun startFASearch() {
        _uiState.value = SignupUiState.ShowFASearch()
    }

    /** FA 검색 */
    fun searchFA(query: String) {
        viewModelScope.launch {
            _uiState.value = SignupUiState.ShowFASearch(query = query, results = emptyList())
            runCatching { signupUseCase.searchFA(query) }
                .onSuccess { _uiState.value = SignupUiState.ShowFASearch(query = query, results = it) }
                .onFailure { _uiState.value = SignupUiState.Error(it.message ?: "검색 중 오류가 발생했습니다") }
        }
    }

    /** FA 선택 후 updateFA → 완료 화면 */
    fun selectFA(faInfo: FAInfo) {
        viewModelScope.launch {
            _uiState.value = SignupUiState.Loading
            runCatching { signupUseCase.updateFA(faInfo.name, faInfo.phone, faInfo.office) }
                .onSuccess { loadSignupDone() }
                .onFailure { _uiState.value = SignupUiState.Error(it.message ?: "FA 선택 중 오류가 발생했습니다") }
        }
    }

    /** FA 건너뛰기 */
    fun skipFA() {
        loadSignupDone()
    }
}
