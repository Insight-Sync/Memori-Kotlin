package com.memori.app.ui.screens.trail

import java.io.IOException
import java.util.Locale
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

data class ResultadoRequest(
    val desafioId: String,
    val tentativaId: String,
    val tempoSegundos: Int
)

data class TentativaResponse(
    val id: String,
    val usuarioId: String,
    val desafioId: String,
    val tentativaId: String,
    val tempoSegundos: Int,
    val concluidoEm: String
)

data class SalvarResultadoResponse(
    val message: String,
    val tentativa: TentativaResponse
)

data class RankingTempo(
    val usuarioId: String,
    val nome: String,
    val nomeUsuario: String,
    val tempoSegundos: Int,
    val concluidoEm: String,
    val posicao: Int
)

data class RankingTemposResponse(
    val desafioId: String,
    val rankings: List<RankingTempo>,
    val meuResultado: RankingTempo?
)

interface RankingApi {
    @POST("api/ranking/app/resultados")
    suspend fun salvarResultado(
        @Header("Authorization") authorization: String,
        @Body dados: ResultadoRequest
    ): SalvarResultadoResponse

    @GET("api/ranking/app/tempos")
    suspend fun buscarRanking(
        @Header("Authorization") authorization: String
    ): RankingTemposResponse
}

object RankingClient {
    val api: RankingApi by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RankingApi::class.java)
    }
}

fun formatarTempo(segundos: Int): String {
    return String.format(
        Locale.ROOT,
        "%02d:%02d:%02d",
        segundos / 3600,
        (segundos % 3600) / 60,
        segundos % 60
    )
}

fun mensagemErroRanking(error: Exception): String {
    return when (error) {
        is HttpException -> {
            if (error.code() == 401) {
                "Sua sessão não foi aceita. Volte ao início e entre novamente."
            } else {
                val mensagem = runCatching {
                    val corpo = error.response()?.errorBody()?.string()
                    JSONObject(corpo ?: "{}").optString("error")
                }.getOrNull()

                mensagem?.takeIf { it.isNotBlank() }
                    ?: "Não foi possível concluir a solicitação (${error.code()})."
            }
        }

        is IOException ->
            "Sem conexão com a API. Confira o backend e tente novamente."

        else ->
            "Não foi possível concluir a solicitação."
    }
}