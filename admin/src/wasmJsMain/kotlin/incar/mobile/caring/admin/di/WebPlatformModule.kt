package incar.mobile.caring.admin.di

import incar.mobile.caring.admin.storage.AdminStorage
import incar.mobile.caring.admin.storage.WebAdminStorage
import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

fun webPlatformModule() = module {
    single<HttpClient> {
        HttpClient(Js) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; coerceInputValues = true })
            }
        }
    }
    single<AdminStorage> { WebAdminStorage() }
}
