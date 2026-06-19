package incar.mobile.caring.domain.model

// /account/initial 결과
sealed class InitialResult {
    data object NeedAgreement : InitialResult() // 약관동의 필요 (both)

    data object IsOverseaReview : InitialResult() // 해외 심사 계정

    data class NeedUpdate(
        val latestVersion: String,
    ) : InitialResult()
}
