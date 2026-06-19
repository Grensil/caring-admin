package incar.mobile.caring.domain.usecase

import incar.mobile.caring.domain.repository.UserSessionRepository

class GetUserInfoUseCase(
    private val userSession: UserSessionRepository,
) {
    fun getUserIdx(): String = userSession.getUserIdx()
    fun getUserName(): String = userSession.getUserName()
    fun getUserPhone(): String = userSession.getUserPhone()
}
