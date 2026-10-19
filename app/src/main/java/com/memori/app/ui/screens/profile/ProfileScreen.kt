package com.memori.app.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.memori.app.ui.screens.auth.AuthState
import com.memori.app.ui.screens.auth.LocalAuthViewModel
import com.memori.app.ui.theme.RedPrimary

@Composable
fun ProfileScreen(onLogout: () -> Unit) {
    val viewModel = LocalAuthViewModel.current

    val usuario by viewModel.usuario.collectAsState()
    val loading by viewModel.perfilLoading.collectAsState()
    val erroPerfil by viewModel.perfilErro.collectAsState()
    val authState by viewModel.authState.collectAsState()

    val saindo = authState is AuthState.Loading
    val nomeCurto = usuario?.nome
        ?.trim()
        ?.substringBefore(" ")
        ?.takeIf { it.isNotBlank() }
        ?: "Perfil"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 190.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.weight(1f, fill = false),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = BorderStroke(1.dp, RedPrimary),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 6.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFEB3B))
                            .border(1.dp, RedPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("😑", fontSize = 12.sp)
                    }

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = nomeCurto,
                        color = RedPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = BorderStroke(1.dp, RedPrimary),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 6.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Pontuação de exemplo, ainda sem integração.
                    Text(
                        text = "10",
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.width(4.dp))

                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Estrelas",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        Text(
            text = "Meu perfil",
            color = RedPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = {},
                        enabled = false
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIos,
                            contentDescription = "Avatar anterior",
                            tint = Color.LightGray
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .border(4.dp, RedPrimary, CircleShape)
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .background(Color(0xFFFFD54F)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("😑", fontSize = 80.sp)

                            Text(
                                text = "💢",
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp),
                                fontSize = 24.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = {},
                        enabled = false
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForwardIos,
                            contentDescription = "Próximo avatar",
                            tint = Color.LightGray
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp),
                        color = RedPrimary
                    )

                    Spacer(Modifier.height(12.dp))
                }

                usuario?.let { pessoa ->
                    Text(
                        text = pessoa.nome,
                        color = RedPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "@${pessoa.nomeUsuario}",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = pessoa.emailUsuario,
                        color = Color.Gray,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }

                erroPerfil?.let { mensagem ->
                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = mensagem,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )

                    TextButton(
                        onClick = { viewModel.atualizarPerfil() },
                        enabled = !loading && !saindo
                    ) {
                        Text("Tentar novamente")
                    }
                }

                val erroAuth = authState as? AuthState.Error

                erroAuth?.let {
                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = it.message,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = onLogout,
                    enabled = !saindo,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RedPrimary
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    if (saindo) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Text(
                            text = "Sair",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}