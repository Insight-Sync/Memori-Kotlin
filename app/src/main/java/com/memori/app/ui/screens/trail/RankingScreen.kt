package com.memori.app.ui.screens.trail

import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.memori.app.R
import com.memori.app.ui.screens.auth.LocalAuthViewModel
import com.memori.app.ui.screens.profile.AvatarImagem
import kotlinx.coroutines.CancellationException
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

@Composable
fun RankingScreen(onBackToHome: () -> Unit) {
    val context = LocalContext.current
    val viewModel = LocalAuthViewModel.current
    val token by viewModel.sessionToken.collectAsState()
    val usuario by viewModel.usuario.collectAsState()
    val coral = Color(0xFFFF666B)
    val debug = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
    val demonstracao = token == null && debug

    var resposta by remember(token) { mutableStateOf<RankingTemposResponse?>(null) }
    var carregando by remember(token) { mutableStateOf(false) }
    var erro by remember(token) { mutableStateOf<String?>(null) }
    var atualizacao by remember { mutableIntStateOf(0) }

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
            resposta = RankingClient.api.buscarRanking("Bearer $credencial")
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
            RankingTempo("demonstracao", "Username", "username", 40, "", 1, usuario?.avatar),
            RankingTempo("exemplo-2", "Jogador 2", "jogador2", 55, "", 2, "azul"),
            RankingTempo("exemplo-3", "Jogador 3", "jogador3", 70, "", 3, "vermelho")
        )
    } else {
        resposta?.rankings.orEmpty().sortedBy { it.posicao }.map {
            if (it.usuarioId == meuId) it.copy(avatar = usuario?.avatar ?: it.avatar) else it
        }
    }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val alturaCard = (maxHeight - 150.dp).coerceIn(480.dp, 680.dp)
        FundoRanking()
        Box(Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.36f)))

        Column(
            modifier = Modifier.fillMaxSize().navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(2.dp, coral)
                ) {
                    Row(
                        Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AvatarImagem(usuario?.avatar, Modifier.size(28.dp))
                        Spacer(Modifier.width(5.dp))
                        Text(
                            usuario?.nome?.substringBefore(" ") ?: if (demonstracao) "Username" else "Perfil",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(2.dp, coral)
                ) {
                    Row(
                        Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("10", color = Color.Gray)
                        Spacer(Modifier.width(5.dp))
                        Icon(Icons.Default.Star, "Estrelas", tint = Color(0xFFFFD83D), modifier = Modifier.size(24.dp))
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Box(
                modifier = Modifier.widthIn(max = 380.dp).fillMaxWidth().height(alturaCard)
            ) {
                Image(
                    painter = painterResource(R.drawable.card_body),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )

                Column(
                    modifier = Modifier.fillMaxSize().padding(
                        start = 28.dp, end = 28.dp, top = 30.dp, bottom = 48.dp
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Ranking", color = Color(0xFF777777), fontSize = 20.sp, modifier = Modifier.weight(1f))
                        IconButton(onClick = { atualizacao++ }, enabled = !carregando && !demonstracao) {
                            Icon(Icons.Default.Refresh, "Atualizar ranking", tint = coral)
                        }
                        Spacer(Modifier.width(28.dp))
                    }

                    if (demonstracao) {
                        Text("Prévia · dados de demonstração", color = Color.Gray, fontSize = 11.sp)
                    }

                    when {
                        carregando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = coral)
                        }
                        erro != null -> Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(erro.orEmpty(), color = coral, textAlign = TextAlign.Center)
                            TextButton(onClick = { atualizacao++ }) { Text("Tentar novamente", color = coral) }
                        }
                        resultados.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Ainda não há resultados.\nConclua a quest para participar!", color = Color.Gray, textAlign = TextAlign.Center)
                        }
                        else -> Column(
                            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val primeiro = resultados.first()
                            Text(
                                "#${primeiro.posicao.toString().padStart(2, '0')} PRIMEIRO LUGAR",
                                color = coral, fontSize = 18.sp,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                formatarTempo(primeiro.tempoSegundos),
                                color = Color.Gray, fontSize = 12.sp,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(8.dp))
                            Image(
                                painterResource(R.drawable.coroa),
                                contentDescription = "Primeiro lugar",
                                modifier = Modifier.size(40.dp),
                                contentScale = ContentScale.Fit
                            )
                            Surface(
                                modifier = Modifier.size(138.dp),
                                shape = CircleShape,
                                color = Color.Transparent,
                                border = BorderStroke(3.dp, Color(0xFFFFD83D))
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    AvatarImagem(primeiro.avatar, Modifier.size(90.dp))
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(
                                primeiro.nomeUsuario,
                                color = Color.Gray, fontSize = 18.sp,
                                maxLines = 1, overflow = TextOverflow.Ellipsis
                            )
                            Spacer(Modifier.height(18.dp))
                            resultados.drop(1).forEach { pessoa ->
                                LinhaRanking(pessoa, coral)
                                Spacer(Modifier.height(12.dp))
                            }
                            val meu = resposta?.meuResultado
                            if (!demonstracao && meu != null && resultados.none { it.usuarioId == meu.usuarioId }) {
                                HorizontalDivider(color = Color.LightGray)
                                Spacer(Modifier.height(12.dp))
                                Text("Seu resultado", color = coral, fontSize = 13.sp)
                                LinhaRanking(meu.copy(avatar = usuario?.avatar ?: meu.avatar), coral)
                            }
                        }
                    }
                }

                Image(
                    painterResource(R.drawable.estrela_verde),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.align(Alignment.TopEnd).padding(top = 24.dp).size(58.dp)
                )
            }

            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onBackToHome,
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = coral),
                modifier = Modifier.widthIn(max = 300.dp).fillMaxWidth().height(48.dp)
            ) {
                Text("Voltar ao mapa", color = Color.White)
            }
        }
    }
}

@Composable
private fun LinhaRanking(pessoa: RankingTempo, coral: Color) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(50.dp),
            shape = CircleShape,
            color = Color.White,
            border = BorderStroke(1.5.dp, coral)
        ) {
            Box(contentAlignment = Alignment.Center) {
                AvatarImagem(pessoa.avatar, Modifier.size(34.dp))
            }
        }
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text("#${pessoa.posicao.toString().padStart(2, '0')}", color = coral, fontSize = 18.sp)
            Text(pessoa.nomeUsuario, color = Color.Gray, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Text(formatarTempo(pessoa.tempoSegundos), color = Color.Gray, fontSize = 11.sp)
    }
}

@Composable
private fun FundoRanking() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val map = remember(context) {
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = context.packageName
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(false)
            setBuiltInZoomControls(false)
            controller.setZoom(18.5)
            controller.setCenter(GeoPoint(-24.487632, -47.840771))
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
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) map.onResume()
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            map.onPause()
            map.onDetach()
        }
    }
    AndroidView(factory = { map }, modifier = Modifier.fillMaxSize())
}