package incar.mobile.caring.data.service

import incar.mobile.caring.domain.service.CryptoService
import java.security.MessageDigest

class JvmCryptoService : CryptoService {
    override fun sha256Hex(input: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(input).joinToString("") { "%02x".format(it) }
    }

    override fun currentTimeMillis(): Long = System.currentTimeMillis()
}
