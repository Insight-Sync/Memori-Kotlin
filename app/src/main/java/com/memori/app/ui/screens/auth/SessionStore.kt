package com.memori.app.ui.screens.auth

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.AtomicFile
import android.util.Base64
import java.io.File
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import org.json.JSONObject

class SessionStore(context: Context) {

    private val arquivo = AtomicFile(
        File(context.noBackupFilesDir, "memori_session")
    )

    private val alias = "memori_session_key"

    private fun chave(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)

        val existente = keyStore.getKey(alias, null) as? SecretKey
        if (existente != null) return existente

        val gerador = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            "AndroidKeyStore"
        )

        gerador.init(
            KeyGenParameterSpec.Builder(
                alias,
                KeyProperties.PURPOSE_ENCRYPT or
                        KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(
                    KeyProperties.ENCRYPTION_PADDING_NONE
                )
                .build()
        )

        return gerador.generateKey()
    }

    fun salvar(token: String) {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, chave())

        val criptografado = cipher.doFinal(
            token.toByteArray(Charsets.UTF_8)
        )

        val conteudo = JSONObject()
            .put(
                "iv",
                Base64.encodeToString(cipher.iv, Base64.NO_WRAP)
            )
            .put(
                "dados",
                Base64.encodeToString(
                    criptografado,
                    Base64.NO_WRAP
                )
            )
            .toString()

        val saida = arquivo.startWrite()

        try {
            saida.write(conteudo.toByteArray(Charsets.UTF_8))
            arquivo.finishWrite(saida)
        } catch (error: Exception) {
            arquivo.failWrite(saida)
            throw error
        }
    }

    fun carregar(): String? {
        if (!arquivo.baseFile.exists()) return null

        return try {
            val conteudo = arquivo.openRead().use {
                it.readBytes().toString(Charsets.UTF_8)
            }

            val json = JSONObject(conteudo)

            val iv = Base64.decode(
                json.getString("iv"),
                Base64.NO_WRAP
            )

            val dados = Base64.decode(
                json.getString("dados"),
                Base64.NO_WRAP
            )

            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(
                Cipher.DECRYPT_MODE,
                chave(),
                GCMParameterSpec(128, iv)
            )

            val token = cipher.doFinal(dados)
                .toString(Charsets.UTF_8)

            if (tokenNaoExpirou(token)) {
                token
            } else {
                limpar()
                null
            }
        } catch (error: Exception) {
            limpar()
            null
        }
    }

    fun limpar() {
        arquivo.delete()
    }

    private fun tokenNaoExpirou(token: String): Boolean {
        return runCatching {
            val partes = token.split(".")
            if (partes.size != 3) return@runCatching false

            val payload = Base64.decode(
                partes[1],
                Base64.URL_SAFE or Base64.NO_WRAP
            ).toString(Charsets.UTF_8)

            val expiracao = JSONObject(payload).getLong("exp")
            val agora = System.currentTimeMillis() / 1000

            expiracao > agora
        }.getOrDefault(false)
    }
}

