package incar.mobile.caring.admin

import androidx.compose.runtime.*
import incar.mobile.caring.admin.screen.*
import incar.mobile.caring.admin.theme.AdminTheme
import incar.mobile.caring.admin.viewmodel.LoginUiState
import incar.mobile.caring.admin.viewmodel.LoginViewModel
import org.koin.compose.viewmodel.koinViewModel

sealed class AdminScreen {
    data object Login : AdminScreen()
    data class Main(val token: String) : AdminScreen()
}

enum class AdminSubMenu(val label: String) {
    // 일반 회원관리
    MEMBER_LIST("회원 목록"),
    MEMBER_DETAIL("회원 상세"),
    MEMBER_SANCTION("제재 관리"),

    // FA(일반) 관리
    FA_LIST("FA 목록"),
    FA_APPROVAL("가입 승인"),
    FA_ACTIVITY("활동 내역"),

    // FA(S급) 관리
    FA_S_LIST("S급 FA 목록"),
    FA_S_APPROVAL("S급 승인"),
    FA_S_ACTIVITY("활동 내역"),

    // 손해사정사 관리
    ADJUSTER_LIST("손해사정사 리스트"),
    CONSULTING_REQUESTS("보험금 상담 요청 내역"),
    EDUCATION_REQUESTS("교육 요청 내역"),

    // 카포스 점주 관리
    CAPOS_LIST("점주 목록"),
    CAPOS_SHOP("가맹점 관리"),
    CAPOS_SETTLEMENT("정산 내역"),
}

enum class AdminMenu(val label: String, val children: List<AdminSubMenu> = emptyList()) {
    MEMBER("일반 회원관리", listOf(
        AdminSubMenu.MEMBER_LIST,
        AdminSubMenu.MEMBER_DETAIL,
        AdminSubMenu.MEMBER_SANCTION,
    )),
    FA("FA(일반) 관리", listOf(
        AdminSubMenu.FA_LIST,
        AdminSubMenu.FA_APPROVAL,
        AdminSubMenu.FA_ACTIVITY,
    )),
    FA_S("FA(S급) 관리", listOf(
        AdminSubMenu.FA_S_LIST,
        AdminSubMenu.FA_S_APPROVAL,
        AdminSubMenu.FA_S_ACTIVITY,
    )),
    ADJUSTER("손해사정사 관리", listOf(
        AdminSubMenu.ADJUSTER_LIST,
        AdminSubMenu.CONSULTING_REQUESTS,
        AdminSubMenu.EDUCATION_REQUESTS,
    )),
    CAPOS("카포스 점주 관리", listOf(
        AdminSubMenu.CAPOS_LIST,
        AdminSubMenu.CAPOS_SHOP,
        AdminSubMenu.CAPOS_SETTLEMENT,
    )),
}

@Composable
fun AdminApp() {
    AdminTheme {
        val loginViewModel: LoginViewModel = koinViewModel()
        val loginUiState by loginViewModel.uiState.collectAsState()

        when (val s = loginUiState) {
            is LoginUiState.Idle, is LoginUiState.Error, is LoginUiState.Loading -> {
                LoginScreen(viewModel = loginViewModel)
            }
            is LoginUiState.Success -> {
                MainScreen(
                    token = s.token,
                    onLogout = { loginViewModel.logout() },
                )
            }
        }
    }
}
