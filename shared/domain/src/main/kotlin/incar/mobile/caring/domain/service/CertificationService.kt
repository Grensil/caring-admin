package incar.mobile.caring.domain.service

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * 본인인증 데이터 처리 (해싱, 인코딩, 통신사 변환)
 * 레거시 서버 인증 알고리즘을 도메인 서비스로 캡슐화
 */
class CertificationService(
    private val cryptoService: CryptoService,
) {
    companion object {
        private val RAND_CHARS = listOf('C', 'D', 'E', 'F', 'A', 'B', 'J', 'K', 'R', 'W', 'V')
        private const val RAND_INDEX_UPPER = 10
    }

    /**
     * CI를 SHA-256 해싱하여 hash_value 생성
     * "incar" 솔트는 레거시 서버 호환을 위한 고정값 — 변경 시 기존 회원 인증 불가
     */
    fun mkHash(ci: String): String {
        val input = (ci + "incar").encodeToByteArray()
        return cryptoService.sha256Hex(input)
    }

    /**
     * 통신사 코드 변환 (KT→KTF, SKT_MVNO→SKM, KT_MVNO→KTM, LGT_MVNO→LGM)
     */
    fun mapCarrier(carrier: String): String =
        when (carrier) {
            "KT" -> "KTF"
            "SKT_MVNO" -> "SKM"
            "KT_MVNO" -> "KTM"
            "LGT_MVNO" -> "LGM"
            else -> carrier
        }

    /**
     * hash_value&&phone&&birth&&gender&&carrier&&pushId 를 base64 후 랜덤 문자 삽입
     */
    @OptIn(ExperimentalEncodingApi::class)
    fun encodeCertData(
        hashValue: String,
        phone: String,
        birth: String,
        gender: String,
        carrier: String,
        pushId: String,
        userName: String,
    ): String {
        val rand = (1 until RAND_INDEX_UPPER).random()
        val raw = "$hashValue&&$phone&&$birth&&$gender&&$carrier&&$pushId"
        val b64 = Base64.Default.encode(raw.encodeToByteArray())
        return "${b64[0]}${RAND_CHARS[rand]}${b64.substring(1)}&&$userName"
    }
}
