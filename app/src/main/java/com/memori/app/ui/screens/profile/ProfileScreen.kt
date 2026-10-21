package com.memori.app.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.memori.app.R
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
    val salvandoAvatar by viewModel.avatarSalvando.collectAsState()
    val erroAvatar by viewModel.avatarErro.collectAsState()

    val avatares = listOf("amarelo", "azul", "vermelho")
    val indiceAvatar = avatares.indexOf(usuario?.avatar)
        .takeIf { it >= 0 } ?: 0
    val avatarAtual = avatares[indiceAvatar]

    val saindo = authState is AuthState.Loading
    val podeTrocarAvatar =
        usuario != null && !loading && !salvandoAvatar && !saindo

    val nomeCurto = usuario?.nome
        ?.trim()
        ?.substringBefore(" ")
        ?.takeIf { it.isNotBlank() }
        ?: "Perfil"

    fun trocarAvatar(direcao: Int) {
        val novoIndice =
            (indiceAvatar + direcao + avatares.size) % avatares.size

        viewModel.selecionarAvatar(avatares[novoIndice])
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 32.dp),
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
                    AvatarImagem(
                        avatar = avatarAtual,
                        modifier = Modifier.size(24.dp)
                    )

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
                    // Pontuação aguardando a definição do grupo.
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
                    .padding(
                        vertical = 32.dp,
                        horizontal = 16.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = { trocarAvatar(-1) },
                        enabled = podeTrocarAvatar
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIos,
                            contentDescription = "Avatar anterior",
                            tint = if (podeTrocarAvatar) {
                                RedPrimary
                            } else {
                                Color.LightGray
                            }
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .widthIn(max = 180.dp)
                            .aspectRatio(1f)
                            .border(4.dp, RedPrimary, CircleShape)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AvatarImagem(
                            avatar = avatarAtual,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    IconButton(
                        onClick = { trocarAvatar(1) },
                        enabled = podeTrocarAvatar
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForwardIos,
                            contentDescription = "Próximo avatar",
                            tint = if (podeTrocarAvatar) {
                                RedPrimary
                            } else {
                                Color.LightGray
                            }
                        )
                    }
                }

                if (salvandoAvatar) {
                    Spacer(Modifier.height(12.dp))

                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = RedPrimary
                    )
                }

                erroAvatar?.let { mensagem ->
                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = mensagem,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        fontSize = 13.sp
                    )
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
                        enabled = !loading && !saindo && !salvandoAvatar
                    ) {
                        Text("Tentar novamente")
                    }
                }

                (authState as? AuthState.Error)?.let { erro ->
                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = erro.message,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = onLogout,
                    enabled = !saindo && !salvandoAvatar,
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

@Composable
fun AvatarImagem(
    avatar: String?,
    modifier: Modifier = Modifier
) {
    val imagem = when (avatar) {
        "azul" -> R.drawable.avatar_azul
        "vermelho" -> R.drawable.avatar_vermelho
        else -> R.drawable.avatar_amarelo
    }

    Image(
        painter = painterResource(id = imagem),
        contentDescription = "Avatar ${avatar ?: "amarelo"}",
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}