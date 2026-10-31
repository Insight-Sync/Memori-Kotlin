package com.memori.app.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.memori.app.R

@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToCadastro: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authState by viewModel.authState.collectAsState()
    val loading = authState is AuthState.Loading

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val emailFocus = remember { FocusRequester() }
    val senhaFocus = remember { FocusRequester() }

    fun entrar() {
        if (loading) return

        if (email.isBlank() || password.isEmpty()) {
            Toast.makeText(
                context,
                "Preencha o e-mail e a senha",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        focusManager.clearFocus()

        viewModel.login(
            email = email,
            pass = password,
            onSuccess = onNavigateToHome
        )
    }

    LaunchedEffect(authState) {
        val erro = authState as? AuthState.Error

        if (erro != null) {
            Toast.makeText(
                context,
                erro.message,
                Toast.LENGTH_LONG
            ).show()

            viewModel.resetState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AuthBackground)
            .imePadding()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AuthHeader()


        Spacer(modifier = Modifier.height(40.dp))

        Column(
            modifier = Modifier
                .widthIn(max = 440.dp)
                .fillMaxWidth()
                .padding(horizontal = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RegistrationField(
                label = "E-mail:",
                value = email,
                placeholder = "Digite o seu e-mail",
                keyboardType = KeyboardType.Email,
                enabled = !loading,
                modifier = Modifier
                    .focusRequester(emailFocus)
                    .focusProperties {
                        next = senhaFocus
                    },
                onValueChange = { email = it }
            )

            Spacer(modifier = Modifier.height(12.dp))

            RegistrationField(
                label = "Senha:",
                value = password,
                placeholder = "Digite a sua senha",
                isPassword = true,
                enabled = !loading,
                imeAction = ImeAction.Done,
                modifier = Modifier
                    .focusRequester(senhaFocus)
                    .focusProperties {
                        previous = emailFocus
                    },
                onDone = { entrar() },
                onValueChange = { password = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { entrar() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                enabled = !loading,
                shape = RoundedCornerShape(29.dp),
                border = BorderStroke(3.dp, AuthPink),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AuthCoral,
                    contentColor = Color.White
                )
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "Entrar",
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Não tem uma conta? ",
                    color = AuthCoral,
                    fontSize = 12.sp
                )

                TextButton(
                    onClick = onNavigateToCadastro,
                    enabled = !loading,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Cadastre-se!",
                        color = AuthCoral,
                        fontSize = 12.sp,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = onNavigateToHome,
                enabled = !loading,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Pular Login",
                    color = AuthCoral,
                    fontSize = 14.sp,
                    textDecoration = TextDecoration.Underline
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Divider(
                    modifier = Modifier.weight(1f),
                    color = AuthPink.copy(alpha = 0.4f)
                )

                Text(
                    text = "ou",
                    color = AuthCoral,
                    fontSize = 14.sp
                )

                Divider(
                    modifier = Modifier.weight(1f),
                    color = AuthPink.copy(alpha = 0.4f)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Fazer login com:",
                color = AuthCoral,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(
                    R.drawable.instagram to "Instagram",
                    R.drawable.facebook to "Facebook",
                    R.drawable.google to "Google"
                ).forEach { (imagem, plataforma) ->
                    IconButton(
                        onClick = {
                            Toast.makeText(
                                context,
                                "Login com $plataforma em breve!",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        enabled = !loading,
                        modifier = Modifier
                            .size(48.dp)
                            .border(
                                width = 1.dp,
                                color = AuthCoral,
                                shape = CircleShape
                            )
                    ) {
                        Image(
                            painter = painterResource(imagem),
                            contentDescription = "Entrar com $plataforma",
                            modifier = Modifier.size(34.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}