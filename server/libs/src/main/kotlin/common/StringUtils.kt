package common

import sun.misc.BASE64Decoder
import sun.misc.BASE64Encoder

import java.io.IOException
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

import org.apache.commons.codec.binary.Base64
import java.security.MessageDigest
import java.util.*
import javax.crypto.SecretKeyFactory



object StringUtils {
    // keys for encryption/decryption
    var aes_key0 = "JUTRFGHytr7r6yghbjXuygtfg"
    var aes_key1 = "HX8789u77TRrehhFFE%#"

    private fun prepareKey(key: String, length: Int = 16): ByteArray {
        val sha = MessageDigest.getInstance("SHA-1")
        return  Arrays.copyOf(sha.digest(key.toByteArray(Charsets.UTF_8)), length)
    }
    fun encrypt(value: String): String? {
        return encrypt(aes_key0, aes_key1, value)
    }
    // key 1 = 44 bytes  init vector: 128bits
    private fun encrypt(key: String, iv: String, value: String): String? {
        return encrypt(prepareKey(key, 16), prepareKey(iv, 16), value)
    }
    private fun encrypt(key1: ByteArray, key2: ByteArray, value: String): String? {
        try {
            val iv = IvParameterSpec(key2)
            val skeySpec = SecretKeySpec(key1, "AES")
            val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv)
            val encrypted = cipher.doFinal(value.toByteArray())
            return Base64.encodeBase64String(encrypted)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return null
    }

    fun decrypt(value: String): String? {
        return decrypt(aes_key0, aes_key1, value)
    }
    private fun decrypt(key: String, ivStr: String, encrypted: String): String? {
        try {
            val iv = IvParameterSpec(prepareKey(ivStr))
            val skeySpec = SecretKeySpec(prepareKey(key), "AES")
            val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv)
            val original = cipher.doFinal(Base64.decodeBase64(encrypted))

            return String(original)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return null
    }


    //    private static Random random = new Random((new Date()).getTime());
    var randKey: Long? = 12345.toLong()
    private var random: Random? = null
    /**
     * Encrypts the string along with salt
     * @param userId
     * @return
     * @throws Exception
     */
    fun simpleEncrypt(userId: String): String {
        set()
        val encoder = BASE64Encoder()

        // let's create some dummy salt
        val salt = ByteArray(8)
        random!!.nextBytes(salt)
        return encoder.encode(salt) + encoder.encode(userId.toByteArray())
    }


    /**
     * Decrypts the string and removes the salt
     * @param encryptKey
     * @return
     * @throws Exception
     */
    fun simpleDecrypt(encryptKey: String): String? {
        set()
        // let's ignore the salt
        if (encryptKey.length > 12) {
            val cipher = encryptKey.substring(12)
            val decoder = BASE64Decoder()
            try {
                return String(decoder.decodeBuffer(cipher))
            } catch (e: IOException) {
                //  throw new InvalidImplementationException(
                //    "Failed to perform decryption for key ["+encryptKey+"]",e);
            }

        }
        return null
    }

    private fun set() {

        if (random == null) {
            random = Random(randKey!!)
        }
    }
}