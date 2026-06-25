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
    // 회원 관리
    MEMBER_LIST("회원 목록"),
    MEMBER_DETAIL("회원 상세"),
    MEMBER_SANCTION("제재 관리"),

    // FA 관리
    FA_LIST("FA 목록"),
    FA_APPROVAL("가입 승인"),
    FA_FEED("피드 게시글 관리"),

    // FA(S급) 관리
    FA_S_LIST("S급 FA 목록"),
    FA_S_APPROVAL("S급 승인"),
    FA_S_ACTIVITY("활동 내역"),

    // 손해사정사 관리
    ADJUSTER_LIST("손해사정사 리스트"),
    CONSULTING_REQUESTS("보험금 상담 요청 내역"),
    EDUCATION_REQUESTS("교육 요청 내역"),

    // 중고차 매매 관리
    USED_CAR_LOTS("매물 관리"),
    USED_CAR_HISTORY("거래 이력"),
    USED_CAR_DEALERS("제휴 매매상 관리"),

    // 카포스 관리
    CAPOS_LIST("점주 목록"),
    CAPOS_REPAIR_BIDS("수리 견적 요청 내역"),
    CAPOS_SETTLEMENT("정산 내역"),

    // 사고 대응 관리
    ACCIDENT_REQUESTS("사고 신청 내역"),
    ACCIDENT_STATUS("처리 현황"),

    // 앱 설정
    APP_FORCE_UPDATE("강제업데이트 설정"),
    APP_SERVICE_MENU("주요서비스 관리"),
    APP_BANNER("배너/공지 관리"),

    // 알림 관리
    NOTIFICATION_PUSH("푸시알림 발송"),
    NOTIFICATION_KAKAO("알림톡 발송"),
}

enum class AdminMenu(val label: String, val children: List<AdminSubMenu> = emptyList()) {
    MEMBER("회원 관리", listOf(
        AdminSubMenu.MEMBER_LIST,
        AdminSubMenu.MEMBER_DETAIL,
        AdminSubMenu.MEMBER_SANCTION,
    )),
    FA("FA 관리", listOf(
        AdminSubMenu.FA_LIST,
        AdminSubMenu.FA_APPROVAL,
        AdminSubMenu.FA_FEED,
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
    USED_CAR("중고차 매매 관리", listOf(
        AdminSubMenu.USED_CAR_LOTS,
        AdminSubMenu.USED_CAR_HISTORY,
        AdminSubMenu.USED_CAR_DEALERS,
    )),
    CAPOS("카포스 관리", listOf(
        AdminSubMenu.CAPOS_LIST,
        AdminSubMenu.CAPOS_REPAIR_BIDS,
        AdminSubMenu.CAPOS_SETTLEMENT,
    )),
    ACCIDENT("사고 대응 관리", listOf(
        AdminSubMenu.ACCIDENT_REQUESTS,
        AdminSubMenu.ACCIDENT_STATUS,
    )),
    APP_SETTINGS("앱 설정", listOf(
        AdminSubMenu.APP_FORCE_UPDATE,
        AdminSubMenu.APP_SERVICE_MENU,
        AdminSubMenu.APP_BANNER,
    )),
    NOTIFICATION("알림 관리", listOf(
        AdminSubMenu.NOTIFICATION_PUSH,
        AdminSubMenu.NOTIFICATION_KAKAO,
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
