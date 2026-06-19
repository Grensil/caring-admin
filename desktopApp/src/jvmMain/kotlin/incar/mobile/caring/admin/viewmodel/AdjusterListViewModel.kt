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

    fun load(token: String) {
        viewModelScope.launch {
            _uiState.value = AdjusterListUiState.Loading
            runCatching { adminApiService.getAdjusters(token) }
                .onSuccess { dto ->
                    if (dto.result == "ok") {
                        val adjusters = dto.adjusterList.map { adj ->
                            Adjuster(
                                userIdx = adj.userIdx,
                                name = adj.userName,
                                phone = adj.userPhone,
                                isLive = adj.isLive,
                                joinedAt = adj.joinedAt,
                                officeName = adj.officeName,
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

    fun refresh(token: String) = load(token)
}
