package com.memori.app.ui.screens.trail

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.memori.app.R
import com.memori.app.ui.theme.RedPrimary
import kotlinx.coroutines.delay

val dialogosIntro = listOf(
    "Olá! Eu sou Yumi",
    "Preciso da sua ajuda!",
    "Perdi as lanternas do Tooro Nagashi...",
    "Uma para a vovó, outra para o vovô...",
    "Elas sairam voando pela praça!",
    "Tenho certeza que uma voou para perto do KKKK!"
)

val dialogosPostAR = listOf(
    "Obrigada! Você encontrou a primeira lanterna!",
    "Sem elas, a vovó ficaria se sentindo sozinha...",
    "Você sabia que elas guiam aqueles que já partiram?",
    "Esse é um costume budista, mas aqui no Vale...",
    "...começou como uma homenagem às vitimas do Rio",
    "e hoje, 60 anos depois, ainda está viva na cidade!",
    "Agora por favor, me ajude a encontrar a outra!"
)

// ---- Posição da Yumi e do balão (ajusta só aqui) ----
private const val YUMI_ALTURA = 0.48f
private const val YUMI_SOBE = 0.13f
private const val BALAO_LARGURA = 0.58f
private const val BALAO_ESQUERDA = 0.32f
private val BALAO_FOLGA = 12.dp

// ---- Ritmo da boca (ajusta só aqui) ----
// Cada par = (ms com a boca ABERTA, ms com a boca FECHADA). Maior = mais lento.
private val BOCA_RITMO = listOf(
    900L to 700L,
    750L to 850L,
    1000L to 650L,
    800L to 800L
)

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun StoryScreen(
    dialogos: List<String>,
    onFinished: () -> Unit
) {
    var dialogoIndex by remember { mutableIntStateOf(0) }
    var textoVisivel by remember { mutableStateOf("") }
    var digitando by remember { mutableStateOf(false) }
    var bocaAberta by remember { mutableStateOf(false) }

    // Digitação do texto
    LaunchedEffect(dialogoIndex) {
        textoVisivel = ""
        digitando = true
        for (char in dialogos[dialogoIndex]) {
            if (!digitando) break
            textoVisivel += char
            delay(35)
        }
        textoVisivel = dialogos[dialogoIndex]
        digitando = false
    }

    // Boca: troca direta entre as duas imagens (sem mistura), em ritmo lento e variado
    LaunchedEffect(digitando) {
        if (digitando) {
            var i = 0
            while (true) {
                val (aberta, fechada) = BOCA_RITMO[i % BOCA_RITMO.size]
                bocaAberta = true
                delay(aberta)
                bocaAberta = false
                delay(fechada)
                i++
            }
        } else {
            bocaAberta = false
        }
    }

    // Respiração suave do personagem enquanto fala
    val escala by animateFloatAsState(
        targetValue = if (digitando) 1.015f else 1f,
        animationSpec = tween(900, easing = FastOutSlowInEasing),
        label = "escala_personagem"
    )

    val balao = painterResource(R.drawable.balao)
    val tamanhoBalao = balao.intrinsicSize
    val proporcao = if (
        tamanhoBalao.width.isFinite() &&
        tamanhoBalao.height.isFinite() &&
        tamanhoBalao.width > 0f &&
        tamanhoBalao.height > 0f
    ) {
        tamanhoBalao.width / tamanhoBalao.height
    } else {
        1.8f
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (digitando) {
                    digitando = false
                } else if (dialogoIndex < dialogos.size - 1) {
                    dialogoIndex++
                } else {
                    onFinished()
                }
            }
    ) {
        val larguraTela = maxWidth
        val alturaYumi = maxHeight * YUMI_ALTURA
        val yumiSobe = maxHeight * YUMI_SOBE

        // Yumi: as duas imagens ficam carregadas, mas só UMA é visível por vez
        Box(
            modifier = Modifier
                .height(alturaYumi)
                .aspectRatio(0.6f)
                .align(Alignment.BottomStart)
                .offset(x = 12.dp, y = -yumiSobe)
                .graphicsLayer {
                    scaleX = escala
                    scaleY = escala
                }
        ) {
            // Boca fechada
            Image(
                painter = painterResource(id = R.drawable.yumi),
                contentDescription = "Yumi",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = if (bocaAberta) 0f else 1f }
            )
            // Boca aberta
            Image(
                painter = painterResource(id = R.drawable.yumi_boca),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = if (bocaAberta) 1f else 0f }
            )
        }

        // Balão
        BoxWithConstraints(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(
                    x = larguraTela * BALAO_ESQUERDA,
                    y = -(alturaYumi + yumiSobe + BALAO_FOLGA)
                )
                .width(larguraTela * BALAO_LARGURA)
                .aspectRatio(proporcao)
        ) {
            Image(
                painter = balao,
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = maxWidth * 0.09f,
                        end = maxWidth * 0.08f,
                        top = maxHeight * 0.08f,
                        bottom = maxHeight * 0.12f
                    ),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Yumi",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = RedPrimary,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                    Text(
                        text = textoVisivel,
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        lineHeight = 20.sp
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${dialogoIndex + 1}/${dialogos.size}",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                    AnimatedVisibility(
                        visible = !digitando,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Text(
                            text = if (dialogoIndex < dialogos.size - 1)
                                "Toque ▶" else "Vamos lá ▶",
                            fontSize = 10.sp,
                            color = RedPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}