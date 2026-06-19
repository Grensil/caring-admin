package incar.mobile.caring.admin

import androidx.compose.runtime.*
import incar.mobile.caring.admin.screen.AdjusterListScreen
import incar.mobile.caring.admin.screen.LoginScreen

private sealed class AdminScreen {
    data object Login : AdminScreen()
    data class AdjusterList(val token: String) : AdminScreen()
}

@Composable
fun AdminApp() {
    var screen by remember { mutableStateOf<AdminScreen>(AdminScreen.Login) }

    when (val current = screen) {
        is AdminScreen.Login -> LoginScreen(
            onLoginSuccess = { token -> screen = AdminScreen.AdjusterList(token) },
        )
        is AdminScreen.AdjusterList -> AdjusterListScreen(
            token = current.token,
            onLogout = { screen = AdminScreen.Login },
        )
    }
}
