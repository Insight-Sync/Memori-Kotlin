package com.memori.app.ui.screens.trail

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.SystemClock
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
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

    do {
        pecas.shuffle()
    } while (isSolved(pecas))

    return pecas.toList()
}

@Composable
fun PuzzleScreen(
    onPuzzleCompleted: (Int) -> Unit,
    onExit: () -> Unit
) {
    val context = LocalContext.current
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

    var salvo by rememberSaveable {
        mutableStateOf(false)
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

    // Mantém o gesto usando a versão atual dos dados.
    val trocarPecasAtual by rememberUpdatedState(trocarPecas)

    BackHandler(enabled = tempoFinal != null) {}

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

    LaunchedEffect(tempoFinal, tentativaId, reenvio, token) {
        val segundos = tempoFinal ?: return@LaunchedEffect

        if (salvo) return@LaunchedEffect

        val tokenAtual = token

        if (tokenAtual == null) {
            erro = "Entre novamente para registrar o resultado."
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

            salvo = true
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(56.dp))

            Text(
                text = "Reconstrua o KKKK",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(16.dp))

            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = "⏱️ ${formatarTempo(tempoExibido)}",
                    modifier = Modifier.padding(
                        horizontal = 20.dp,
                        vertical = 8.dp
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Arraste uma peça sobre outra para trocar de lugar.",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            Surface(
                modifier = Modifier
                    .widthIn(max = 312.dp)
                    .fillMaxWidth()
                    .aspectRatio(1f),
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
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
                                    .padding(2.dp)
                                    .clip(RoundedCornerShape(4.dp))
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

            Spacer(Modifier.height(24.dp))

            OutlinedButton(
                onClick = {
                    indiceArrastado = -1
                    deslocamento = Offset.Zero
                    tiles = novoTabuleiro()
                    inicio = SystemClock.elapsedRealtime()
                    tentativaId = UUID.randomUUID().toString()
                    tempoExibido = 0
                    salvo = false
                    erro = null
                },
                enabled = tempoFinal == null &&
                        indiceArrastado == -1,
                shape = RoundedCornerShape(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null
                )

                Spacer(Modifier.width(8.dp))

                Text("Embaralhar e reiniciar tempo")
            }

            Text(
                text = "O desafio termina quando a imagem estiver montada.",
                modifier = Modifier.padding(top = 12.dp),
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))
        }

        IconButton(
            onClick = onExit,
            enabled = tempoFinal == null,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .background(Color.White, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Sair do desafio"
            )
        }
    }

    val segundosFinais = tempoFinal

    if (segundosFinais != null) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text("🎉 Desafio concluído!")
            },
            text = {
                Column {
                    Text(
                        "Você resolveu o quebra-cabeça em " +
                                "${formatarTempo(segundosFinais)}."
                    )

                    Spacer(Modifier.height(16.dp))

                    when {
                        salvando -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp)
                                )

                                Spacer(Modifier.width(12.dp))
                                Text("Salvando seu tempo...")
                            }
                        }

                        salvo -> {
                            Text(
                                text = "Tempo salvo na sua conta!",
                                color = Color(0xFF2E7D32)
                            )
                        }

                        erro != null -> {
                            Text(
                                text = erro.orEmpty(),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            },
            confirmButton = {
                if (salvo) {
                    Button(
                        enabled = !navegando,
                        onClick = {
                            navegando = true
                            onPuzzleCompleted(segundosFinais)
                        }
                    ) {
                        Text("Continuar")
                    }
                } else {
                    Button(
                        enabled = !salvando,
                        onClick = { reenvio++ }
                    ) {
                        Text(
                            if (salvando) {
                                "Salvando..."
                            } else {
                                "Tentar salvar novamente"
                            }
                        )
                    }
                }
            }
        )
    }
}