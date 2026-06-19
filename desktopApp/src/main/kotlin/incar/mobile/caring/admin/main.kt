package incar.mobile.caring.admin

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import incar.mobile.caring.di.initKoin

fun main() = application {
    initKoin(
        baseUrl = "https://caring-web-flatform.onrender.com",
        appVersion = "1.0",
        iamportImpKey = "",
        iamportImpSecret = "",
    )

    val state = rememberWindowState(size = DpSize(1200.dp, 800.dp))

    Window(
        onCloseRequest = ::exitApplication,
        title = "Caring Admin",
        state = state,
    ) {
        AdminApp()
    }
}
