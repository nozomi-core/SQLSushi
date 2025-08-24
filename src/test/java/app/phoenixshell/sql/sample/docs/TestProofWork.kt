package app.phoenixshell.sql.sample.docs

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature
import java.util.Base64
import java.util.UUID

class TestProofWork {

    val array = listOf(
        """🚀""",
        """0"""
    )

    //@Test
    fun generateKey() {
        val keyPairGenerator = KeyPairGenerator.getInstance("Ed25519")
        val keyPair = keyPairGenerator.generateKeyPair()
        val privateKey: PrivateKey = keyPair.private
        val publicKey: PublicKey = keyPair.public

        //println("Private Key (Base64): ${Base64.getEncoder().encodeToString(privateKey.encoded)}")
        //println("Public Key  (Base64): ${Base64.getEncoder().encodeToString(publicKey.encoded)}")

        // 2. Sign some data
        val data = "Data to be signed using Ed25519".toByteArray(Charsets.UTF_8)
        val signature = Signature.getInstance("Ed25519")
        signature.initSign(privateKey)
        signature.update(data)
        val digitalSignature = signature.sign()

        //println("Signature (Base64): ${Base64.getEncoder().encodeToString(digitalSignature)}")

        // 3. Verify signature
        val verifier = Signature.getInstance("Ed25519")
        verifier.initVerify(publicKey)
        verifier.update(data)
        val isVerified = verifier.verify(digitalSignature)
    }

    //@Test
    fun signal() {
        assertEquals(array[0].toCharArray()[0].code, "")

    }

    //@Test
    fun testProofOfWork() {

        var found = false

        val result = findValidNonce(UUID.randomUUID().toString(), 5)
        assertEquals("123", result.second)
    }

    fun sha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(input.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    fun hasLeadingHexZeros(hash: String, requiredZeros: Int): Boolean {
        val prefix = "0".repeat(requiredZeros)
        return hash.startsWith(prefix)
    }

    fun findValidNonce(publicKey: String, difficulty: Int): Pair<Int, String> {
        var nonce = 0
        while (true) {
            val input = "$publicKey$nonce"
            val hash = sha256(input)
            if (hasLeadingHexZeros(hash, difficulty)) {
                return Pair(nonce, hash)
            }
            nonce++
        }
    }
}