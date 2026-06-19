package incar.mobile.caring.data.remote

import incar.mobile.caring.data.remote.dto.auth.AddCarResponseDto
import incar.mobile.caring.data.remote.dto.auth.BaseResponseDto
import incar.mobile.caring.data.remote.dto.auth.FASearchResponseDto
import incar.mobile.caring.data.remote.dto.auth.SignupDoneResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.http.parameters

class SignupApiService(
    private val httpClient: HttpClient,
    private val baseUrl: String,
    private val appVersion: String,
) {
    suspend fun searchFA(searchWord: String): FASearchResponseDto =
        httpClient.submitForm(
            url = "$baseUrl/account/fa_search",
            formParameters = parameters {
                append("search_word", searchWord)
                append("v", appVersion)
            },
        ).body()

    suspend fun updateFA(
        tokenKey: String,
        faName: String,
        faPhone: String,
        faOffice: String,
    ): BaseResponseDto =
        httpClient.submitForm(
            url = "$baseUrl/account/update_fa",
            formParameters = parameters {
                append("tokenkey", tokenKey)
                append("fa_name", faName)
                append("fa_phone", faPhone)
                append("fa_office", faOffice)
                append("v", appVersion)
            },
        ).body()

    suspend fun addFakeCar(
        tokenKey: String,
        ownerName: String,
        ownerPhone: String,
        ownerCarrier: String,
        ownerBirth: String,
        ownerGender: String,
    ): AddCarResponseDto =
        httpClient.submitForm(
            url = "$baseUrl/account/add_mycar",
            formParameters = parameters {
                append("tokenkey", tokenKey)
                append("owner_type", "fake")
                append("owner_name", ownerName)
                append("owner_phone", ownerPhone)
                append("owner_carrier", ownerCarrier)
                append("owner_birth", ownerBirth)
                append("owner_gender", ownerGender)
                append("v", appVersion)
            },
        ).body()

    suspend fun getSignupDone(tokenKey: String): SignupDoneResponseDto =
        httpClient.submitForm(
            url = "$baseUrl/account/signup_done",
            formParameters = parameters {
                append("tokenkey", tokenKey)
                append("v", appVersion)
            },
        ).body()
}
