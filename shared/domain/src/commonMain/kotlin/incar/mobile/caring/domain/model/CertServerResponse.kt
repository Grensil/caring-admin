package incar.mobile.caring.domain.model

/** 본인인증 API(/account/sms_done) 서버 응답 — Repository 레이어 반환 타입 */
data class CertServerResponse(
    val userIdx: String,
    val pgValue: String,
    val userType: UserType,
    val carposSeq: Int,
)
