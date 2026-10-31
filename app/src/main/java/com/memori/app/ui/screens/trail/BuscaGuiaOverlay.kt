package com.memori.app.ui.screens.trail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.memori.app.R
import com.memori.app.ui.theme.RedPrimary

val mensagensGuiaBusca = listOf(
    "Ajude Yumi a encontrar a lanterna de sua família! Dirija-se ao KKKK e procure pela lanterna perdida."
)

val mensagensGuiaLanterna = listOf(
    "Ufa! Obrigada por construir a lanterna e me ajudar nessa jornada!"
)

// Posição fixa do balão: os dois ficam sempre na mesma altura
private val TOPO_FIXO = 80.dp

@Composable
fun BuscaGuiaOverlay(
    mensagens: List<String>,
    @Suppress("UNUSED_PARAMETER") topo: Dp = TOPO_FIXO, // ignorado de propósito
    onEntendido: () -> Unit
) {
    var passo by remember { mutableIntStateOf(0) }
    val ultimo = passo == mensagens.lastIndex

    // Ícone da Yumi: quanto dele sai para fora do balão (por baixo)
    val tamanhoIcone = 72.dp
    val parteFora = 48.dp
    val parteDentro = tamanhoIcone - parteFora

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = TOPO_FIXO, start = 16.dp, end = 88.dp)
                .fillMaxWidth()
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 6.dp,
                border = BorderStroke(1.dp, Color(0xFFEEEEEE)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        enabled = !ultimo,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { passo++ }
            ) {
                Row(
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = parteDentro + 4.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = mensagens[passo],
                        modifier = Modifier.weight(1f),
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Center
                    )

                    if (!ultimo) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Próximo",
                            tint = RedPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            Image(
                painter = painterResource(R.drawable.yumi2),
                contentDescription = "Yumi",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 4.dp, y = parteFora)
                    .size(tamanhoIcone)
                    .zIndex(1f)
            )

            if (ultimo) {
                Button(
                    onClick = onEntendido,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-8).dp, y = 24.dp)
                        .height(40.dp),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(2.dp, Color.White),
                    colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    Text(
                        text = "Entendido!",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}