package com.memori.app.ui.screens.auth

import android.app.Application
import android.content.pm.ApplicationInfo
import android.util.Base64
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException

val LocalAuthViewModel = staticCompositionLocalOf<AuthViewModel> {
    error("AuthViewModel não foi disponibilizado pela navegação.")
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val sessionStore = SessionStore(application)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _sessionToken = MutableStateFlow<String?>(null)
    val sessionToken: StateFlow<String?> = _sessionToken

    private val _sessionReady = MutableStateFlow(false)
    val sessionReady: StateFlow<Boolean> = _sessionReady

    private val _usuario = MutableStateFlow<UsuarioResponse?>(null)
    val usuario: StateFlow<UsuarioResponse?> = _usuario

    private val _perfilLoading = MutableStateFlow(false)
    val perfilLoading: StateFlow<Boolean> = _perfilLoading

    private val _perfilErro = MutableStateFlow<String?>(null)
    val perfilErro: StateFlow<String?> = _perfilErro

    private val _avatarSalvando = MutableStateFlow(false)
    val avatarSalvando: StateFlow<Boolean> = _avatarSalvando

    private val _avatarErro = MutableStateFlow<String?>(null)
    val avatarErro: StateFlow<String?> = _avatarErro

    private val avataresPermitidos = setOf(
        "amarelo",
        "azul",
        "vermelho"
    )

    init {
        viewModelScope.launch {
            try {
                val token = withContext(Dispatchers.IO) {
                    sessionStore.carregar()
                }

                _sessionToken.value = token

                if (token != null) {
                    carregarPerfil(token)
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _perfilErro.value =
                    "Não foi possível recuperar a sessão salva."
            } finally {
                _sessionReady.value = true
            }
        }
    }

    fun login(
        email: String,
        pass: String,
        onSuccess: () -> Unit
    ) {
        if (!_sessionReady.value) return
        if (_authState.value is AuthState.Loading) return

        if (email.isBlank() || pass.isBlank()) {
            _authState.value = AuthState.Error(
                "Informe o e-mail e a senha."
            )
            return
        }

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            try {
                val resposta = ApiClient.authApi.login(
                    LoginRequest(
                        emailUsuario = email.trim().lowercase(),
                        senhaUsuario = pass
                    )
                )

                check(resposta.token.isNotBlank()) {
                    "A API não retornou um token válido."
                }

                val perfil = ApiClient.authApi.buscarPerfil(
                    id = resposta.usuario.id,
                    authorization = "Bearer ${resposta.token}"
                )

                check(perfil.usuario.id == resposta.usuario.id) {
                    "A API retornou um perfil inválido."
                }

                withContext(Dispatchers.IO) {
                    sessionStore.salvar(resposta.token)
                }

                _sessionToken.value = resposta.token
                _usuario.value = perfil.usuario
                _perfilErro.value = null
                _avatarErro.value = null
                _avatarSalvando.value = false

                _authState.value = AuthState.Success(
                    "Login realizado!"
                )
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _authState.value = AuthState.Error(
                    mensagemErro(error)
                )
                return@launch
            }

            onSuccess()
        }
    }

    fun register(
        email: String,
        pass: String,
        name: String,
        username: String,
        onSuccess: () -> Unit
    ) {
        if (_authState.value is AuthState.Loading) return

        if (listOf(email, pass, name, username).any { it.isBlank() }) {
            _authState.value = AuthState.Error(
                "Preencha todos os campos."
            )
            return
        }

        if (pass.length < 8) {
            _authState.value = AuthState.Error(
                "A senha deve ter pelo menos 8 caracteres."
            )
            return
        }

        if (pass.toByteArray(Charsets.UTF_8).size > 72) {
            _authState.value = AuthState.Error(
                "A senha excede o tamanho permitido."
            )
            return
        }

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            try {
                ApiClient.authApi.register(
                    RegisterRequest(
                        nome = name.trim(),
                        nomeUsuario = username.trim(),
                        emailUsuario = email.trim().lowercase(),
                        senhaUsuario = pass
                    )
                )

                _authState.value = AuthState.Success(
                    "Conta criada! Entre com seu e-mail e senha."
                )
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _authState.value = AuthState.Error(
                    mensagemErro(error)
                )
                return@launch
            }

            onSuccess()
        }
    }

    fun atualizarPerfil() {
        val token = _sessionToken.value ?: return

        if (_perfilLoading.value || _avatarSalvando.value) return
        if (_authState.value is AuthState.Loading) return

        viewModelScope.launch {
            carregarPerfil(token)
        }
    }

    private suspend fun carregarPerfil(token: String) {
        _perfilLoading.value = true
        _perfilErro.value = null

        try {
            val partes = token.split(".")
            require(partes.size == 3)

            val payload = Base64.decode(
                partes[1],
                Base64.URL_SAFE or Base64.NO_WRAP
            ).toString(Charsets.UTF_8)

            val id = JSONObject(payload).getString("id")

            val resposta = ApiClient.authApi.buscarPerfil(
                id = id,
                authorization = "Bearer $token"
            )

            check(resposta.usuario.id == id) {
                "A API retornou um perfil inválido."
            }

            if (_sessionToken.value == token) {
                _usuario.value = resposta.usuario
            }
        } catch (error: CancellationException) {
            throw error
        } catch (error: HttpException) {
            if (_sessionToken.value == token) {
                if (error.code() == 401) {
                    encerrarSessao()
                } else {
                    _perfilErro.value = mensagemErro(error)
                }
            }
        } catch (error: Exception) {
            if (_sessionToken.value == token) {
                _perfilErro.value =
                    "Não foi possível carregar seu perfil. Tente novamente."
            }
        } finally {
            _perfilLoading.value = false
        }
    }

    fun limparDemonstracao() {
        if (_sessionToken.value == null) {
            _usuario.value = null
            _avatarErro.value = null
            _avatarSalvando.value = false
        }
    }

    fun selecionarAvatar(avatar: String) {
        if (avatar !in avataresPermitidos) {
            _avatarErro.value = "Escolha um avatar válido."
            return
        }

        val token = _sessionToken.value

        if (token == null) {
            val application = getApplication<Application>()

            val debug =
                (application.applicationInfo.flags and
                        ApplicationInfo.FLAG_DEBUGGABLE) != 0

            if (debug && _sessionReady.value) {
                _usuario.value = UsuarioResponse(
                    id = "demonstracao",
                    nome = "Username",
                    nomeUsuario = "username",
                    emailUsuario = "",
                    avatar = avatar
                )

                _avatarErro.value = null
            } else {
                _avatarErro.value =
                    "Entre na sua conta para alterar o avatar."
            }

            return
        }

        val pessoa = _usuario.value

        if (pessoa == null) {
            _avatarErro.value =
                "Seu perfil ainda não foi carregado. Tente novamente."
            return
        }

        if (_avatarSalvando.value || _perfilLoading.value) return
        if (_authState.value is AuthState.Loading) return

        if (avatar == (pessoa.avatar ?: "amarelo")) return

        _avatarSalvando.value = true
        _avatarErro.value = null

        viewModelScope.launch {
            try {
                val resposta = ApiClient.authApi.atualizarAvatar(
                    authorization = "Bearer $token",
                    dados = AvatarRequest(
                        avatar = avatar
                    )
                )

                check(resposta.usuario.id == pessoa.id) {
                    "A API retornou um perfil inválido."
                }

                check(resposta.usuario.avatar == avatar) {
                    "A API não confirmou o avatar escolhido."
                }

                if (_sessionToken.value == token) {
                    _usuario.value = resposta.usuario
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: HttpException) {
                if (_sessionToken.value == token) {
                    _avatarErro.value = mensagemErroAvatar(error)
                }
            } catch (error: Exception) {
                if (_sessionToken.value == token) {
                    _avatarErro.value =
                        "Não foi possível confirmar o salvamento do avatar. " +
                                "Confira a conexão e tente novamente."
                }
            } finally {
                if (_sessionToken.value == token) {
                    _avatarSalvando.value = false
                }
            }
        }
    }

    fun logout(
        onSuccess: () -> Unit
    ) {
        if (_authState.value is AuthState.Loading) return

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            try {
                encerrarSessao()
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _authState.value = AuthState.Error(
                    "Não foi possível encerrar a sessão. Tente novamente."
                )
                return@launch
            }

            onSuccess()
        }
    }

    private suspend fun encerrarSessao() {
        withContext(Dispatchers.IO) {
            sessionStore.limpar()
        }

        _sessionToken.value = null
        _usuario.value = null
        _perfilErro.value = null
        _avatarErro.value = null
        _avatarSalvando.value = false
        _authState.value = AuthState.Idle
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }

    private fun mensagemErroAvatar(
        error: HttpException
    ): String {
        val detalhe = extrairMensagemApi(error)

        val mensagem = when (error.code()) {
            401 ->
                "A API não aceitou a autenticação para trocar o avatar. " +
                        "Se a sessão expirou, saia e entre novamente."

            403 ->
                "A API não permitiu alterar o avatar deste usuário."

            404 ->
                "A API retornou 404 ao salvar o avatar. " +
                        "Confira a rota PATCH /api/usuario/me/avatar no backend."

            405 ->
                "A API não aceitou o método PATCH para atualizar o avatar."

            else ->
                "Não foi possível salvar o avatar (${error.code()})."
        }

        return if (detalhe.isNullOrBlank()) {
            mensagem
        } else {
            "$mensagem $detalhe"
        }
    }

    private fun extrairMensagemApi(
        error: HttpException
    ): String? {
        return runCatching {
            val corpo = error.response()
                ?.errorBody()
                ?.string()

            val json = JSONObject(corpo ?: "{}")

            json.optString("error")
                .takeIf { it.isNotBlank() }
                ?: json.optString("message")
                    .takeIf { it.isNotBlank() }
        }.getOrNull()
    }

    private fun mensagemErro(
        error: Exception
    ): String {
        return when (error) {
            is HttpException -> {
                extrairMensagemApi(error)
                    ?: "Não foi possível concluir a solicitação (${error.code()})."
            }

            is IOException ->
                "Não foi possível acessar a API ou salvar a sessão."

            else ->
                "Não foi possível concluir a solicitação."
        }
    }
}