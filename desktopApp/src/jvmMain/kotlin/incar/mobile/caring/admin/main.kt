package incar.mobile.caring.admin

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import incar.mobile.caring.admin.di.adminCommonModule
import incar.mobile.caring.admin.di.jvmPlatformModule
import org.koin.core.context.startKoin

fun main() = application {
    startKoin {
        modules(
            adminCommonModule(BuildConfig.BASE_URL),
            jvmPlatformModule(),
        )
    }
    val state = rememberWindowState(size = DpSize(1200.dp, 800.dp))
    Window(
        onCloseRequest = ::exitApplication,
        title = "Caring Admin",
        state = state,
    ) {
        AdminApp()
    }
}
