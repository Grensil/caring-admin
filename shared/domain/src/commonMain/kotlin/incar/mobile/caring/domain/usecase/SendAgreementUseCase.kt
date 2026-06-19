package incar.mobile.caring.domain.usecase

import incar.mobile.caring.domain.repository.AuthRepository
import incar.mobile.caring.domain.repository.UserSessionRepository
import incar.mobile.caring.domain.service.TokenService

class SendAgreementUseCase(
    private val authRepository: AuthRepository,
    private val userSession: UserSessionRepository,
    private val tokenService: TokenService,
) {
    suspend operator fun invoke(type: String): Result<Unit> {
        val tokenKey = tokenService.makeToken(userSession.getUserIdx())
        return authRepository.sendAgreement(tokenKey, type)
    }

    suspend fun activateInactiveAccount(): Result<Unit> {
        val tokenKey = tokenService.makeToken(userSession.getUserIdx())
        return authRepository.activateInactiveAccount(tokenKey)
    }
}
