package com.memori.app.ui.screens.trail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.memori.app.R
import com.memori.app.ui.theme.RedPrimary
import kotlinx.coroutines.delay

val dialogosIntro = listOf(
    "Olá! Eu sou Yumi, sua guia nessa aventura!",
    "Você está prestes a descobrir os segredos do Conjunto KKKK...",
    "Este lugar é muito especial para o Vale do Ribeira.",
    "Mas antes, preciso da sua ajuda para encontrar as Lanternas Brilhantes!",
    "Vamos começar? Escaneie o marcador no local para dar o primeiro passo!"
)

val dialogosPostAR = listOf(
    "Incrível! Você encontrou o primeiro fragmento!",
    "Agora vem o verdadeiro desafio...",
    "Reconstrua o mosaico do KKKK para revelar o segredo final!",
    "Eu confio em você. Boa sorte!"
)

@Composable
fun StoryScreen(
    dialogos: List<String>,
    onFinished: () -> Unit
) {
    var dialogoIndex by remember { mutableIntStateOf(0) }
    var textoVisivel by remember { mutableStateOf("") }
    var digitando by remember { mutableStateOf(false) }
    var imagemIndex by remember { mutableIntStateOf(0) }

    val imagens = listOf(
        R.drawable.yumi,
        R.drawable.yumi_boca
    )

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

    LaunchedEffect(digitando) {
        while (digitando) {
            delay(600)
            imagemIndex = (imagemIndex + 1) % imagens.size
        }
        imagemIndex = 0
    }

    val escala by animateFloatAsState(
        targetValue = if (digitando) 1.02f else 1f,
        animationSpec = tween(600),
        label = "escala_personagem"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {

        Image(
            painter = painterResource(id = imagens[imagemIndex]),
            contentDescription = "Yumi",
            modifier = Modifier
                .height(400.dp)
                .aspectRatio(0.6f)
                .align(Alignment.BottomStart)
                .offset(x = 12.dp, y = (-80.dp))
                .graphicsLayer {
                    scaleX = escala
                    scaleY = escala
                }
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 180.dp, y = (-480.dp))
                .widthIn(max = 180.dp)
                .clickable {
                    if (digitando) {
                        digitando = false
                    } else {
                        if (dialogoIndex < dialogos.size - 1) {
                            dialogoIndex++
                        } else {
                            onFinished()
                        }
                    }
                }
        ) {

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.95f),
                shadowElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Yumi",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = RedPrimary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = textoVisivel,
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        lineHeight = 20.sp,
                        minLines = 3
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${dialogoIndex + 1}/${dialogos.size}",
                            fontSize = 10.sp,
                            color = Color.LightGray
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


            Box(
                modifier = Modifier
                    .size(16.dp)
                    .offset(x = (-8).dp, y = 8.dp)
                    .align(Alignment.BottomStart)
                    .background(
                        Color.White.copy(alpha = 0.95f),
                        RoundedCornerShape(bottomStart = 12.dp)
                    )
            )
        }
    }
}