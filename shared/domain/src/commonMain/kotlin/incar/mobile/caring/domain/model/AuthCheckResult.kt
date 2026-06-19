package incar.mobile.caring.domain.model

// /account/user_check next_step 기반 라우팅
sealed class AuthCheckResult {
    // 저장된 세션 없음 (최초 진입 / 로그아웃 상태)
    data object NoSession : AuthCheckResult()

    data class NavigateToMain(
        val userIdx: String,
        val pgValue: String,
        val carposSeq: Int = -1,
        val userType: UserType? = null, // null = 서버 응답에 role 정보 없음, 기존 세션 유지
    ) : AuthCheckResult()

    data object NavigateToAddCar : AuthCheckResult()

    data object NavigateToInactiveAccount : AuthCheckResult()

    data class NavigateToAgreement(
        val type: String,
    ) : AuthCheckResult() // both, use, privacy

    data object ClearAndReAuth : AuthCheckResult() // no_hash

    data class NeedUpdate(
        val latestVersion: String,
    ) : AuthCheckResult()
}
