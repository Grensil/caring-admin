package incar.mobile.caring.domain.model

data class UserSession(
    val hashValue: String,
    val userIdx: String,
    val pgValue: String,
    val userName: String,
    val userPhone: String,
    val userBirth: String,
    val userGender: String,
    val userCarrier: String,
    val pushId: String,
    val apiServer: String,
)
