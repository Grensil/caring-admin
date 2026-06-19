package incar.mobile.caring.admin.di

import android.content.Context
import incar.mobile.caring.admin.storage.AdminStorage
import incar.mobile.caring.admin.storage.AndroidAdminStorage
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

fun androidPlatformModule() = module {
    single<HttpClient> {
        HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; coerceInputValues = true })
            }
        }
    }
    single<AdminStorage> { AndroidAdminStorage(androidContext()) }
}
