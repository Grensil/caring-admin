package incar.mobile.caring.admin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import incar.mobile.caring.admin.data.AdminApiService
import incar.mobile.caring.admin.model.Adjuster
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AdjusterListUiState {
    data object Loading : AdjusterListUiState()
    data class Success(val adjusters: List<Adjuster>) : AdjusterListUiState()
    data class Error(val message: String) : AdjusterListUiState()
}

class AdjusterListViewModel(
    private val adminApiService: AdminApiService,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AdjusterListUiState>(AdjusterListUiState.Loading)
    val uiState: StateFlow<AdjusterListUiState> = _uiState

    fun load() {
        viewModelScope.launch {
            _uiState.value = AdjusterListUiState.Loading
            runCatching { adminApiService.getAdjusters() }
                .onSuccess { dto ->
                    if (dto.result == "success") {
                        val adjusters = dto.adjusters.map { adj ->
                            Adjuster(
                                id = adj.id,
                                name = adj.name,
                                company = adj.company,
                                phone = adj.phone,
                                address = adj.address,
                                careerYears = adj.careerYears,
                                reviewScore = adj.reviewScore,
                                reviewCount = adj.reviewCount,
                                isVisible = adj.isVisible,
                            )
                        }
                        _uiState.value = AdjusterListUiState.Success(adjusters)
                    } else {
                        _uiState.value = AdjusterListUiState.Error(dto.errorMessage ?: "조회 실패")
                    }
                }
                .onFailure { e ->
                    _uiState.value = AdjusterListUiState.Error(e.message ?: "네트워크 오류")
                }
        }
    }

    fun refresh() = load()
}
