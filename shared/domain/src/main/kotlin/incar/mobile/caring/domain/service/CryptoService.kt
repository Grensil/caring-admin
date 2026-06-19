package incar.mobile.caring.domain.service

interface CryptoService {
    fun sha256Hex(input: ByteArray): String
    fun currentTimeMillis(): Long
}
