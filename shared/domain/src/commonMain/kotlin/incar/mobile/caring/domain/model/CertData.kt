package incar.mobile.caring.domain.model

// 본인인증 후 결과
data class CertResult(
    val hashValue: String,
    val userName: String,
    val userPhone: String,
    val userBirth: String,
    val userGender: String,
    val userCarrier: String,
    val userIdx: String,
    val pgValue: String,
)
