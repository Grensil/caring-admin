package incar.mobile.caring.domain.usecase

import incar.mobile.caring.domain.model.UserType
import incar.mobile.caring.domain.repository.UserSessionRepository

class GetUserTypeUseCase(
    private val userSession: UserSessionRepository,
) {
    operator fun invoke(): UserType = userSession.getUserType()
}