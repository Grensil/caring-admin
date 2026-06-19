package incar.mobile.caring.data.remote

import incar.mobile.caring.data.remote.dto.auth.IamportCertResponseDto
import incar.mobile.caring.data.remote.dto.auth.IamportTokenResponseDto
import incar.mobile.caring.domain.model.IamportCertDetail
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.forms.submitForm
import io.ktor.http.parameters

private const val IAMPORT_BASE_URL = "https://api.iamport.kr"

class IamportApiService(
    private val httpClient: HttpClient,
    private val impKey: String,
    private val impSecret: String,
) {
    /** Iamport access_token 발급 */
    private suspend fun getAccessToken(): String {
        val resp: IamportTokenResponseDto = httpClient.submitForm(
            url = "$IAMPORT_BASE_URL/users/getToken",
            formParameters = parameters {
                append("imp_key", impKey)
                append("imp_secret", impSecret)
            },
        ).body()
        return resp.response?.accessToken
            ?: error("Iamport 토큰 발급 실패: ${resp.message}")
    }

    /** imp_uid로 본인인증 상세 조회 → IamportCertDetail */
    suspend fun fetchCertDetail(impUid: String): IamportCertDetail {
        val token = getAccessToken()
        val resp: IamportCertResponseDto = httpClient.get(
            "$IAMPORT_BASE_URL/certifications/$impUid"
        ) {
            bearerAuth(token)
        }.body()

        val cert = resp.response
            ?: error("Iamport 본인인증 정보 없음: ${resp.message}")

        return IamportCertDetail(
            ci = cert.uniqueKey,
            name = cert.name,
            phone = cert.phone,
            birthday = cert.birthday.orEmpty(),
            gender = when (cert.gender) {
                "male"   -> "M"
                "female" -> "F"
                else     -> ""
            },
            carrier = cert.carrier.orEmpty(),
        )
    }
}
