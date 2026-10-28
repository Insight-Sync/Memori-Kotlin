package com.memori.app.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.memori.app.ui.screens.auth.LocalAuthViewModel
import com.memori.app.ui.screens.trail.RankingClient
import com.memori.app.ui.screens.trail.RankingTempo
import com.memori.app.ui.screens.trail.formatarTempo
import com.memori.app.ui.screens.trail.mensagemErroRanking
import com.memori.app.ui.theme.RedPrimary
import kotlinx.coroutines.CancellationException

@Composable
fun AchievementsScreen() {
    val viewModel = LocalAuthViewModel.current
    val usuario by viewModel.usuario.collectAsState()
    val token by viewModel.sessionToken.collectAsState()

    val nomeCurto = usuario?.nome
        ?.trim()
        ?.substringBefore(" ")
        ?.takeIf { it.isNotBlank() }
        ?: "Perfil"
    key(token) {
        ConteudoConquistas(
            token = token,
            nomeCurto = nomeCurto,
            avatar = usuario?.avatar
        )
    }
}

@Composable
private fun ConteudoConquistas(
    token: String?,
    nomeCurto: String,
    avatar: String?
) {
    var resultado by remember {
        mutableStateOf<RankingTempo?>(null)
    }

    var carregando by remember {
        mutableStateOf(true)
    }

    var erro by remember {
        mutableStateOf<String?>(null)
    }

    var atualizacao by remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(token, atualizacao) {
        carregando = true
        erro = null
        resultado = null

        try {
            if (token.isNullOrBlank()) {
                erro = "Entre novamente para consultar suas conquistas."
            } else {
                val resposta = RankingClient.api.buscarRanking(
                    authorization = "Bearer $token"
                )

                resultado = resposta.meuResultado
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
            .padding(16.dp),
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
                        avatar = avatar,
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
                border = BorderStroke(1.dp, RedPrimary)
            ) {
                Text(
                    text = when {
                        carregando -> "Carregando..."
                        erro != null -> "Indisponível"
                        resultado != null -> "1 conquista 🏆"
                        else -> "0 conquistas"
                    },
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 8.dp
                    ),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        Text(
            text = "Minhas conquistas",
            color = RedPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(24.dp))

        val conquista = resultado
        val mensagemErro = erro

        when {
            carregando -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 64.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = RedPrimary
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "Buscando suas conquistas...",
                        color = Color.Gray
                    )
                }
            }

            mensagemErro != null -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Não foi possível carregar",
                            color = RedPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )

                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = mensagemErro,
                            textAlign = TextAlign.Center,
                            color = Color.Gray
                        )

                        Spacer(Modifier.height(20.dp))

                        Button(
                            onClick = { atualizacao++ },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RedPrimary
                            )
                        ) {
                            Text("Tentar novamente")
                        }
                    }
                }
            }

            conquista == null -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🏁",
                            fontSize = 64.sp
                        )

                        Spacer(Modifier.height(20.dp))

                        Text(
                            text = "Você ainda não possui conquistas",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = RedPrimary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = "Conclua o quebra-cabeça do KKKK " +
                                    "para registrar sua primeira conquista!",
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            else -> {
                ConquistaKKKKCard(resultado = conquista)
            }
        }

        Spacer(Modifier.height(24.dp))

        OutlinedButton(
            onClick = { atualizacao++ },
            enabled = !carregando,
            shape = RoundedCornerShape(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                tint = RedPrimary
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = "Atualizar conquistas",
                color = RedPrimary
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ConquistaKKKKCard(
    resultado: RankingTempo
) {
    Card(
        modifier = Modifier
            .widthIn(max = 320.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        border = BorderStroke(
            1.dp,
            Color.LightGray.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Desafio concluído",
                color = Color(0xFF2E7D32),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Explorador do KKKK",
                color = Color(0xFF666666),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Quebra-cabeça do Conjunto Monumental KKKK",
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .size(140.dp)
                    .background(
                        Color(0xFFFFC107).copy(alpha = 0.12f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🏛️",
                    fontSize = 80.sp
                )
            }

            Spacer(Modifier.height(32.dp))

            Text(
                text = "#${resultado.posicao} Lugar",
                color = RedPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Posição atual no ranking",
                color = Color.Gray,
                fontSize = 12.sp
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Seu melhor tempo",
                color = Color.Gray,
                fontSize = 14.sp
            )

            Text(
                text = formatarTempo(resultado.tempoSegundos),
                color = RedPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
