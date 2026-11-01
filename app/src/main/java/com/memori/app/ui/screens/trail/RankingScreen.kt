package com.memori.app.ui.screens.trail

import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.memori.app.R
import com.memori.app.ui.screens.auth.LocalAuthViewModel
import com.memori.app.ui.screens.home.DestinoMenuMemori
import com.memori.app.ui.screens.home.MenuMemori
import com.memori.app.ui.screens.profile.AvatarImagem
import kotlinx.coroutines.CancellationException
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

private val FonteTituloRanking = FontFamily(
    Font(R.font.pangolin_regular)
)

private val FonteNomeRanking = FontFamily(
    Font(R.font.louis_george_cafe_regular)
)

private val FonteDetalheRanking = FontFamily(
    Font(R.font.manjari_regular)
)

private val CoralRanking = Color(0xFFFF6B6B)
private val CinzaRanking = Color(0xFF8C8C8C)
private val AmareloRanking = Color(0xFFFFD83D)

object UltimaJogada {
    var tempoSegundos: Int? = null
}

@Composable
fun RankingScreen(onBackToHome: () -> Unit) {
    BackHandler(onBack = onBackToHome)

    val context = LocalContext.current
    val viewModel = LocalAuthViewModel.current
    val token by viewModel.sessionToken.collectAsState()
    val usuario by viewModel.usuario.collectAsState()

    val debug =
        (context.applicationInfo.flags and
                ApplicationInfo.FLAG_DEBUGGABLE) != 0

    val demonstracao = token == null && debug

    var resposta by remember(token) {
        mutableStateOf<RankingTemposResponse?>(null)
    }

    var carregando by remember(token) {
        mutableStateOf(false)
    }

    var erro by remember(token) {
        mutableStateOf<String?>(null)
    }

    var atualizacao by remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(token, atualizacao) {
        resposta = null
        erro = null
        carregando = false

        if (demonstracao) {
            return@LaunchedEffect
        }

        val credencial = token

        if (credencial == null) {
            erro = "Entre na sua conta para consultar o ranking."
            return@LaunchedEffect
        }

        carregando = true

        try {
            resposta = RankingClient.api.buscarRanking(
                "Bearer $credencial"
            )
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            erro = mensagemErroRanking(error)
        } finally {
            carregando = false
        }
    }

    val meuId = usuario?.id ?: resposta?.meuResultado?.usuarioId

    val resultados = if (demonstracao) {
        listOf(
            RankingTempo(
                usuarioId = "demonstracao",
                nome = "Username",
                nomeUsuario = "username",
                tempoSegundos = 40,
                concluidoEm = "",
                posicao = 1,
                avatar = "vermelho"
            ),
            RankingTempo(
                usuarioId = "exemplo-2",
                nome = "Jogador 2",
                nomeUsuario = "jogador2",
                tempoSegundos = 55,
                concluidoEm = "",
                posicao = 2,
                avatar = "amarelo"
            ),
            RankingTempo(
                usuarioId = "exemplo-3",
                nome = "Jogador 3",
                nomeUsuario = "jogador3",
                tempoSegundos = 70,
                concluidoEm = "",
                posicao = 3,
                avatar = "azul"
            )
        )
    } else {
        resposta?.rankings
            .orEmpty()
            .sortedBy { it.posicao }
            .map { pessoa ->
                if (pessoa.usuarioId == meuId) {
                    pessoa.copy(
                        avatar = usuario?.avatar ?: pessoa.avatar
                    )
                } else {
                    pessoa
                }
            }
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val alturaCard = (maxHeight - 230.dp)
            .coerceIn(410.dp, 680.dp)

        FundoRanking()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.48f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(
                    start = 28.dp,
                    end = 28.dp,
                    top = 32.dp,
                    bottom = 148.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFFFAFAFA),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.5.dp, CoralRanking),
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = 6.dp,
                            vertical = 3.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AvatarImagem(
                            usuario?.avatar,
                            Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                        )

                        Spacer(Modifier.width(4.dp))

                        Text(
                            text = usuario?.nome
                                ?.substringBefore(" ")
                                ?: if (demonstracao) {
                                    "Username"
                                } else {
                                    "Perfil"
                                },
                            color = CinzaRanking,
                            fontFamily = FonteDetalheRanking,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            letterSpacing = 0.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.widthIn(max = 110.dp)
                        )
                    }
                }

                Surface(
                    color = Color(0xFFFAFAFA),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.5.dp, CoralRanking),
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 3.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "10",
                            color = CinzaRanking,
                            fontFamily = FonteDetalheRanking,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            letterSpacing = 0.sp
                        )

                        Spacer(Modifier.width(5.dp))

                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Estrelas",
                            tint = AmareloRanking,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            Box(
                modifier = Modifier
                    .widthIn(max = 380.dp)
                    .fillMaxWidth()
                    .height(alturaCard)
            ) {
                Image(
                    painter = painterResource(R.drawable.card_body),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            start = 22.dp,
                            end = 22.dp,
                            top = 28.dp,
                            bottom = 48.dp
                        )
                ) {
                    Text(
                        text = "Ranking",
                        color = CinzaRanking,
                        fontFamily = FonteTituloRanking,
                        fontWeight = FontWeight.Normal,
                        fontSize = 22.sp,
                        lineHeight = 26.sp,
                        letterSpacing = 0.sp
                    )

                    when {
                        carregando -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = CoralRanking
                                )
                            }
                        }

                        erro != null -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = erro.orEmpty(),
                                    color = CoralRanking,
                                    fontFamily = FonteDetalheRanking,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                    letterSpacing = 0.sp,
                                    textAlign = TextAlign.Center
                                )

                                TextButton(
                                    onClick = { atualizacao++ }
                                ) {
                                    Text(
                                        text = "Tentar novamente",
                                        color = CoralRanking,
                                        fontFamily = FonteTituloRanking,
                                        fontWeight = FontWeight.Normal
                                    )
                                }
                            }
                        }

                        resultados.isEmpty() -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Ainda não há resultados.\nConclua a quest para participar!",
                                    color = CinzaRanking,
                                    fontFamily = FonteDetalheRanking,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                    letterSpacing = 0.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        else -> {
                            val primeiro = resultados.first()

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .verticalScroll(rememberScrollState()),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "#${primeiro.posicao.toString().padStart(2, '0')} PRIMEIRO LUGAR",
                                    color = CoralRanking,
                                    fontFamily = FonteTituloRanking,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 22.sp,
                                    lineHeight = 25.sp,
                                    letterSpacing = 0.sp,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Text(
                                    text = formatarTempo(
                                        primeiro.tempoSegundos
                                    ) + " h",
                                    color = CinzaRanking,
                                    fontFamily = FonteDetalheRanking,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp,
                                    lineHeight = 14.sp,
                                    letterSpacing = 0.sp,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Image(
                                    painter = painterResource(
                                        R.drawable.coroa
                                    ),
                                    contentDescription = "Primeiro lugar",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.size(34.dp)
                                )

                                Surface(
                                    modifier = Modifier.size(150.dp),
                                    shape = CircleShape,
                                    color = Color.White,
                                    border = BorderStroke(
                                        3.dp,
                                        AmareloRanking
                                    ),
                                    shadowElevation = 2.dp
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AvatarImagem(
                                            primeiro.avatar,
                                            Modifier
                                                .size(88.dp)
                                                .clip(CircleShape)
                                        )
                                    }
                                }

                                Spacer(Modifier.height(5.dp))

                                Text(
                                    text = primeiro.nomeUsuario,
                                    color = CinzaRanking,
                                    fontFamily = FonteNomeRanking,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 22.sp,
                                    lineHeight = 26.sp,
                                    letterSpacing = 0.sp,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(Modifier.height(6.dp))

                                HorizontalDivider(
                                    modifier = Modifier.fillMaxWidth(0.86f),
                                    thickness = 1.dp,
                                    color = Color(0xFFC8C8C8)
                                )

                                Spacer(Modifier.height(12.dp))

                                resultados.drop(1).forEach { pessoa ->
                                    LinhaRanking(pessoa)

                                    Spacer(Modifier.height(12.dp))
                                }

                                val meuResultado = resposta?.meuResultado

                                if (
                                    !demonstracao &&
                                    meuResultado != null &&
                                    resultados.none {
                                        it.usuarioId == meuResultado.usuarioId
                                    }
                                ) {
                                    HorizontalDivider(
                                        color = Color(0xFFC8C8C8)
                                    )

                                    Spacer(Modifier.height(12.dp))

                                    LinhaRanking(
                                        meuResultado.copy(
                                            avatar = usuario?.avatar
                                                ?: meuResultado.avatar
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                Image(
                    painter = painterResource(R.drawable.estrela_verde),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 18.dp)
                        .size(58.dp)
                )
            }
        }

        MenuMemori(
            selecionado = "map",
            avatar = usuario?.avatar,
            onNavigate = { destino ->
                DestinoMenuMemori.solicitar(destino)
                onBackToHome()
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 24.dp)
                .widthIn(max = 320.dp)
                .fillMaxWidth(0.82f)
        )
    }
}

@Composable
private fun LinhaRanking(pessoa: RankingTempo) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(52.dp),
            shape = CircleShape,
            color = Color.White,
            border = BorderStroke(1.5.dp, CoralRanking),
            shadowElevation = 2.dp
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                AvatarImagem(
                    pessoa.avatar,
                    Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "#${pessoa.posicao.toString().padStart(2, '0')}",
                color = CoralRanking,
                fontFamily = FonteTituloRanking,
                fontWeight = FontWeight.Normal,
                fontSize = 22.sp,
                lineHeight = 25.sp,
                letterSpacing = 0.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = pessoa.nomeUsuario,
                    color = CinzaRanking,
                    fontFamily = FonteNomeRanking,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    letterSpacing = 0.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 90.dp)
                )

                Canvas(
                    modifier = Modifier
                        .weight(1f)
                        .height(12.dp)
                        .padding(horizontal = 2.dp)
                ) {
                    var x = 0f

                    while (x < size.width) {
                        drawCircle(
                            color = CinzaRanking,
                            radius = 0.6.dp.toPx(),
                            center = Offset(
                                x,
                                size.height * 0.7f
                            )
                        )

                        x += 3.dp.toPx()
                    }
                }

                Text(
                    text = formatarTempo(pessoa.tempoSegundos) + " h",
                    color = CinzaRanking,
                    fontFamily = FonteDetalheRanking,
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp,
                    letterSpacing = 0.sp,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun FundoRanking() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val map = remember(context) {
        Configuration.getInstance().load(
            context,
            context.getSharedPreferences(
                "osmdroid",
                Context.MODE_PRIVATE
            )
        )

        Configuration.getInstance().userAgentValue = context.packageName

        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(false)
            setBuiltInZoomControls(false)
            controller.setZoom(18.5)
            controller.setCenter(
                GeoPoint(-24.487632, -47.840771)
            )
        }
    }

    DisposableEffect(map, lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> map.onResume()
                Lifecycle.Event.ON_PAUSE -> map.onPause()
                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        if (
            lifecycleOwner.lifecycle.currentState
                .isAtLeast(Lifecycle.State.RESUMED)
        ) {
            map.onResume()
        }

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            map.onPause()
            map.onDetach()
        }
    }

    AndroidView(
        factory = { map },
        modifier = Modifier.fillMaxSize()
    )
}