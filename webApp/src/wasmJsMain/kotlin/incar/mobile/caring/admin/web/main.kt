package incar.mobile.caring.admin.web

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import incar.mobile.caring.admin.AdminApp
import incar.mobile.caring.admin.BuildConfig
import incar.mobile.caring.admin.di.adminCommonModule
import incar.mobile.caring.admin.di.webPlatformModule
import kotlinx.browser.document
import org.koin.core.context.startKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    startKoin {
        modules(
            adminCommonModule(BuildConfig.BASE_URL),
            webPlatformModule(),
        )
    }
    ComposeViewport(document.body!!) {
        AdminApp()
    }
}
