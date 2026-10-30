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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.memori.app.ui.theme.Manjari
import com.memori.app.ui.theme.Pangolin
import kotlinx.coroutines.CancellationException
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

private val FonteNomeRanking = FontFamily(
    Font(
        resId = R.font.louis_george_cafe_regular,
        weight = FontWeight.Normal
    )
)

private val CoralRanking = Color(0xFFFF6B6B)
private val CinzaRanking = Color(0xFF8C8C8C)

@Composable
fun RankingScreen(onBackToHome: () -> Unit) {
    BackHandler(onBack = onBackToHome)

    val context = LocalContext.current
    val viewModel = LocalAuthViewModel.current
    val token by viewModel.sessionToken.collectAsState()
    val usuario by viewModel.usuario.collectAsState()

    val debug = (
            context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE
            ) != 0

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

        if (demonstracao) return@LaunchedEffect

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
                "demonstracao",
                "Username",
                "username",
                40,
                "",
                1,
                usuario?.avatar
            ),
            RankingTempo(
                "exemplo-2",
                "Jogador 2",
                "jogador2",
                55,
                "",
                2,
                "azul"
            ),
            RankingTempo(
                "exemplo-3",
                "Jogador 3",
                "jogador3",
                70,
                "",
                3,
                "vermelho"
            )
        )
    } else {
        resposta?.rankings
            .orEmpty()
            .sortedBy { it.posicao }
            .map { resultado ->
                if (resultado.usuarioId == meuId) {
                    resultado.copy(
                        avatar = usuario?.avatar ?: resultado.avatar
                    )
                } else {
                    resultado
                }
            }
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val alturaCard = (maxHeight - 230.dp)
            .coerceIn(360.dp, 680.dp)

        FundoRanking()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.36f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 20.dp,
                    bottom = 140.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.weight(
                        weight = 1f,
                        fill = false
                    ),
                    color = Color.White,
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(2.dp, CoralRanking)
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 4.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AvatarImagem(
                            avatar = usuario?.avatar,
                            modifier = Modifier.size(28.dp)
                        )

                        Spacer(Modifier.width(5.dp))

                        Text(
                            text = usuario?.nome
                                ?.substringBefore(" ")
                                ?: if (demonstracao) {
                                    "Username"
                                } else {
                                    "Perfil"
                                },
                            color = CinzaRanking,
                            fontFamily = Manjari,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            letterSpacing = 0.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(Modifier.width(12.dp))

                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(2.dp, CoralRanking)
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
                            color = CinzaRanking,
                            fontFamily = Manjari,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            letterSpacing = 0.sp
                        )

                        Spacer(Modifier.width(5.dp))

                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Estrelas",
                            tint = Color(0xFFFFD83D),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

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
                            start = 28.dp,
                            end = 28.dp,
                            top = 30.dp,
                            bottom = 48.dp
                        )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ranking",
                            color = CinzaRanking,
                            fontFamily = Pangolin,
                            fontWeight = FontWeight.Normal,
                            fontSize = 22.sp,
                            lineHeight = 22.sp,
                            letterSpacing = 0.sp,
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(
                            onClick = { atualizacao++ },
                            enabled = !carregando && !demonstracao
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Atualizar ranking",
                                tint = CoralRanking
                            )
                        }

                        Spacer(Modifier.width(28.dp))
                    }

                    if (demonstracao) {
                        Text(
                            text = "Prévia · dados de demonstração",
                            color = CinzaRanking,
                            fontFamily = Manjari,
                            fontWeight = FontWeight.Normal,
                            fontSize = 11.sp,
                            letterSpacing = 0.sp
                        )
                    }

                    when {
                        carregando -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = CoralRanking
                                )
                            }
                        }

                        erro != null -> {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = erro.orEmpty(),
                                    color = CoralRanking,
                                    fontFamily = Manjari,
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
                                        fontFamily = Pangolin,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 14.sp,
                                        letterSpacing = 0.sp
                                    )
                                }
                            }
                        }

                        resultados.isEmpty() -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Ainda não há resultados.\nConclua a quest para participar!",
                                    color = CinzaRanking,
                                    fontFamily = Manjari,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                    letterSpacing = 0.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        else -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                val primeiro = resultados.first()

                                Text(
                                    text = "#${
                                        primeiro.posicao.toString()
                                            .padStart(2, '0')
                                    } PRIMEIRO LUGAR",
                                    color = CoralRanking,
                                    fontFamily = Pangolin,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 22.sp,
                                    lineHeight = 22.sp,
                                    letterSpacing = 0.sp,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Text(
                                    text = "${formatarTempo(primeiro.tempoSegundos)} h",
                                    color = CinzaRanking,
                                    fontFamily = Manjari,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    letterSpacing = 0.sp,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(Modifier.height(8.dp))

                                Image(
                                    painter = painterResource(R.drawable.coroa),
                                    contentDescription = "Primeiro lugar",
                                    modifier = Modifier.size(40.dp),
                                    contentScale = ContentScale.Fit
                                )

                                Surface(
                                    modifier = Modifier.size(138.dp),
                                    shape = CircleShape,
                                    color = Color.Transparent,
                                    border = BorderStroke(
                                        3.dp,
                                        Color(0xFFFFD83D)
                                    )
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AvatarImagem(
                                            avatar = primeiro.avatar,
                                            modifier = Modifier.size(90.dp)
                                        )
                                    }
                                }

                                Spacer(Modifier.height(8.dp))

                                Text(
                                    text = primeiro.nomeUsuario,
                                    color = CinzaRanking,
                                    fontFamily = FonteNomeRanking,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 22.sp,
                                    lineHeight = 22.sp,
                                    letterSpacing = 0.sp,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(Modifier.height(18.dp))

                                HorizontalDivider(
                                    modifier = Modifier.fillMaxWidth(0.9f),
                                    thickness = 1.dp,
                                    color = Color(0xFFB8B8B8)
                                )

                                Spacer(Modifier.height(14.dp))

                                resultados.drop(1).forEach { pessoa ->
                                    LinhaRanking(pessoa)
                                    Spacer(Modifier.height(12.dp))
                                }

                                val meu = resposta?.meuResultado

                                if (
                                    !demonstracao &&
                                    meu != null &&
                                    resultados.none {
                                        it.usuarioId == meu.usuarioId
                                    }
                                ) {
                                    HorizontalDivider(
                                        color = Color.LightGray
                                    )

                                    Spacer(Modifier.height(12.dp))

                                    Text(
                                        text = "Seu resultado",
                                        color = CoralRanking,
                                        fontFamily = Pangolin,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 14.sp,
                                        letterSpacing = 0.sp
                                    )

                                    Spacer(Modifier.height(8.dp))

                                    LinhaRanking(
                                        meu.copy(
                                            avatar = usuario?.avatar
                                                ?: meu.avatar
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
                        .padding(top = 24.dp)
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
            modifier = Modifier.size(50.dp),
            shape = CircleShape,
            color = Color.White,
            border = BorderStroke(1.5.dp, CoralRanking)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                AvatarImagem(
                    avatar = pessoa.avatar,
                    modifier = Modifier.size(34.dp)
                )
            }
        }

        Spacer(Modifier.width(10.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "#${pessoa.posicao.toString().padStart(2, '0')}",
                color = CoralRanking,
                fontFamily = Pangolin,
                fontWeight = FontWeight.Normal,
                fontSize = 22.sp,
                lineHeight = 22.sp,
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
                    lineHeight = 16.sp,
                    letterSpacing = 0.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 100.dp)
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
                            radius = 0.7.dp.toPx(),
                            center = Offset(
                                x,
                                size.height * 0.75f
                            )
                        )

                        x += 4.dp.toPx()
                    }
                }

                Text(
                    text = "${formatarTempo(pessoa.tempoSegundos)} h",
                    color = CinzaRanking,
                    fontFamily = Manjari,
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    letterSpacing = 0.sp
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