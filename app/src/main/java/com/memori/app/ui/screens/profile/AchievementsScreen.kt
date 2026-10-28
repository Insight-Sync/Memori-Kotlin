package com.memori.app.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.memori.app.R
import com.memori.app.ui.screens.auth.LocalAuthViewModel
import com.memori.app.ui.screens.trail.RankingClient
import com.memori.app.ui.screens.trail.RankingTempo
import com.memori.app.ui.screens.trail.formatarTempo
import com.memori.app.ui.screens.trail.mensagemErroRanking
import kotlinx.coroutines.CancellationException

@Composable
fun AchievementsScreen() {
    val viewModel = LocalAuthViewModel.current
    val usuario by viewModel.usuario.collectAsState()
    val token by viewModel.sessionToken.collectAsState()

    val coral = Color(0xFFFF666B)

    var resultado by remember(token) {
        mutableStateOf<RankingTempo?>(null)
    }

    var carregando by remember(token) {
        mutableStateOf(true)
    }

    var erro by remember(token) {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(token) {
        carregando = true
        erro = null
        resultado = null

        try {
            val credencial = token

            if (credencial.isNullOrBlank()) {
                erro = "Entre na sua conta para carregar suas conquistas e seu melhor tempo."
            } else {
                resultado = RankingClient.api.buscarRanking(
                    authorization = "Bearer $credencial"
                ).meuResultado
            }
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            erro = mensagemErroRanking(error)
        } finally {
            carregando = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = 20.dp,
                vertical = 24.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                border = BorderStroke(1.5.dp, coral),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 5.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AvatarImagem(
                        avatar = usuario?.avatar,
                        modifier = Modifier.size(26.dp)
                    )

                    Spacer(Modifier.width(6.dp))

                    Text(
                        text = usuario?.nome
                            ?.substringBefore(" ")
                            ?: "Perfil",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.widthIn(max = 130.dp)
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                border = BorderStroke(1.5.dp, coral)
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 5.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "10",
                        color = Color.Gray
                    )

                    Spacer(Modifier.width(4.dp))

                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Estrelas",
                        tint = Color(0xFFFFD83D),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        Text(
            text = "Minhas conquistas",
            color = coral,
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))

        when {
            carregando -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = coral
                    )
                }
            }

            erro != null -> {
                Column(
                    modifier = Modifier
                        .widthIn(max = 340.dp)
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = erro.orEmpty(),
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }

            else -> {
                CartaoLanternas(
                    resultado = resultado
                )
            }
        }
    }
}

@Composable
private fun CartaoLanternas(
    resultado: RankingTempo?
) {
    val coral = Color(0xFFFF666B)
    val tempo = resultado?.tempoSegundos

    val estrelas = when {
        tempo == null -> 0
        tempo < 30 -> 3
        tempo <= 60 -> 2
        else -> 1
    }

    val imagemEstrelas = when (estrelas) {
        3 -> R.drawable.tres_estrelas
        2 -> R.drawable.duas_estrelas
        1 -> R.drawable.uma_estrela
        else -> R.drawable.zero_estrelas
    }

    Card(
        modifier = Modifier
            .widthIn(max = 340.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF9F9F9)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        border = BorderStroke(
            width = 1.dp,
            color = Color(0xFFD6D6D6)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 18.dp,
                    vertical = 24.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(imagemEstrelas),
                contentDescription = "$estrelas de 3 estrelas",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .width(150.dp)
                    .height(44.dp)
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Lanternas Brilhantes",
                color = Color.Gray,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            Image(
                painter = painterResource(R.drawable.lanterna),
                contentDescription = "Lanternas Brilhantes",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = if (resultado == null) {
                    "Ainda não iniciada"
                } else {
                    "Conquistada!"
                },
                color = coral,
                fontSize = 18.sp
            )

            Spacer(Modifier.height(6.dp))

            if (tempo != null) {
                Text(
                    text = "Melhor tempo: ${formatarTempo(tempo)} h",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            } else {
                Text(
                    text = "Conclua a quest para conquistar sua lanterna.",
                    color = Color.Gray,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}