package incar.mobile.caring.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import incar.mobile.caring.domain.model.UserType
import incar.mobile.caring.domain.repository.UserSessionRepository
import incar.mobile.caring.domain.usecase.SignupUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AddCarUiState {
    data object Loading : AddCarUiState()
    data object NavigateToFASearch : AddCarUiState()
    data object NavigateToSignupDone : AddCarUiState()
    data class Error(val message: String) : AddCarUiState()
}

class AddCarViewModel(
    private val signupUseCase: SignupUseCase,
    private val userSessionRepository: UserSessionRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddCarUiState>(AddCarUiState.Loading)
    val uiState: StateFlow<AddCarUiState> = _uiState.asStateFlow()

    fun addFakeCarAndContinue() {
        viewModelScope.launch {
            _uiState.value = AddCarUiState.Loading
            runCatching { signupUseCase.addFakeCar() }
                .onSuccess {
                    val userType = userSessionRepository.getUserType()
                    _uiState.value = if (userType == UserType.CARPOS) {
                        AddCarUiState.NavigateToSignupDone
                    } else {
                        AddCarUiState.NavigateToFASearch
                    }
                }
                .onFailure { _uiState.value = AddCarUiState.Error(it.message ?: "차량 등록 중 오류가 발생했습니다") }
        }
    }
}
