package com.memori.app.ui.screens.auth

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.memori.app.R

internal val AuthBackground = Color(0xFFF9F9F9)
internal val AuthCoral = Color(0xFFFF666B)
internal val AuthPink = Color(0xFFFFB3B7)

@Composable
fun AuthHeader(
    titulo: String? = null,
    onVoltar: (() -> Unit)? = null
) {
    val cadastro = titulo != null

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .background(AuthBackground)
            .clipToBounds()
    ) {
        if (cadastro) {
            Image(
                painter = painterResource(R.drawable.bola_amarela_textura),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = (-120).dp, y = (-30).dp)
                    .size(170.dp)
            )

            Image(
                painter = painterResource(R.drawable.bola_verde_textura),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-12).dp, y = (-45).dp)
                    .size(170.dp)
            )

            Image(
                painter = painterResource(R.drawable.bola_azul_textura),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 80.dp, y = 55.dp)
                    .size(130.dp)
            )

            Text(
                text = titulo.orEmpty(),
                color = AuthCoral,
                fontSize = 30.sp,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = (-5).dp)
            )
        } else {
            // Bola vermelha
            Image(
                painter = painterResource(R.drawable.bola_vermelha_textura),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = (-92).dp, y = 8.dp)
                    .size(145.dp)
            )
            // Bola amarela
            Image(
                painter = painterResource(R.drawable.bola_amarela_textura),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 10.dp, y = (-45).dp)
                    .size(145.dp)
            )

            // Bola verde
            Image(
                painter = painterResource(R.drawable.bola_verde_textura),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 46.dp, y = 55.dp)
                    .size(145.dp)
            )

            // Bola azul
            Image(
                painter = painterResource(R.drawable.bola_azul_textura),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 30.dp, y = 25.dp)
                    .size(145.dp)
            )

            // MEMO centralizado, sem deslocamento horizontal.
            Image(
                painter = painterResource(R.drawable.logo_rabiscado),
                contentDescription = "MEMORI",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = (-10).dp)
                    .size(180.dp)
            )
        }

        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val largura = size.width
            val altura = size.height

            val curva = Path().apply {
                moveTo(
                    x = 0f,
                    y = altura * 0.88f
                )

                cubicTo(
                    x1 = largura * 0.12f,
                    y1 = altura * 1.05f,
                    x2 = largura * 0.70f,
                    y2 = altura * 0.60f,
                    x3 = largura,
                    y3 = altura * 0.88f
                )

                lineTo(largura, altura)
                lineTo(0f, altura)
                close()
            }

            drawPath(
                path = curva,
                color = Color.Black.copy(alpha = 0.025f),
                style = Stroke(width = 14.dp.toPx())
            )

            drawPath(
                path = curva,
                color = Color.Black.copy(alpha = 0.035f),
                style = Stroke(width = 6.dp.toPx())
            )

            drawPath(
                path = curva,
                color = AuthBackground
            )
        }
        if (onVoltar != null) {
            IconButton(
                onClick = onVoltar,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 12.dp, top = 8.dp)
                    .size(48.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.icon_voltar),
                    contentDescription = "Voltar para o login",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}