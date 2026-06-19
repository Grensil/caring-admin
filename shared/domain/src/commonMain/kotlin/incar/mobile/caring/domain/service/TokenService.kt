package incar.mobile.caring.domain.service

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * 서버 인증용 토큰 생성 (기존 make_token 로직 동일)
 * user_idx 기반으로 시간 + idx 조합 base64
 */
class TokenService(
    private val cryptoService: CryptoService,
) {
    companion object {
        private const val UNIX_TIMESTAMP_LENGTH = 10
        private const val UNIX_INNER_LENGTH = 9
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun makeToken(idx: String): String {
        val unixtime10 = (cryptoService.currentTimeMillis() / 1000).toString()
        val unixtime9 = unixtime10.substring(1, UNIX_TIMESTAMP_LENGTH)
        val fUnix = unixtime9.substring(0, 1)
        val bUnix = unixtime9.substring(1, UNIX_INNER_LENGTH)
        val idxLength = idx.length
        val idxMulti = (idx.toLongOrNull()?.times(idxLength) ?: 0L).toString()
        val idxMultiLength = idxMulti.length
        val first = "$fUnix$idxMulti$bUnix$idxMultiLength"
        val revFirst = first.reversed()
        val tail = "$revFirst$idxLength"
        return Base64.Default.encode(tail.encodeToByteArray())
    }
}
