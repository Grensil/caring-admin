package incar.mobile.caring.data.repository

import incar.mobile.caring.data.remote.AuthApiService
import incar.mobile.caring.data.remote.IamportApiService
import incar.mobile.caring.domain.model.AuthCheckResult
import incar.mobile.caring.domain.model.CertServerResponse
import incar.mobile.caring.domain.model.IamportCertDetail
import incar.mobile.caring.domain.model.InitialResult
import incar.mobile.caring.domain.model.UserType
import incar.mobile.caring.domain.repository.AuthRepository

private const val BUILD_VERSION = 1051200

class AuthRepositoryImpl(
    private val authApiService: AuthApiService,
    private val iamportApiService: IamportApiService,
) : AuthRepository {

    override suspend fun checkUser(hashValue: String, pushId: String): AuthCheckResult {
        val resp = authApiService.userCheck(hashValue, pushId)

        if (resp.result != "success") {
            return AuthCheckResult.ClearAndReAuth
        }

        val serverVersion = resp.version?.toIntOrNull() ?: 0
        if (serverVersion > BUILD_VERSION) {
            return AuthCheckResult.NeedUpdate(resp.version ?: "")
        }

        val userType: UserType? = if (resp.isFa != null) {
            val carposSeq = resp.carposSeq ?: -1
            UserType.from(
                isFa       = resp.isFa,
                isLive     = resp.isLive ?: false,
                isSuper    = resp.isSuper ?: false,
                isAdjuster = resp.isAdjuster ?: false,
                carposSeq  = carposSeq,
            )
        } else null

        return when (resp.nextStep) {
            "login"                  -> AuthCheckResult.NavigateToMain(
                userIdx   = resp.userIdx ?: "",
                pgValue   = resp.pgValue ?: "",
                carposSeq = resp.carposSeq ?: -1,
                userType  = userType,
            )
            "addcar"                 -> AuthCheckResult.NavigateToAddCar
            "sleep"                  -> AuthCheckResult.NavigateToInactiveAccount
            "both", "use", "privacy" -> AuthCheckResult.NavigateToAgreement(resp.nextStep)
            "no_hash"                -> AuthCheckResult.ClearAndReAuth
            else                     -> AuthCheckResult.ClearAndReAuth
        }
    }

    override suspend fun initial(): InitialResult {
        val resp = authApiService.initial()

        if (resp.result != "success") {
            throw IllegalStateException(resp.errorMessage ?: "initial 실패")
        }

        val serverVersion = resp.version?.toIntOrNull() ?: 0
        if (serverVersion > BUILD_VERSION) {
            return InitialResult.NeedUpdate(resp.version ?: "")
        }

        return if (resp.isKorea == "N") InitialResult.IsOverseaReview
        else InitialResult.NeedAgreement
    }

    override suspend fun sendAgreement(tokenKey: String, type: String): Result<Unit> =
        runCatching {
            val resp = authApiService.agreeUpdate(tokenKey, type)
            if (resp.result != "success") throw IllegalStateException(resp.errorMessage)
        }

    override suspend fun activateInactiveAccount(tokenKey: String): Result<Unit> =
        runCatching {
            val resp = authApiService.sleepUpdate(tokenKey)
            if (resp.result != "success") throw IllegalStateException(resp.errorMessage)
        }

    override suspend fun sendCertData(encodedData: String, type: String): CertServerResponse {
        val resp = if (type == "signup") authApiService.smsDone(encodedData)
                   else authApiService.smsDoneSignin(encodedData)

        if (resp.result != "success") {
            throw IllegalStateException(resp.errorMessage ?: "본인인증 서버 전송 실패")
        }

        val carposSeq = resp.carposSeq ?: -1
        return CertServerResponse(
            userIdx   = resp.userIdx ?: "",
            pgValue   = resp.pgValue ?: "",
            userType  = UserType.from(
                isFa       = resp.isFa ?: false,
                isLive     = resp.isLive ?: false,
                isSuper    = resp.isSuper ?: false,
                isAdjuster = resp.isAdjuster ?: false,
                carposSeq  = carposSeq,
            ),
            carposSeq = carposSeq,
        )
    }

    override suspend fun fetchIamportCert(impUid: String): IamportCertDetail =
        iamportApiService.fetchCertDetail(impUid)
}
