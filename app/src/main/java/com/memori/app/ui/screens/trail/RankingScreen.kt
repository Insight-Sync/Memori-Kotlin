package com.memori.app.ui.screens.trail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.memori.app.ui.theme.RedPrimary
import kotlinx.coroutines.CancellationException

@Composable
fun RankingScreen(onBackToHome: () -> Unit) {
    val authViewModel = LocalAuthViewModel.current
    val token by authViewModel.sessionToken.collectAsState()

    var resposta by remember {
        mutableStateOf<RankingTemposResponse?>(null)
    }

    var loading by remember {
        mutableStateOf(true)
    }

    var erro by remember {
        mutableStateOf<String?>(null)
    }

    var atualizar by remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(token, atualizar) {
        loading = true
        erro = null
        resposta = null

        try {
            val tokenAtual = token

            if (tokenAtual == null) {
                erro = "Entre novamente para consultar o ranking."
            } else {
                resposta = RankingClient.api.buscarRanking(
                    authorization = "Bearer $tokenAtual"
                )
            }
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            erro = mensagemErroRanking(error)
        } finally {
            loading = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Ranking",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = RedPrimary
                        )

                        Text(
                            text = "Quebra-cabeça do KKKK",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }

                    IconButton(
                        onClick = { atualizar++ },
                        enabled = !loading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Atualizar ranking",
                            tint = RedPrimary
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Melhor tempo de cada pessoa",
                    fontSize = 13.sp,
                    color = Color.Gray
                )

                Spacer(Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    val dados = resposta
                    val mensagem = erro

                    when {
                        loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center),
                                color = RedPrimary
                            )
                        }

                        mensagem != null -> {
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = mensagem,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.error
                                )

                                TextButton(
                                    onClick = { atualizar++ }
                                ) {
                                    Text("Tentar novamente")
                                }
                            }
                        }

                        dados == null || dados.rankings.isEmpty() -> {
                            Text(
                                text = "Ainda não há resultados.\n" +
                                        "Conclua o desafio para participar!",
                                modifier = Modifier.align(Alignment.Center),
                                textAlign = TextAlign.Center,
                                color = Color.Gray
                            )
                        }

                        else -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                item {
                                    val meu = dados.meuResultado

                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        color = Color(0xFFFFF3E0)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp)
                                        ) {
                                            Text(
                                                text = "Seu melhor resultado",
                                                fontWeight = FontWeight.Bold,
                                                color = RedPrimary
                                            )

                                            Spacer(Modifier.height(4.dp))

                                            Text(
                                                text = if (meu != null) {
                                                    "#${meu.posicao} • " +
                                                            formatarTempo(meu.tempoSegundos)
                                                } else {
                                                    "Você ainda não concluiu este desafio."
                                                },
                                                color = Color.DarkGray
                                            )
                                        }
                                    }
                                }

                                items(
                                    items = dados.rankings,
                                    key = { it.usuarioId }
                                ) { resultado ->
                                    RankingTempoItem(
                                        resultado = resultado,
                                        souEu = resultado.usuarioId ==
                                                dados.meuResultado?.usuarioId
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = onBackToHome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RedPrimary
                    )
                ) {
                    Text("Voltar ao início")
                }
            }
        }
    }
}

@Composable
private fun RankingTempoItem(
    resultado: RankingTempo,
    souEu: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (souEu) {
            Color(0xFFFFF8E1)
        } else {
            Color(0xFFF9F9F9)
        },
        border = if (souEu) {
            BorderStroke(1.dp, RedPrimary)
        } else {
            null
        }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (resultado.posicao == 1) {
                    "👑"
                } else {
                    "#${resultado.posicao}"
                },
                fontWeight = FontWeight.Bold,
                color = RedPrimary,
                fontSize = 20.sp,
                modifier = Modifier.width(44.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = resultado.nomeUsuario +
                            if (souEu) " (Você)" else "",
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = resultado.nome,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.width(8.dp))

            Text(
                text = formatarTempo(resultado.tempoSegundos),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = RedPrimary
            )
        }
    }
}