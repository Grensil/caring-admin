package incar.mobile.caring.viewmodel

import androidx.lifecycle.ViewModel
import incar.mobile.caring.domain.usecase.GetUserInfoUseCase
import incar.mobile.caring.domain.usecase.LogoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class MoreUiState(
    val userName: String = "",
    val userPhone: String = "",
    val isLoggedOut: Boolean = false,
)

class MoreViewModel(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        MoreUiState(
            userName = getUserInfoUseCase.getUserName(),
            userPhone = getUserInfoUseCase.getUserPhone(),
        ),
    )

    val uiState: StateFlow<MoreUiState> = _uiState.asStateFlow()

    fun logout() {
        logoutUseCase()
        _uiState.value = _uiState.value.copy(isLoggedOut = true)
    }
}
