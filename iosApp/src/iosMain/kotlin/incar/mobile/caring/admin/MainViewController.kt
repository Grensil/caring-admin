package incar.mobile.caring.admin

import androidx.compose.ui.window.ComposeUIViewController
import incar.mobile.caring.admin.di.adminCommonModule
import incar.mobile.caring.admin.di.iosPlatformModule
import org.koin.core.context.startKoin
import platform.UIKit.UIViewController

private var koinStarted = false

fun MainViewController(): UIViewController {
    if (!koinStarted) {
        koinStarted = true
        startKoin {
            modules(
                adminCommonModule(BuildConfig.BASE_URL),
                iosPlatformModule(),
            )
        }
    }
    return ComposeUIViewController {
        AdminApp()
    }
}
