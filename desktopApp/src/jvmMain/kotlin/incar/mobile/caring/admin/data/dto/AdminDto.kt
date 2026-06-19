package incar.mobile.caring.admin.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdminLoginResponseDto(
    val result: String,
    @SerialName("error_message") val errorMessage: String? = null,
    val token: String? = null,
)

@Serializable
data class AdjusterListResponseDto(
    val result: String,
    @SerialName("error_message") val errorMessage: String? = null,
    @SerialName("adjuster_list") val adjusterList: List<AdjusterDto> = emptyList(),
)

@Serializable
data class AdjusterDto(
    @SerialName("user_idx") val userIdx: String,
    @SerialName("user_name") val userName: String,
    @SerialName("user_phone") val userPhone: String,
    @SerialName("is_live") val isLive: Boolean = false,
    @SerialName("joined_at") val joinedAt: String = "",
    @SerialName("office_name") val officeName: String = "",
)
