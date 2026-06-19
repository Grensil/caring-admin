package incar.mobile.caring.admin.data

import incar.mobile.caring.admin.data.dto.AdminLoginResponseDto
import incar.mobile.caring.admin.data.dto.AdjusterListResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Parameters

class AdminApiService(
    private val httpClient: HttpClient,
    private val baseUrl: String,
    private val appVersion: String,
) {
    suspend fun login(id: String, pw: String): AdminLoginResponseDto =
        httpClient.post("$baseUrl/admin/login") {
            setBody(FormDataContent(Parameters.build {
                append("id", id)
                append("pw", pw)
                append("v", appVersion)
            }))
        }.body()

    suspend fun getAdjusters(token: String): AdjusterListResponseDto =
        httpClient.post("$baseUrl/admin/adjusters") {
            setBody(FormDataContent(Parameters.build {
                append("token", token)
                append("v", appVersion)
            }))
        }.body()
}
