package incar.mobile.caring.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import incar.mobile.caring.domain.model.HomeData
import incar.mobile.caring.domain.model.UserType
import incar.mobile.caring.domain.usecase.GetHomeDataUseCase
import incar.mobile.caring.domain.usecase.GetUserTypeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Success(
        val homeData: HomeData,
        val userType: UserType,
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

class HomeViewModel(
    private val getHomeData: GetHomeDataUseCase,
    private val getUserType: GetUserTypeUseCase,
) : ViewModel() {

    /** 한번에 관찰되어서 로직상 깔끔하지만 여러 데이터를 비동기적으로 처리할 때에는 불편 **/
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    /** 나누어서 관찰되어서 로직을 여러 개를 관리 해야하지만 여러 데이터를 비동기적으로 처리할 때에는 편함 **/
    private val _homeDataState = MutableStateFlow<UiState<HomeData?>>(UiState.Loading)
    val homeDataState: StateFlow<UiState<HomeData?>> = _homeDataState.asStateFlow()

    private val _userTypeState = MutableStateFlow<UiState<UserType?>>(UiState.Loading)
    val userTypeState: StateFlow<UiState<UserType?>> = _userTypeState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            runCatching {
                HomeUiState.Success(
                    homeData = getHomeData(),
                    userType = getUserType(),
                )
            }
                .onSuccess {
                    _uiState.value = it
                    _homeDataState.value = UiState.Success(it.homeData)
                    _userTypeState.value = UiState.Success(it.userType)
                }
                .onFailure { _uiState.value = HomeUiState.Error(it.message ?: "홈 데이터 로드 실패") }
        }
    }
}
