package com.omniflow.core.auth

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.nio.ByteBuffer
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenCipher @Inject constructor() {
    private val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }

    fun encrypt(value: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        }
        val encrypted = cipher.doFinal(value.toByteArray(Charsets.UTF_8))
        val payload = ByteBuffer.allocate(Int.SIZE_BYTES + cipher.iv.size + encrypted.size)
            .putInt(cipher.iv.size)
            .put(cipher.iv)
            .put(encrypted)
            .array()
        return Base64.encodeToString(payload, Base64.NO_WRAP)
    }

    fun decrypt(value: String): String {
        val payload = ByteBuffer.wrap(Base64.decode(value, Base64.NO_WRAP))
        val ivLength = payload.int
        require(ivLength in MIN_IV_BYTES..MAX_IV_BYTES) { "Invalid encrypted token payload" }

        val iv = ByteArray(ivLength).also(payload::get)
        val encrypted = ByteArray(payload.remaining()).also(payload::get)
        val cipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, getOrCreateKey(), GCMParameterSpec(AUTH_TAG_BITS, iv))
        }
        return cipher.doFinal(encrypted).toString(Charsets.UTF_8)
    }

    @Synchronized
    private fun getOrCreateKey(): SecretKey {
        val existingKey = keyStore.getKey(KEY_ALIAS, null) as? SecretKey
        if (existingKey != null) return existingKey

        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_PROVIDER).run {
            init(
                KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setRandomizedEncryptionRequired(true)
                    .build(),
            )
            generateKey()
        }
    }

    private companion object {
        const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        const val KEY_ALIAS = "omniflow_session_tokens"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val AUTH_TAG_BITS = 128
        const val MIN_IV_BYTES = 12
        const val MAX_IV_BYTES = 16
    }
}
