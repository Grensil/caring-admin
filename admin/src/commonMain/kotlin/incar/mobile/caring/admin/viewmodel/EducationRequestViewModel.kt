package incar.mobile.caring.admin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import incar.mobile.caring.admin.data.AdminApiService
import incar.mobile.caring.admin.data.dto.EducationRequestDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class EducationRequestUiState {
    data object Loading : EducationRequestUiState()
    data class Success(val items: List<EducationRequestDto>, val total: Int) : EducationRequestUiState()
    data class Error(val message: String) : EducationRequestUiState()
}

class EducationRequestViewModel(
    private val adminApiService: AdminApiService,
) : ViewModel() {

    private val _uiState = MutableStateFlow<EducationRequestUiState>(EducationRequestUiState.Loading)
    val uiState: StateFlow<EducationRequestUiState> = _uiState

    fun load(token: String) {
        viewModelScope.launch {
            _uiState.value = EducationRequestUiState.Loading
            runCatching { adminApiService.getEducationRequests(token) }
                .onSuccess { dto ->
                    if (dto.result == "success") {
                        _uiState.value = EducationRequestUiState.Success(
                            dto.educationRequests,
                            dto.pagination?.total ?: dto.educationRequests.size,
                        )
                    } else {
                        _uiState.value = EducationRequestUiState.Error(dto.errorMessage ?: "조회 실패")
                    }
                }
                .onFailure { e ->
                    _uiState.value = EducationRequestUiState.Error(e.message ?: "네트워크 오류")
                }
        }
    }

    fun refresh(token: String) = load(token)
}
