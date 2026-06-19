package incar.mobile.caring.data.di

import incar.mobile.caring.data.remote.AuthApiService
import incar.mobile.caring.data.remote.HomeApiService
import incar.mobile.caring.data.remote.IamportApiService
import incar.mobile.caring.data.remote.SignupApiService
import incar.mobile.caring.data.repository.AuthRepositoryImpl
import incar.mobile.caring.data.repository.HomeRepositoryImpl
import incar.mobile.caring.data.repository.SignupRepositoryImpl
import incar.mobile.caring.data.repository.UserSessionRepositoryImpl
import incar.mobile.caring.data.service.NotificationServiceImpl
import incar.mobile.caring.domain.repository.AuthRepository
import incar.mobile.caring.domain.repository.HomeRepository
import incar.mobile.caring.domain.repository.SignupRepository
import incar.mobile.caring.domain.repository.UserSessionRepository
import incar.mobile.caring.domain.service.NotificationService
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.client.request.forms.FormDataContent
import io.ktor.http.ContentType
import io.ktor.http.content.OutgoingContent
import io.ktor.http.encodeURLParameter
import io.ktor.http.withCharset
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.charsets.Charsets
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Ktor FormDataContent가 & + = 같은 특수문자를 form value 안에서 percent-encode 하지 않는 버그 수정.
 * ex) data=[base64]&&name → PHP가 &를 파라미터 구분자로 오해
 */
private val FormEncodingFix = createClientPlugin("FormEncodingFix") {
    client.requestPipeline.intercept(HttpRequestPipeline.Transform) { body ->
        if (body is FormDataContent) {
            val fixed = body.formData.entries()
                .flatMap { (key, values) -> values.map { key to it } }
                .joinToString("&") { (k, v) -> "${k.encodeURLParameter()}=${v.encodeURLParameter()}" }
            proceedWith(object : OutgoingContent.ByteArrayContent() {
                override val contentType = ContentType.Application.FormUrlEncoded.withCharset(
                    Charsets.UTF_8)
                override fun bytes() = fixed.encodeToByteArray()
            })
        }
    }
}

fun networkModule(
    baseUrl: String,
    appVersion: String,
    iamportImpKey: String,
    iamportImpSecret: String,
    kicaaBaseUrl: String = "https://www.car-ing.kr",
) = module {
    single(named("baseUrl")) { baseUrl }
    single(named("kicaaBaseUrl")) { kicaaBaseUrl }
    single {
        HttpClient {
            install(FormEncodingFix)
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true }, contentType = ContentType.Any)
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 10_000
                connectTimeoutMillis = 5_000
                socketTimeoutMillis = 10_000
            }
        }
    }
    single { AuthApiService(get(), baseUrl, appVersion) }
    single { HomeApiService(get(), baseUrl, appVersion) }
    single { IamportApiService(get(), iamportImpKey, iamportImpSecret) }
    single { SignupApiService(get(), baseUrl, appVersion) }
}

val dataModule = module {
    single<UserSessionRepository> { UserSessionRepositoryImpl(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<HomeRepository> { HomeRepositoryImpl(get()) }
    single<SignupRepository> { SignupRepositoryImpl(get()) }
    single<NotificationService> { NotificationServiceImpl() }
}
