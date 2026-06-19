package incar.mobile.caring.admin

import androidx.compose.runtime.*
import incar.mobile.caring.admin.screen.AdjusterListScreen
import incar.mobile.caring.admin.screen.LoginScreen

private sealed class AdminScreen {
    data object Login : AdminScreen()
    data object AdjusterList : AdminScreen()
}

@Composable
fun AdminApp() {
    var screen by remember { mutableStateOf<AdminScreen>(AdminScreen.Login) }

    when (screen) {
        is AdminScreen.Login -> LoginScreen(
            onLoginSuccess = { screen = AdminScreen.AdjusterList },
        )
        is AdminScreen.AdjusterList -> AdjusterListScreen(
            onLogout = { screen = AdminScreen.Login },
        )
    }
}
