package incar.mobile.caring.domain.repository

import incar.mobile.caring.domain.model.AuthCheckResult
import incar.mobile.caring.domain.model.CertServerResponse
import incar.mobile.caring.domain.model.IamportCertDetail
import incar.mobile.caring.domain.model.InitialResult

interface AuthRepository {
    /** hash_value 있을 때 → /account/user_check */
    suspend fun checkUser(
        hashValue: String,
        pushId: String,
    ): AuthCheckResult

    /** 최초 진입 (hash_value 없음) → /account/initial */
    suspend fun initial(): InitialResult

    /** 약관 동의 → /account/agree_update */
    suspend fun sendAgreement(
        tokenKey: String,
        type: String,
    ): Result<Unit>

    /** 휴면(비활성) 계정 활성화 → /account/sleep_update */
    suspend fun activateInactiveAccount(tokenKey: String): Result<Unit>

    /**
     * 본인인증 완료 후 서버 전달
     * ttt: "signup" → /account/sms_done
     * ttt: "signin" → /account/sms_done_signin (deprecated 2026-05-21, 실질적으로 signup만 사용)
     */
    suspend fun sendCertData(
        encodedData: String,
        type: String,
    ): CertServerResponse

    /**
     * Iamport imp_uid로 본인인증 상세 정보 조회
     * POST https://api.iamport.kr/users/getToken → access_token
     * GET  https://api.iamport.kr/certifications/{imp_uid} → CI, 이름, 전화번호 등
     */
    suspend fun fetchIamportCert(impUid: String): IamportCertDetail
}
