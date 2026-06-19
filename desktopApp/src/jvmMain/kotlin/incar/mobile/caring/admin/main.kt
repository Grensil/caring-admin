package incar.mobile.caring.admin

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import incar.mobile.caring.di.initKoin

fun main() = application {
    initKoin(
        baseUrl = BuildConfig.BASE_URL,
        appVersion = "1.0",
        iamportImpKey = BuildConfig.IAMPORT_IMP_KEY,
        iamportImpSecret = BuildConfig.IAMPORT_IMP_SECRET,
        kicaaBaseUrl = BuildConfig.KICAA_BASE_URL,
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
