package incar.mobile.caring.domain.usecase

import incar.mobile.caring.domain.model.HomeData
import incar.mobile.caring.domain.repository.HomeRepository
import incar.mobile.caring.domain.repository.UserSessionRepository
import incar.mobile.caring.domain.service.TokenService

class GetHomeDataUseCase(
    private val homeRepository: HomeRepository,
    private val userSession: UserSessionRepository,
    private val tokenService: TokenService,
) {
    suspend operator fun invoke(): HomeData {
        val tokenKey = tokenService.makeToken(userSession.getUserIdx())
        return homeRepository.getHomeData(tokenKey)
    }
}
