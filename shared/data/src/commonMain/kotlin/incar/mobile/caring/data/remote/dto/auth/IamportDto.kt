package incar.mobile.caring.data.remote.dto.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** POST https://api.iamport.kr/users/getToken */
@Serializable
data class IamportTokenResponseDto(
    val code: Int,
    val message: String? = null,
    val response: IamportTokenDto? = null,
)

@Serializable
data class IamportTokenDto(
    @SerialName("access_token") val accessToken: String,
    val now: Long = 0,
    @SerialName("expired_at") val expiredAt: Long = 0,
)

/** GET https://api.iamport.kr/certifications/{imp_uid} */
@Serializable
data class IamportCertResponseDto(
    val code: Int,
    val message: String? = null,
    val response: IamportCertDto? = null,
)

@Serializable
data class IamportCertDto(
    @SerialName("imp_uid") val impUid: String,
    @SerialName("merchant_uid") val merchantUid: String,
    @SerialName("unique_key") val uniqueKey: String,  // CI
    val name: String,
    val gender: String? = null,   // "male" | "female"
    val birthday: String? = null, // "YYYY-MM-DD"
    val phone: String,
    val carrier: String? = null,  // "SKT" | "KT" | "LGT" | "SKT_MVNO" | "KT_MVNO" | "LGT_MVNO"
    val certified: Boolean = false,
)
