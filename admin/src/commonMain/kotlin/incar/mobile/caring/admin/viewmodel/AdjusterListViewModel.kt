package incar.mobile.caring.admin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import incar.mobile.caring.admin.data.AdminApiService
import incar.mobile.caring.admin.data.dto.AdjusterDto
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

    private val _updateError = MutableStateFlow<String?>(null)
    val updateError: StateFlow<String?> = _updateError

    fun load(token: String) {
        viewModelScope.launch {
            _uiState.value = AdjusterListUiState.Loading
            runCatching { adminApiService.getAdjusters(token) }
                .onSuccess { dto ->
                    if (dto.result == "success") {
                        _uiState.value = AdjusterListUiState.Success(dto.adjusters.map { it.toDomain() })
                    } else {
                        _uiState.value = AdjusterListUiState.Error(dto.errorMessage ?: "조회 실패")
                    }
                }
                .onFailure { e ->
                    _uiState.value = AdjusterListUiState.Error(e.message ?: "네트워크 오류")
                }
        }
    }

    fun update(
        token: String,
        id: Int,
        fields: Map<String, Any?>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch {
            runCatching { adminApiService.updateAdjuster(token, id, fields) }
                .onSuccess { dto ->
                    if (dto.result == "success" && dto.adjuster != null) {
                        val current = (_uiState.value as? AdjusterListUiState.Success)?.adjusters ?: emptyList()
                        _uiState.value = AdjusterListUiState.Success(
                            current.map { if (it.id == id) dto.adjuster.toDomain() else it }
                        )
                        onSuccess()
                    } else {
                        onError(dto.errorMessage ?: "수정 실패")
                    }
                }
                .onFailure { e -> onError(e.message ?: "네트워크 오류") }
        }
    }

    fun refresh(token: String) = load(token)
}

private fun AdjusterDto.toDomain() = Adjuster(
    id = id,
    name = name,
    company = company,
    phone = phone,
    officePhone = officePhone,
    fax = fax,
    address = address,
    addressDetail = addressDetail,
    careerYears = careerYears,
    regions = regions ?: emptyList(),
    fields = fields ?: emptyList(),
    consultingFields = consultingFields,
    email = email,
    profileImage = profileImage,
    qualifications = qualifications ?: emptyList(),
    mainCareer = mainCareer,
    isVisible = isVisible,
    reviewScore = reviewScore,
    reviewCount = reviewCount,
    consultingReviewScore = consultingReviewScore,
    consultingReviewCount = consultingReviewCount,
    lat = lat,
    lng = lng,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
