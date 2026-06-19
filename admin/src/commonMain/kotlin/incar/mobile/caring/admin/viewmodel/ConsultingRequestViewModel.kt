package incar.mobile.caring.admin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import incar.mobile.caring.admin.data.AdminApiService
import incar.mobile.caring.admin.data.dto.ConsultingRequestDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ConsultingRequestUiState {
    data object Loading : ConsultingRequestUiState()
    data class Success(val items: List<ConsultingRequestDto>, val total: Int) : ConsultingRequestUiState()
    data class Error(val message: String) : ConsultingRequestUiState()
}

class ConsultingRequestViewModel(
    private val adminApiService: AdminApiService,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ConsultingRequestUiState>(ConsultingRequestUiState.Loading)
    val uiState: StateFlow<ConsultingRequestUiState> = _uiState

    fun load(token: String) {
        viewModelScope.launch {
            _uiState.value = ConsultingRequestUiState.Loading
            runCatching { adminApiService.getConsultingRequests(token) }
                .onSuccess { dto ->
                    if (dto.result == "success") {
                        _uiState.value = ConsultingRequestUiState.Success(
                            dto.consultingRequests,
                            dto.pagination?.total ?: dto.consultingRequests.size,
                        )
                    } else {
                        _uiState.value = ConsultingRequestUiState.Error(dto.errorMessage ?: "조회 실패")
                    }
                }
                .onFailure { e ->
                    _uiState.value = ConsultingRequestUiState.Error(e.message ?: "네트워크 오류")
                }
        }
    }

    fun refresh(token: String) = load(token)
}
