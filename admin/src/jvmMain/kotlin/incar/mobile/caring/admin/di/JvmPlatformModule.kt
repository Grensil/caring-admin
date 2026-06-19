package incar.mobile.caring.admin.di

import incar.mobile.caring.admin.storage.AdminStorage
import incar.mobile.caring.admin.storage.JvmAdminStorage
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

fun jvmPlatformModule() = module {
    single<HttpClient> {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; coerceInputValues = true })
            }
        }
    }
    single<AdminStorage> { JvmAdminStorage() }
}
