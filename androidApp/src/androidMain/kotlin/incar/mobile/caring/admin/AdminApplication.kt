package incar.mobile.caring.admin

import android.app.Application
import incar.mobile.caring.admin.di.adminCommonModule
import incar.mobile.caring.admin.di.androidPlatformModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AdminApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AdminApplication)
            modules(
                adminCommonModule(BuildConfig.BASE_URL),
                androidPlatformModule(),
            )
        }
    }
}
