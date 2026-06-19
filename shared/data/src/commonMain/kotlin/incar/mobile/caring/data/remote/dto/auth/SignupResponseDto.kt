package incar.mobile.caring.data.remote.dto.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FASearchResponseDto(
    val result: String,
    @SerialName("error_message") val errorMessage: String = "",
    @SerialName("fa_list") val faList: List<FAItemDto> = emptyList(),
)

@Serializable
data class FAItemDto(
    @SerialName("fa_name") val faName: String = "",
    @SerialName("fa_phone") val faPhone: String = "",
    @SerialName("fa_office") val faOffice: String = "",
)

@Serializable
data class AddCarResponseDto(
    val result: String,
    @SerialName("error_message") val errorMessage: String = "",
)

@Serializable
data class SignupDoneResponseDto(
    val result: String,
    @SerialName("error_message") val errorMessage: String = "",
    @SerialName("user_name") val userName: String = "",
    @SerialName("user_phone") val userPhone: String = "",
    val carnum: String = "",
    val car: String = "",
    val oil: String = "",
    val cnt: Int = 0,
    val insure: String = "",
    @SerialName("end_date") val endDate: String = "",
    @SerialName("fa_name") val faName: String = "",
    @SerialName("fa_phone") val faPhone: String = "",
)
