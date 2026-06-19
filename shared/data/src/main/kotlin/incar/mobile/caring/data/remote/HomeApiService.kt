package incar.mobile.caring.data.remote

import incar.mobile.caring.data.remote.dto.home.HomeResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Parameters
import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true }

class HomeApiService(
    private val httpClient: HttpClient,
    private val baseUrl: String,
    private val appVersion: String,
) {
    suspend fun getTab1Main(tokenKey: String): HomeResponseDto {
        val response = httpClient.post("$baseUrl/main/tab1_main") {
            setBody(FormDataContent(Parameters.build {
                append("tokenkey", tokenKey)
                append("v", appVersion)
            }))
        }
        val statusCode = response.status.value
        val bodyBytes: ByteArray = response.body()
        val text = bodyBytes.decodeToString()
        if (text.isBlank()) {
            throw IllegalStateException("서버 응답이 비어있습니다 (HTTP $statusCode)")
        }
        return json.decodeFromString(text)
    }
}
