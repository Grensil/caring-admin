package incar.mobile.caring.domain.usecase

import incar.mobile.caring.domain.repository.UserSessionRepository

class LogoutUseCase(
    private val userSession: UserSessionRepository,
) {
    operator fun invoke() = userSession.clearAuthData()
}
