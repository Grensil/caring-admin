package incar.mobile.caring.data.remote.dto.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BaseResponseDto(
    val result: String,
    @SerialName("error_message") val errorMessage: String? = null,
    val version: String? = null,
)

@Serializable
data class UserCheckResponseDto(
    val result: String,
    @SerialName("error_message") val errorMessage: String? = null,
    @SerialName("next_step") val nextStep: String? = null,
    @SerialName("user_idx") val userIdx: String? = null,
    @SerialName("pg_value") val pgValue: String? = null,
    val version: String? = null,
    @SerialName("is_fa") val isFa: Boolean? = null,
    @SerialName("is_super") val isSuper: Boolean? = null,
    @SerialName("is_live") val isLive: Boolean? = null,
    @SerialName("is_adjuster") val isAdjuster: Boolean? = null,
    @SerialName("carpos_seq") val carposSeq: Int? = null,
)

@Serializable
data class InitialResponseDto(
    val result: String,
    @SerialName("error_message") val errorMessage: String? = null,
    @SerialName("is_korea") val isKorea: String? = null,
    val version: String? = null,
)

@Serializable
data class SmsDoneResponseDto(
    val result: String,
    @SerialName("error_message") val errorMessage: String? = null,
    @SerialName("user_idx") val userIdx: String? = null,
    @SerialName("pg_value") val pgValue: String? = null,
    @SerialName("next_step") val nextStep: String? = null,
    val version: String? = null,
    @SerialName("is_fa") val isFa: Boolean? = null,
    @SerialName("is_super") val isSuper: Boolean? = null,
    @SerialName("is_live") val isLive: Boolean? = null,
    @SerialName("is_adjuster") val isAdjuster: Boolean? = null,
    @SerialName("carpos_seq") val carposSeq: Int? = null,
)
