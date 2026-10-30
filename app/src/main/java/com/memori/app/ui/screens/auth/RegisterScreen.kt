package com.memori.app.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.memori.app.R
import com.memori.app.ui.theme.Manjari
import com.memori.app.ui.theme.Pangolin

@Suppress("UNUSED_PARAMETER")
@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: AuthViewModel = viewModel(),
    onCadastroConcluido: () -> Unit = onNavigateToLogin
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var agreeTerms by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsState()
    val loading = authState is AuthState.Loading

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val nomeFocus = remember { FocusRequester() }
    val emailFocus = remember { FocusRequester() }
    val usuarioFocus = remember { FocusRequester() }
    val senhaFocus = remember { FocusRequester() }

    fun cadastrar() {
        if (loading) return

        if (!agreeTerms) {
            Toast.makeText(
                context,
                "Aceite os termos de uso",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        focusManager.clearFocus()

        viewModel.register(
            email = email,
            pass = password,
            name = name,
            username = username,
            onSuccess = onCadastroConcluido
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
        AuthHeader(
            titulo = "Cadastro",
            onVoltar = {
                if (!loading) {
                    onNavigateToLogin()
                }
            }
        )

        Column(
            modifier = Modifier
                .widthIn(max = 440.dp)
                .fillMaxWidth()
                .padding(horizontal = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RegistrationField(
                label = "Nome completo:",
                value = name,
                placeholder = "Digite o seu nome completo",
                enabled = !loading,
                modifier = Modifier
                    .focusRequester(nomeFocus)
                    .focusProperties {
                        next = emailFocus
                    },
                onValueChange = { name = it }
            )

            RegistrationField(
                label = "E-mail:",
                value = email,
                placeholder = "Digite o seu e-mail",
                keyboardType = KeyboardType.Email,
                enabled = !loading,
                modifier = Modifier
                    .focusRequester(emailFocus)
                    .focusProperties {
                        previous = nomeFocus
                        next = usuarioFocus
                    },
                onValueChange = { email = it }
            )

            RegistrationField(
                label = "Usuário:",
                value = username,
                placeholder = "Digite o seu usuário",
                enabled = !loading,
                modifier = Modifier
                    .focusRequester(usuarioFocus)
                    .focusProperties {
                        previous = emailFocus
                        next = senhaFocus
                    },
                onValueChange = { username = it }
            )

            RegistrationField(
                label = "Senha:",
                value = password,
                placeholder = "Digite a sua senha",
                isPassword = true,
                enabled = !loading,
                imeAction = ImeAction.Done,
                bottomSpacing = 0.dp,
                modifier = Modifier
                    .focusRequester(senhaFocus)
                    .focusProperties {
                        previous = usuarioFocus
                    },
                onValueChange = { password = it }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = agreeTerms,
                    onClick = {
                        agreeTerms = !agreeTerms
                    },
                    enabled = !loading,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = AuthCoral,
                        unselectedColor = AuthCoral
                    )
                )

                Text(
                    text = "Concordo com os termos de uso",
                    fontFamily = Pangolin,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    letterSpacing = 0.sp,
                    color = AuthCoral
                )
            }

            Button(
                onClick = { cadastrar() },
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
                        text = "Cadastrar",
                        fontFamily = Pangolin,
                        fontWeight = FontWeight.Normal,
                        fontSize = 18.sp,
                        letterSpacing = 0.sp
                    )
                }
            }

            TextButton(
                onClick = onNavigateToLogin,
                enabled = !loading,
                contentPadding = PaddingValues(
                    horizontal = 8.dp,
                    vertical = 0.dp
                )
            ) {
                Text(
                    text = "Já tenho uma conta!",
                    color = AuthCoral,
                    fontFamily = Pangolin,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    letterSpacing = 0.sp,
                    textDecoration = TextDecoration.Underline
                )
            }

            Spacer(Modifier.height(8.dp))

            Image(
                painter = painterResource(R.drawable.logo_rabiscado),
                contentDescription = "MEMORI",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(56.dp)
            )

            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
fun RegistrationField(
    label: String,
    value: String,
    placeholder: String,
    isPassword: Boolean = false,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Next,
    onDone: (() -> Unit)? = null,
    bottomSpacing: Dp = 16.dp,
    onValueChange: (String) -> Unit
) {
    var senhaVisivel by remember {
        mutableStateOf(false)
    }

    val focusManager = LocalFocusManager.current

    LaunchedEffect(enabled) {
        if (!enabled) {
            senhaVisivel = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = bottomSpacing)
    ) {
        Text(
            text = label,
            color = AuthCoral,
            fontFamily = Pangolin,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 14.sp,
            letterSpacing = 0.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 6.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier
                .fillMaxWidth()
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
                Text(
                    text = placeholder,
                    fontFamily = Manjari,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    lineHeight = 14.sp,
                    letterSpacing = 0.sp,
                    color = Color(0xFFD9D9D9)
                )
            },
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = Manjari,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 14.sp,
                letterSpacing = 0.sp
            ),
            shape = RoundedCornerShape(28.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AuthCoral,
                unfocusedBorderColor = AuthCoral,
                cursorColor = AuthCoral,
                focusedContainerColor = AuthBackground,
                unfocusedContainerColor = AuthBackground,
                disabledContainerColor = AuthBackground
            ),
            visualTransformation = if (
                isPassword && !senhaVisivel
            ) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            trailingIcon = if (isPassword) {
                {
                    IconButton(
                        onClick = {
                            senhaVisivel = !senhaVisivel
                        },
                        enabled = enabled,
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
                            },
                            tint = AuthCoral
                        )
                    }
                }
            } else {
                null
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = if (isPassword) {
                    KeyboardType.Password
                } else {
                    keyboardType
                },
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(
                        FocusDirection.Next
                    )
                },
                onDone = {
                    focusManager.clearFocus()
                    onDone?.invoke()
                }
            ),
            singleLine = true,
            enabled = enabled
        )
    }
} 