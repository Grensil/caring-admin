package incar.mobile.caring.domain.usecase

import incar.mobile.caring.domain.model.CertResult
import incar.mobile.caring.domain.repository.AuthRepository
import incar.mobile.caring.domain.repository.UserSessionRepository

/**
 * 본인인증 데이터 서버 전송 후 세션 저장
 * - encodedData는 호출 측에서 CertificationService로 인코딩한 값
 */
class CertifyUseCase(
    private val authRepository: AuthRepository,
    private val userSession: UserSessionRepository,
) {
    suspend fun sendCertData(
        encodedData: String,
        type: String,
    ): CertResult {
        val response = authRepository.sendCertData(encodedData, type)
        userSession.setUserIdx(response.userIdx)
        userSession.setPgValue(response.pgValue)
        userSession.setUserType(response.userType)
        userSession.setCarposSeq(response.carposSeq)
        return CertResult(
            hashValue   = userSession.getHashValue(),
            userName    = userSession.getUserName(),
            userPhone   = userSession.getUserPhone(),
            userBirth   = userSession.getUserBirth(),
            userGender  = userSession.getUserGender(),
            userCarrier = userSession.getUserCarrier(),
            userIdx     = response.userIdx,
            pgValue     = response.pgValue,
        )
    }
}
