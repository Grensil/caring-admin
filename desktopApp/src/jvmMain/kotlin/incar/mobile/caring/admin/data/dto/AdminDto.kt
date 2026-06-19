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
    val adjusters: List<AdjusterDto> = emptyList(),
    val pagination: PaginationDto? = null,
)

@Serializable
data class PaginationDto(
    val total: Int = 0,
    val page: Int = 1,
    val size: Int = 20,
    @SerialName("has_next") val hasNext: Boolean = false,
)

@Serializable
data class AdjusterDto(
    val id: Int = 0,
    val name: String = "",
    val company: String = "",
    val phone: String = "",
    @SerialName("office_phone") val officePhone: String? = null,
    val address: String = "",
    @SerialName("career_years") val careerYears: Int = 0,
    val email: String = "",
    @SerialName("is_visible") val isVisible: Boolean = true,
    @SerialName("review_score") val reviewScore: Double? = null,
    @SerialName("review_count") val reviewCount: Int = 0,
)
