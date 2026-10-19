package com.memori.app.ui.screens.auth

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

data class LoginRequest(
    val emailUsuario: String,
    val senhaUsuario: String
)

data class RegisterRequest(
    val nome: String,
    val nomeUsuario: String,
    val emailUsuario: String,
    val senhaUsuario: String
)

data class UsuarioResponse(
    @SerializedName(value = "id", alternate = ["_id"])
    val id: String,
    val nome: String,
    val nomeUsuario: String,
    val emailUsuario: String
)

data class LoginResponse(
    val token: String,
    val usuario: UsuarioResponse
)

data class RegisterResponse(
    val message: String,
    val usuario: UsuarioResponse
)

data class PerfilResponse(
    val usuario: UsuarioResponse
)

interface AuthApi {
    @POST("api/usuario/auth")
    suspend fun login(
        @Body dados: LoginRequest
    ): LoginResponse

    @POST("api/usuario")
    suspend fun register(
        @Body dados: RegisterRequest
    ): RegisterResponse

    @GET("api/usuario/{id}")
    suspend fun buscarPerfil(
        @Path("id") id: String,
        @Header("Authorization") authorization: String
    ): PerfilResponse
}

object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:3000/"

    val authApi: AuthApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }
}