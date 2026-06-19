package incar.mobile.caring.domain.usecase

import incar.mobile.caring.domain.model.CertResult
import incar.mobile.caring.domain.repository.AuthRepository
import incar.mobile.caring.domain.repository.UserSessionRepository
import incar.mobile.caring.domain.service.CertificationService

/**
 * Iamport imp_uid → 서버 전송까지 본인인증 전체 플로우 처리
 *
 * 1. fetchIamportCert(impUid)  — Iamport REST API로 CI, 이름, 전화번호 등 조회
 * 2. mkHash(ci)                — CI + "incar" SHA-256 → hash_value
 * 3. mapCarrier(carrier)       — 통신사 코드 변환 (KT→KTF 등)
 * 4. UserSessionRepository에 유저 데이터 저장
 * 5. encodeCertData(...)       — 서버 전송용 hash_key 인코딩
 * 6. sendCertData(encoded)     — 신규: POST /account/sms_done, 기존: POST /account/sms_done_signin
 */
class CertifyFromImpUidUseCase(
    private val authRepository: AuthRepository,
    private val certificationService: CertificationService,
    private val userSessionRepository: UserSessionRepository,
) {
    suspend operator fun invoke(impUid: String): CertResult {
        val detail = authRepository.fetchIamportCert(impUid)

        val hashValue = certificationService.mkHash(detail.ci)
        val mappedCarrier = certificationService.mapCarrier(detail.carrier)

        // hash 저장 전에 기존 세션 여부 확인 → signup / signin 분기
        val certType = if (userSessionRepository.getHashValue().isBlank()) "signup" else "signin"

        userSessionRepository.setHashValue(hashValue)
        userSessionRepository.setUserName(detail.name)
        userSessionRepository.setUserPhone(detail.phone)
        userSessionRepository.setUserBirth(detail.birthday)
        userSessionRepository.setUserGender(detail.gender)
        userSessionRepository.setUserCarrier(mappedCarrier)

        val pushId = userSessionRepository.getPushId()
        val encoded = certificationService.encodeCertData(
            hashValue = hashValue,
            phone = detail.phone,
            birth = detail.birthday,
            gender = detail.gender,
            carrier = mappedCarrier,
            pushId = pushId,
            userName = detail.name,
        )

        val response = authRepository.sendCertData(encoded, type = certType)
        userSessionRepository.setUserIdx(response.userIdx)
        userSessionRepository.setPgValue(response.pgValue)
        userSessionRepository.setUserType(response.userType)
        userSessionRepository.setCarposSeq(response.carposSeq)
        return CertResult(
            hashValue   = hashValue,
            userName    = detail.name,
            userPhone   = detail.phone,
            userBirth   = detail.birthday,
            userGender  = detail.gender,
            userCarrier = mappedCarrier,
            userIdx     = response.userIdx,
            pgValue     = response.pgValue,
        )
    }
}
