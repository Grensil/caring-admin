package incar.mobile.caring.domain.model

/** Iamport GET /certifications/{imp_uid} 응답에서 추출한 본인인증 정보 */
data class IamportCertDetail(
    val ci: String,        // unique_key — hash 생성에 사용
    val name: String,
    val phone: String,
    val birthday: String,  // "YYYY-MM-DD"
    val gender: String,    // "male" | "female" — IamportCertDetail 내부에서 M/F 변환하지 않음 (UseCase 책임)
    val carrier: String,   // "SKT" | "KT" | "LGT" | "SKT_MVNO" | "KT_MVNO" | "LGT_MVNO"
)
