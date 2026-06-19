package incar.mobile.caring.admin.data

import incar.mobile.caring.admin.data.dto.AdminLoginResponseDto
import incar.mobile.caring.admin.data.dto.AdjusterListResponseDto
import incar.mobile.caring.admin.data.dto.AdjusterUpdateResponseDto
import incar.mobile.caring.admin.data.dto.ConsultingRequestListResponseDto
import incar.mobile.caring.admin.data.dto.EducationRequestListResponseDto
import incar.mobile.caring.admin.model.Adjuster
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Parameters
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }

class AdminApiService(
    private val httpClient: HttpClient,
    private val baseUrl: String,
    private val appVersion: String,
) {
    suspend fun login(id: String, pw: String): AdminLoginResponseDto {
        val response = httpClient.post("$baseUrl/admin/login") {
            setBody(FormDataContent(Parameters.build {
                append("id", id)
                append("pw", pw)
                append("v", appVersion)
            }))
        }
        val bodyText = response.bodyAsText()
        if (!response.status.isSuccess() || bodyText.trimStart().startsWith("<")) {
            throw Exception("서버 오류 (${response.status.value})\n응답: ${bodyText.take(300)}")
        }
        return json.decodeFromString(bodyText)
    }

    suspend fun getAdjusters(token: String, page: Int = 1, size: Int = 50): AdjusterListResponseDto {
        val response = httpClient.get("$baseUrl/admin/adjusters") {
            header("X-Admin-Token", token)
            url {
                parameters.append("page", page.toString())
                parameters.append("size", size.toString())
            }
        }
        val bodyText = response.bodyAsText()
        if (!response.status.isSuccess() || bodyText.trimStart().startsWith("<")) {
            throw Exception("서버 오류 (${response.status.value})\n응답: ${bodyText.take(300)}")
        }
        return json.decodeFromString(bodyText)
    }

    suspend fun getConsultingRequests(token: String, page: Int = 1, size: Int = 50): ConsultingRequestListResponseDto {
        val response = httpClient.get("$baseUrl/admin/consulting-requests") {
            header("X-Admin-Token", token)
            url {
                parameters.append("page", page.toString())
                parameters.append("size", size.toString())
            }
        }
        val bodyText = response.bodyAsText()
        if (!response.status.isSuccess() || bodyText.trimStart().startsWith("<")) {
            throw Exception("서버 오류 (${response.status.value})\n응답: ${bodyText.take(300)}")
        }
        return json.decodeFromString(bodyText)
    }

    suspend fun getEducationRequests(token: String, page: Int = 1, size: Int = 50): EducationRequestListResponseDto {
        val response = httpClient.get("$baseUrl/admin/education-requests") {
            header("X-Admin-Token", token)
            url {
                parameters.append("page", page.toString())
                parameters.append("size", size.toString())
            }
        }
        val bodyText = response.bodyAsText()
        if (!response.status.isSuccess() || bodyText.trimStart().startsWith("<")) {
            throw Exception("서버 오류 (${response.status.value})\n응답: ${bodyText.take(300)}")
        }
        return json.decodeFromString(bodyText)
    }

    suspend fun updateAdjuster(token: String, id: Int, fields: Map<String, Any?>): AdjusterUpdateResponseDto {
        val response = httpClient.put("$baseUrl/admin/adjusters/$id") {
            header("X-Admin-Token", token)
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(),
                buildJsonObject(fields)))
        }
        val bodyText = response.bodyAsText()
        if (!response.status.isSuccess() || bodyText.trimStart().startsWith("<")) {
            throw Exception("서버 오류 (${response.status.value})\n응답: ${bodyText.take(300)}")
        }
        return json.decodeFromString(bodyText)
    }

    private fun buildJsonObject(fields: Map<String, Any?>): kotlinx.serialization.json.JsonObject {
        val map = fields.mapValues { (_, v) ->
            when (v) {
                null -> kotlinx.serialization.json.JsonNull
                is String -> kotlinx.serialization.json.JsonPrimitive(v)
                is Int -> kotlinx.serialization.json.JsonPrimitive(v)
                is Boolean -> kotlinx.serialization.json.JsonPrimitive(v)
                is List<*> -> kotlinx.serialization.json.JsonArray(
                    v.map { item ->
                        if (item is String) kotlinx.serialization.json.JsonPrimitive(item)
                        else kotlinx.serialization.json.JsonNull
                    }
                )
                else -> kotlinx.serialization.json.JsonPrimitive(v.toString())
            }
        }
        return kotlinx.serialization.json.JsonObject(map)
    }
}
