package com.memori.app.ui.screens.trail

import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import android.content.pm.ApplicationInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.SystemClock
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.memori.app.R
import com.memori.app.ui.screens.auth.LocalAuthViewModel
import java.util.Collections
import java.util.UUID
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay

fun splitBitmap(source: Bitmap): List<Bitmap> {
    val tileW = source.width / 3
    val tileH = source.height / 3

    return (0 until 9).map { index ->
        Bitmap.createBitmap(
            source,
            (index % 3) * tileW,
            (index / 3) * tileH,
            tileW,
            tileH
        )
    }
}

fun isSolved(tiles: List<Int>): Boolean {
    return tiles == (0..8).toList()
}

private fun novoTabuleiro(): List<Int> {
    val pecas = (0..8).toMutableList()
    val indices = pecas.indices.shuffled().take(2)
    Collections.swap(pecas, indices[0], indices[1])
    return pecas.toList()
}

@Composable
fun PuzzleScreen(
    onPuzzleCompleted: (Int) -> Unit,
    onExit: () -> Unit
) {
    val context = LocalContext.current
    val coral = Color(0xFFFF666B)
    val debug = (context.applicationInfo.flags and
        ApplicationInfo.FLAG_DEBUGGABLE) != 0
    var finalizarSolicitado by rememberSaveable { mutableStateOf(false) }

    val authViewModel = LocalAuthViewModel.current
    val token by authViewModel.sessionToken.collectAsState()

    val tileBitmaps = remember(context) {
        val source = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.kkkk_puzzle
        )

        val side = minOf(source.width, source.height)

        val square = Bitmap.createBitmap(
            source,
            0,
            0,
            side,
            side
        )

        splitBitmap(square).map { it.asImageBitmap() }
    }

    var tiles by rememberSaveable(
        stateSaver = listSaver<List<Int>, Int>(
            save = { it },
            restore = { it.toList() }
        )
    ) {
        mutableStateOf(novoTabuleiro())
    }

    var inicio by rememberSaveable {
        mutableStateOf(SystemClock.elapsedRealtime())
    }

    var tentativaId by rememberSaveable {
        mutableStateOf(UUID.randomUUID().toString())
    }

    var tempoFinal by rememberSaveable {
        mutableStateOf<Int?>(null)
    }

    var reenvio by remember {
        mutableIntStateOf(0)
    }

    var salvando by remember {
        mutableStateOf(false)
    }

    var erro by remember {
        mutableStateOf<String?>(null)
    }

    var tempoExibido by remember {
        mutableIntStateOf(0)
    }

    var navegando by remember {
        mutableStateOf(false)
    }

    var indiceArrastado by remember {
        mutableIntStateOf(-1)
    }

    var deslocamento by remember {
        mutableStateOf(Offset.Zero)
    }

    val trocarPecas: (Int, Int) -> Unit = { origem, destino ->
        if (
            tempoFinal == null &&
            origem in 0..8 &&
            destino in 0..8 &&
            origem != destino
        ) {
            val novasPecas = tiles.toMutableList()
            Collections.swap(novasPecas, origem, destino)
            tiles = novasPecas.toList()

            if (isSolved(novasPecas)) {
                val duracao = (
                        SystemClock.elapsedRealtime() - inicio
                        ).coerceAtLeast(0L)

                tempoFinal = (
                        (duracao + 999L) / 1000L
                        ).coerceAtLeast(1L).toInt()
            }
        }
    }
    val trocarPecasAtual by rememberUpdatedState(trocarPecas)

    BackHandler { if (!salvando && !navegando) onExit() }

    LaunchedEffect(inicio, tempoFinal) {
        val final = tempoFinal

        if (final != null) {
            tempoExibido = final
        } else {
            while (true) {
                tempoExibido = (
                        (SystemClock.elapsedRealtime() - inicio)
                            .coerceAtLeast(0L) / 1000L
                        ).toInt()

                delay(200)
            }
        }
    }

    LaunchedEffect(tempoFinal, tentativaId, reenvio, token, finalizarSolicitado) {
        if (!finalizarSolicitado) return@LaunchedEffect
        val segundos = tempoFinal ?: return@LaunchedEffect

        val tokenAtual = token

        if (tokenAtual == null) {
            if (debug && !navegando) {
                navegando = true
                onPuzzleCompleted(segundos)
            } else {
                erro = "Entre novamente para registrar o resultado."
            }
            return@LaunchedEffect
        }

        salvando = true
        erro = null

        try {
            val resposta = RankingClient.api.salvarResultado(
                authorization = "Bearer $tokenAtual",
                dados = ResultadoRequest(
                    desafioId = "kkkk-puzzle",
                    tentativaId = tentativaId,
                    tempoSegundos = segundos
                )
            )

            check(resposta.tentativa.tentativaId == tentativaId)
            check(resposta.tentativa.tempoSegundos == segundos)

            if (!navegando) {
                navegando = true
                onPuzzleCompleted(segundos)
            }
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            erro = mensagemErroRanking(error)
        } finally {
            salvando = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    state = rememberScrollState(),
                    enabled = indiceArrastado == -1
                )
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                IconButton(
                    onClick = onExit,
                    enabled = !salvando && !navegando,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Image(
                        painter = painterResource(R.drawable.icon_voltar),
                        contentDescription = "Voltar ao mapa",
                        modifier = Modifier.size(28.dp)
                    )
                }

                Surface(
                    modifier = Modifier.align(Alignment.Center),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    border = BorderStroke(1.5.dp, coral)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(R.drawable.cronometro),
                            contentDescription = null,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = formatarTempo(tempoExibido),
                            color = Color(0xFF888888),
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            Text(
                text = "Reconstrua a Lanterna",
                fontSize = 20.sp,
                color = coral,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(64.dp))

            Surface(
                modifier = Modifier
                    .widthIn(max = 360.dp)
                    .fillMaxWidth()
                    .aspectRatio(1f),
                shape = RoundedCornerShape(0.dp),
                color = coral,
                border = BorderStroke(2.dp, coral)
            ) {
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(1.dp)
                ) {
                    val tamanhoPeca = maxWidth / 3
                    val tamanhoPx = with(LocalDensity.current) {
                        tamanhoPeca.toPx()
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(
                                tamanhoPx,
                                tempoFinal,
                                tentativaId
                            ) {
                                if (tempoFinal != null) {
                                    return@pointerInput
                                }

                                var origem = -1
                                var pontoInicial = Offset.Zero
                                var movimento = Offset.Zero

                                fun indiceNaPosicao(
                                    posicao: Offset
                                ): Int {
                                    val limite = tamanhoPx * 3

                                    if (
                                        posicao.x < 0f ||
                                        posicao.y < 0f ||
                                        posicao.x >= limite ||
                                        posicao.y >= limite
                                    ) {
                                        return -1
                                    }

                                    val coluna = floor(
                                        posicao.x / tamanhoPx
                                    ).toInt()

                                    val linha = floor(
                                        posicao.y / tamanhoPx
                                    ).toInt()

                                    return linha * 3 + coluna
                                }

                                detectDragGestures(
                                    onDragStart = { posicao ->
                                        origem = indiceNaPosicao(
                                            posicao
                                        )

                                        pontoInicial = posicao
                                        movimento = Offset.Zero
                                        deslocamento = Offset.Zero
                                        indiceArrastado = origem
                                    },
                                    onDrag = { change, quantidade ->
                                        change.consume()

                                        if (origem in 0..8) {
                                            movimento += quantidade
                                            deslocamento = movimento
                                        }
                                    },
                                    onDragEnd = {
                                        val destino = indiceNaPosicao(
                                            pontoInicial + movimento
                                        )

                                        trocarPecasAtual(
                                            origem,
                                            destino
                                        )

                                        origem = -1
                                        indiceArrastado = -1
                                        deslocamento = Offset.Zero
                                    },
                                    onDragCancel = {
                                        origem = -1
                                        indiceArrastado = -1
                                        deslocamento = Offset.Zero
                                    }
                                )
                            }
                    ) {
                        for (index in 0..8) {
                            val value = tiles[index]
                            val arrastando = index == indiceArrastado

                            val movimentoAtual = if (arrastando) {
                                deslocamento
                            } else {
                                Offset.Zero
                            }

                            Box(
                                modifier = Modifier
                                    .offset {
                                        IntOffset(
                                            x = (
                                                    (index % 3) * tamanhoPx +
                                                            movimentoAtual.x
                                                    ).roundToInt(),
                                            y = (
                                                    (index / 3) * tamanhoPx +
                                                            movimentoAtual.y
                                                    ).roundToInt()
                                        )
                                    }
                                    .size(tamanhoPeca)
                                    .zIndex(
                                        if (arrastando) 1f else 0f
                                    )
                                    .border(1.dp, coral)
                                    .padding(1.dp)
                                    .background(Color.White)
                            ) {
                                Image(
                                    bitmap = tileBitmaps[value],
                                    contentDescription = "Peça ${value + 1}",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.FillBounds
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(64.dp))

            BotaoDesenhado(
                texto = if (erro != null) "Tentar finalizar novamente" else "Finalizar",
                enabled = tempoFinal != null && !salvando && !navegando,
                carregando = salvando,
                onClick = {
                    if (tempoFinal != null && !salvando && !navegando) {
                        erro = null
                        if (finalizarSolicitado) reenvio++
                        finalizarSolicitado = true
                    }
                },
                modifier = Modifier.widthIn(max = 320.dp).fillMaxWidth()
            )

            erro?.let {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun BotaoDesenhado(
    texto: String,
    enabled: Boolean,
    carregando: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    manterTextoDaImagem: Boolean = false
) {
    Box(
        modifier = modifier
            .aspectRatio(663f / 174f)
            .alpha(if (enabled || carregando) 1f else 0.5f)
            .clip(RoundedCornerShape(100.dp))
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .semantics { contentDescription = texto },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.btn_padrao),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        if (!manterTextoDaImagem || carregando) {
            Box(
                modifier = Modifier.fillMaxWidth(0.72f).fillMaxHeight(0.72f)
                    .background(Color(0xFFFF666B), RoundedCornerShape(100.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (carregando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(texto, color = Color.White, fontSize = 18.sp)
                }
            }
        }
    }
}
