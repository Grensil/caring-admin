package incar.mobile.caring.viewmodel

import androidx.lifecycle.ViewModel
import incar.mobile.caring.domain.model.UserType
import incar.mobile.caring.domain.usecase.GetUserTypeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// 일반유저/카포스 탭
enum class NormalTab { HOME, CAR, CARE, MORE }

// FA 탭
enum class FaTab { HOME, CUSTOMER, COMMUNICATION, MORE }

sealed class MainTab {
    data class Normal(val tab: NormalTab = NormalTab.HOME) : MainTab()
    data class Fa(val tab: FaTab = FaTab.HOME) : MainTab()
}

data class MainUiState(
    val userType: UserType,
    val tab: MainTab,
)

class MainViewModel(
    getUserType: GetUserTypeUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        getUserType().let { userType ->
            MainUiState(
                userType = userType,
                tab      = if (userType.isFa) MainTab.Fa() else MainTab.Normal(),
            )
        }
    )

    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun selectNormalTab(tab: NormalTab) {
        _uiState.value = _uiState.value.copy(tab = MainTab.Normal(tab))
    }

    fun selectFaTab(tab: FaTab) {
        _uiState.value = _uiState.value.copy(tab = MainTab.Fa(tab))
    }
}
