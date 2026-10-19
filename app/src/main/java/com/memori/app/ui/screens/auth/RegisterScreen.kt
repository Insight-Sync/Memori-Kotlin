package com.memori.app.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
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
import com.memori.app.ui.theme.RedPrimary

@Suppress("UNUSED_PARAMETER")
@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: AuthViewModel = viewModel()
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
            onSuccess = {
                Toast.makeText(
                    context,
                    "Conta criada! Entre com seu e-mail e senha.",
                    Toast.LENGTH_LONG
                ).show()

                onNavigateToLogin()
            }
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .offset(x = (-20).dp, y = (-20).dp)
                .clip(CircleShape)
                .background(
                    Color(0xFFFFD54F).copy(alpha = 0.5f)
                )
        )

        Box(
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.TopEnd)
                .offset(x = 20.dp, y = (-30).dp)
                .clip(CircleShape)
                .background(
                    Color(0xFF81C784).copy(alpha = 0.5f)
                )
        )

        Box(
            modifier = Modifier
                .size(80.dp)
                .offset(x = 100.dp, y = 200.dp)
                .clip(CircleShape)
                .background(
                    Color(0xFF4DB6AC).copy(alpha = 0.3f)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(60.dp))

            Text(
                text = "Cadastro",
                color = RedPrimary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(40.dp))

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
                placeholder = "Mínimo de 8 caracteres",
                isPassword = true,
                enabled = !loading,
                imeAction = ImeAction.Done,
                modifier = Modifier
                    .focusRequester(senhaFocus)
                    .focusProperties {
                        previous = usuarioFocus
                    },
                onValueChange = { password = it }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = agreeTerms,
                    onClick = {
                        agreeTerms = !agreeTerms
                    },
                    enabled = !loading,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = RedPrimary
                    )
                )

                Text(
                    text = "Concordo com os termos de uso",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { cadastrar() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = RedPrimary
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "Cadastrar",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            TextButton(
                onClick = onNavigateToLogin,
                enabled = !loading
            ) {
                Text(
                    text = "Já tenho uma conta!",
                    color = RedPrimary,
                    textDecoration = TextDecoration.Underline,
                    fontSize = 14.sp
                )
            }

            Spacer(Modifier.height(32.dp))

            Row(
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Box(
                    Modifier
                        .size(20.dp)
                        .background(
                            Color(0xFFFF5252),
                            RoundedCornerShape(2.dp)
                        )
                )

                Spacer(Modifier.width(4.dp))

                Box(
                    Modifier
                        .size(20.dp)
                        .background(
                            Color(0xFFFFC107),
                            RoundedCornerShape(2.dp)
                        )
                )

                Spacer(Modifier.width(4.dp))

                Box(
                    Modifier
                        .size(20.dp)
                        .background(
                            Color(0xFF4CAF50),
                            RoundedCornerShape(2.dp)
                        )
                )
            }
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
            .padding(bottom = 12.dp)
    ) {
        Text(
            text = label,
            color = RedPrimary,
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 4.dp)
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
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            },
            shape = RoundedCornerShape(26.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = RedPrimary.copy(
                    alpha = 0.5f
                ),
                focusedBorderColor = RedPrimary
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
                            tint = RedPrimary
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
                }
            ),
            singleLine = true,
            enabled = enabled
        )
    }
}