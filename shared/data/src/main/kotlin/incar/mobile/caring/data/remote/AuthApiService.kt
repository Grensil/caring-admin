package incar.mobile.caring.data.remote

import incar.mobile.caring.data.remote.dto.auth.BaseResponseDto
import incar.mobile.caring.data.remote.dto.auth.InitialResponseDto
import incar.mobile.caring.data.remote.dto.auth.SmsDoneResponseDto
import incar.mobile.caring.data.remote.dto.auth.UserCheckResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Parameters

class AuthApiService(
    private val httpClient: HttpClient,
    private val baseUrl: String,
    private val appVersion: String,
) {
    suspend fun userCheck(
        hashValue: String,
        pushId: String,
    ): UserCheckResponseDto = httpClient.post("$baseUrl/account/user_check") {
        setBody(FormDataContent(Parameters.build {
            append("seq", hashValue)
            append("push_id", pushId)
            append("ver", appVersion)
        }))
    }.body()

    suspend fun initial(): InitialResponseDto = httpClient.post("$baseUrl/account/initial") {
        setBody(FormDataContent(Parameters.build {
            append("ver", appVersion)
        }))
    }.body()

    suspend fun agreeUpdate(
        tokenKey: String,
        type: String,
    ): BaseResponseDto = httpClient.post("$baseUrl/account/agree_update") {
        setBody(FormDataContent(Parameters.build {
            append("tokenkey", tokenKey)
            append("type", type)
            append("ver", appVersion)
        }))
    }.body()

    suspend fun sleepUpdate(tokenKey: String): BaseResponseDto = httpClient.post("$baseUrl/account/sleep_update") {
        setBody(FormDataContent(Parameters.build {
            append("tokenkey", tokenKey)
            append("ver", appVersion)
        }))
    }.body()

    suspend fun smsDone(data: String): SmsDoneResponseDto = httpClient.post("$baseUrl/account/sms_done") {
        setBody(FormDataContent(Parameters.build {
            append("data", data)
            append("ver", appVersion)
        }))
    }.body()

    suspend fun smsDoneSignin(data: String): SmsDoneResponseDto = httpClient.post("$baseUrl/account/sms_done_signin") {
        setBody(FormDataContent(Parameters.build {
            append("data", data)
            append("ver", appVersion)
        }))
    }.body()
}
