package incar.mobile.caring.admin.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdjusterUpdateResponseDto(
    val result: String,
    @SerialName("error_message") val errorMessage: String? = null,
    val adjuster: AdjusterDto? = null,
)

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
    val fax: String? = null,
    val address: String? = null,
    @SerialName("address_detail") val addressDetail: String? = null,
    @SerialName("career_years") val careerYears: Int = 0,
    val regions: List<String>? = null,
    val fields: List<String>? = null,
    @SerialName("consulting_fields") val consultingFields: List<String> = emptyList(),
    val email: String? = null,
    @SerialName("profile_image") val profileImage: String? = null,
    val qualifications: List<String>? = null,
    @SerialName("main_career") val mainCareer: String? = null,
    @SerialName("account_seq") val accountSeq: Int? = null,
    @SerialName("kicaa_company_id") val kicaaCompanyId: String? = null,
    @SerialName("is_visible") val isVisible: Boolean = true,
    @SerialName("review_score") val reviewScore: Double? = null,
    @SerialName("review_count") val reviewCount: Int = 0,
    @SerialName("consulting_review_score") val consultingReviewScore: Double? = null,
    @SerialName("consulting_review_count") val consultingReviewCount: Int = 0,
    val lat: Double? = null,
    val lng: Double? = null,
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("updated_at") val updatedAt: String = "",
)

@Serializable
data class ConsultingRequestListResponseDto(
    val result: String,
    @SerialName("error_message") val errorMessage: String? = null,
    @SerialName("consulting_requests") val consultingRequests: List<ConsultingRequestDto> = emptyList(),
    val pagination: PaginationDto? = null,
)

@Serializable
data class ConsultingRequestDto(
    val id: Int = 0,
    @SerialName("adjuster_id") val adjusterId: Int = 0,
    @SerialName("adjuster_name_snapshot") val adjusterNameSnapshot: String? = null,
    @SerialName("consulting_field") val consultingField: String? = null,
    @SerialName("requester_account_seq") val requesterAccountSeq: Int? = null,
    @SerialName("requester_name_snapshot") val requesterNameSnapshot: String? = null,
    @SerialName("accident_type") val accidentType: String? = null,
    @SerialName("accident_date") val accidentDate: String? = null,
    val region: String? = null,
    @SerialName("accident_detail") val accidentDetail: String? = null,
    @SerialName("inquiry_content") val inquiryContent: String? = null,
    val contact: String? = null,
    val status: String = "",
    @SerialName("reject_reason") val rejectReason: String? = null,
    @SerialName("cancel_reason") val cancelReason: String? = null,
    @SerialName("requester_name") val requesterName: String? = null,
    @SerialName("requester_phone") val requesterPhone: String? = null,
    @SerialName("adjuster_name_full") val adjusterNameFull: String? = null,
    @SerialName("adjuster_company") val adjusterCompany: String? = null,
    @SerialName("adjuster_phone") val adjusterPhone: String? = null,
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("updated_at") val updatedAt: String = "",
)

@Serializable
data class EducationRequestListResponseDto(
    val result: String,
    @SerialName("error_message") val errorMessage: String? = null,
    @SerialName("education_requests") val educationRequests: List<EducationRequestDto> = emptyList(),
    val pagination: PaginationDto? = null,
)

@Serializable
data class EducationRequestDto(
    val id: Int = 0,
    @SerialName("adjuster_id") val adjusterId: Int = 0,
    @SerialName("adjuster_name_snapshot") val adjusterNameSnapshot: String? = null,
    @SerialName("requester_id") val requesterId: Int? = null,
    @SerialName("org_name") val orgName: String? = null,
    val headcount: Int? = null,
    val location: String? = null,
    val field: String? = null,
    @SerialName("desired_date") val desiredDate: String? = null,
    val content: String? = null,
    val status: String = "",
    @SerialName("reject_reason") val rejectReason: String? = null,
    @SerialName("cancel_reason") val cancelReason: String? = null,
    @SerialName("confirmed_date") val confirmedDate: String? = null,
    @SerialName("requester_name") val requesterName: String? = null,
    @SerialName("requester_phone") val requesterPhone: String? = null,
    @SerialName("adjuster_name_full") val adjusterNameFull: String? = null,
    @SerialName("adjuster_company") val adjusterCompany: String? = null,
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("updated_at") val updatedAt: String = "",
)
