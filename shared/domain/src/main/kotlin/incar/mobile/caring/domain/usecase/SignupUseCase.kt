package incar.mobile.caring.domain.usecase

import incar.mobile.caring.domain.model.FAInfo
import incar.mobile.caring.domain.model.SignupDoneData
import incar.mobile.caring.domain.repository.SignupRepository
import incar.mobile.caring.domain.repository.UserSessionRepository
import incar.mobile.caring.domain.service.TokenService

class SignupUseCase(
    private val signupRepository: SignupRepository,
    private val userSessionRepository: UserSessionRepository,
    private val tokenService: TokenService,
) {
    suspend fun searchFA(searchWord: String): List<FAInfo> =
        signupRepository.searchFA(searchWord)

    suspend fun updateFA(faName: String, faPhone: String, faOffice: String): Result<Unit> {
        val userIdx = userSessionRepository.getUserIdx()
        val tokenKey = tokenService.makeToken(userIdx)
        return signupRepository.updateFA(tokenKey, faName, faPhone, faOffice)
    }

    suspend fun addFakeCar(): Result<Unit> {
        val userIdx = userSessionRepository.getUserIdx()
        val tokenKey = tokenService.makeToken(userIdx)
        return signupRepository.addFakeCar(
            tokenKey = tokenKey,
            ownerName = userSessionRepository.getUserName(),
            ownerPhone = userSessionRepository.getUserPhone(),
            ownerCarrier = userSessionRepository.getUserCarrier(),
            ownerBirth = userSessionRepository.getUserBirth(),
            ownerGender = userSessionRepository.getUserGender(),
        )
    }

    suspend fun getSignupDone(): SignupDoneData {
        val userIdx = userSessionRepository.getUserIdx()
        val tokenKey = tokenService.makeToken(userIdx)
        return signupRepository.getSignupDone(tokenKey)
    }
}
