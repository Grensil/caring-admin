package incar.mobile.caring.domain.usecase

import incar.mobile.caring.domain.model.AuthCheckResult
import incar.mobile.caring.domain.model.InitialResult
import incar.mobile.caring.domain.repository.AuthRepository
import incar.mobile.caring.domain.repository.UserSessionRepository

class CheckAuthUseCase(
    private val authRepository: AuthRepository,
    private val userSession: UserSessionRepository,
) {
    /**
     * 저장된 세션을 읽어 인증 상태를 확인하고, 결과에 따른 세션 업데이트까지 처리합니다.
     */
    suspend fun checkAuth(): AuthCheckResult {
        val hashValue = userSession.getHashValue()
        if (hashValue.isBlank()) return AuthCheckResult.NoSession

        val pushId = userSession.getPushId()
        return when (val result = authRepository.checkUser(hashValue, pushId)) {
            is AuthCheckResult.NavigateToMain -> {
                userSession.setUserIdx(result.userIdx)
                userSession.setPgValue(result.pgValue)
                result.userType?.let {
                    userSession.setUserType(it)
                    userSession.setCarposSeq(result.carposSeq)
                }
                result
            }
            is AuthCheckResult.ClearAndReAuth -> {
                userSession.clearAuthData()
                result
            }
            else -> result
        }
    }

    fun hasSession(): Boolean = userSession.getHashValue().isNotBlank()

    fun clearSession() = userSession.clearAuthData()

    suspend fun initial(): InitialResult = authRepository.initial()
}
