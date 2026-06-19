package incar.mobile.caring.admin

import androidx.compose.runtime.*
import incar.mobile.caring.admin.screen.*
import incar.mobile.caring.admin.theme.AdminTheme

sealed class AdminScreen {
    data object Login : AdminScreen()
    data class Main(val token: String) : AdminScreen()
}

enum class AdminSubMenu(val label: String) {
    ADJUSTER_LIST("손해사정사 리스트"),
    CONSULTING_REQUESTS("보험금 상담 요청 내역"),
    EDUCATION_REQUESTS("교육 요청 내역"),
}

enum class AdminMenu(val label: String, val children: List<AdminSubMenu> = emptyList()) {
    USER_TYPE("유저 타입 전환"),
    ADJUSTER("손해사정사 관리", listOf(
        AdminSubMenu.ADJUSTER_LIST,
        AdminSubMenu.CONSULTING_REQUESTS,
        AdminSubMenu.EDUCATION_REQUESTS,
    )),
    PUSH("푸시 알림"),
    FORCE_UPDATE("강제 업데이트"),
}

@Composable
fun AdminApp() {
    AdminTheme {
        var screen by remember { mutableStateOf<AdminScreen>(AdminScreen.Login) }

        when (val s = screen) {
            is AdminScreen.Login -> LoginScreen(
                onLoginSuccess = { token -> screen = AdminScreen.Main(token) },
            )
            is AdminScreen.Main -> MainScreen(
                token = s.token,
                onLogout = { screen = AdminScreen.Login },
            )
        }
    }
}
