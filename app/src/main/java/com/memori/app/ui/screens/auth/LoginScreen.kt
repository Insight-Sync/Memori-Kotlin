package com.memori.app.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToCadastro: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var senhaVisivel by remember { mutableStateOf(false) }

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
        senhaVisivel = false

        viewModel.login(
            email,
            password,
            onNavigateToHome
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
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "E-mail:",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyLarge
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .focusRequester(emailFocus)
                .focusProperties {
                    next = senhaFocus
                }
                .onPreviewKeyEvent { event ->
                    if (event.key == Key.Tab) {
                        if (event.type == KeyEventType.KeyDown) {
                            focusManager.moveFocus(
                                if (event.isShiftPressed) {
                                    FocusDirection.Previous
                                } else {
                                    FocusDirection.Next
                                }
                            )
                        }

                        true
                    } else {
                        false
                    }
                },
            placeholder = {
                Text("Digite o seu e-mail")
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    senhaFocus.requestFocus()
                }
            ),
            enabled = !loading
        )

        Text(
            text = "Senha:",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyLarge
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
                .focusRequester(senhaFocus)
                .focusProperties {
                    previous = emailFocus
                }
                .onPreviewKeyEvent { event ->
                    if (event.key == Key.Tab) {
                        if (event.type == KeyEventType.KeyDown) {
                            focusManager.moveFocus(
                                if (event.isShiftPressed) {
                                    FocusDirection.Previous
                                } else {
                                    FocusDirection.Next
                                }
                            )
                        }

                        true
                    } else {
                        false
                    }
                },
            placeholder = {
                Text("Digite a sua senha")
            },
            visualTransformation = if (senhaVisivel) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        senhaVisivel = !senhaVisivel
                    },
                    enabled = !loading,
                    modifier = Modifier.focusProperties {
                        canFocus = false
                    }
                ) {
                    Icon(
                        imageVector = if (senhaVisivel) {
                            Icons.Default.VisibilityOff
                        } else {
                            Icons.Default.Visibility
                        },
                        contentDescription = if (senhaVisivel) {
                            "Ocultar senha"
                        } else {
                            "Mostrar senha"
                        }
                    )
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { entrar() }
            ),
            enabled = !loading
        )

        Button(
            onClick = { entrar() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            enabled = !loading
        ) {
            if (loading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = "Entrar",
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Não tem uma conta? ",
                color = Color.Gray,
                fontSize = 14.sp
            )

            TextButton(
                onClick = onNavigateToCadastro,
                enabled = !loading,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Cadastre-se!",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                    fontSize = 14.sp
                )
            }
        }
    }
}