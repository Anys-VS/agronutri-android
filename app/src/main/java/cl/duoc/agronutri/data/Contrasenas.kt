package cl.duoc.agronutri.data

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object Contrasenas {
    private const val ITERACIONES = 100_000
    private const val LARGO_BITS = 256

    fun nuevaSal(): ByteArray = ByteArray(16).also { SecureRandom().nextBytes(it) }

    fun hash(clave: String, sal: ByteArray): ByteArray {
        val spec = PBEKeySpec(clave.toCharArray(), sal, ITERACIONES, LARGO_BITS)
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                .generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }

    fun verificar(clave: String, salB64: String, hashB64: String): Boolean {
        val sal = Base64.getDecoder().decode(salB64)
        val esperado = Base64.getDecoder().decode(hashB64)
        return MessageDigest.isEqual(hash(clave, sal), esperado)
    }

    fun aBase64(bytes: ByteArray): String = Base64.getEncoder().encodeToString(bytes)
}