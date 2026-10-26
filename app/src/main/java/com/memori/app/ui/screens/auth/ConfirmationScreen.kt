package com.memori.app.ui.screens.auth

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.memori.app.R

@Composable
fun ConfirmationScreen(
    onVoltarLogin: () -> Unit
) {
    val fundo = Color(0xFFF9F9F9)
    val coral = Color(0xFFFF666B)

    BackHandler(onBack = onVoltarLogin)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fundo)
            .safeDrawingPadding()
    ) {
        IconButton(
            onClick = onVoltarLogin,
            modifier = Modifier.padding(
                start = 8.dp,
                top = 8.dp
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Voltar para o login",
                tint = coral,
                modifier = Modifier.size(24.dp)
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 440.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp)
                    .padding(top = 24.dp, bottom = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(
                        id = R.drawable.logo_rabiscado
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(160.dp)
                )

                Spacer(Modifier.height(32.dp))

                Text(
                    text = "Recebemos a sua solicitação!",
                    color = coral,
                    fontSize = 20.sp,
                    lineHeight = 26.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(12.dp))

                // Texto mockado para reproduzir o protótipo.
                // O envio de e-mail ainda não está implementado.
                Text(
                    text = "Foi enviado um e-mail de confirmação para\n" +
                            "a sua caixa de entrada.\n" +
                            "Caso já tenha confirmado, por favor,\n" +
                            "retorne para a tela de login para acessar a\n" +
                            "plataforma.",
                    color = Color(0xFF909090),
                    fontSize = 12.sp,
                    lineHeight = 15.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.widthIn(max = 290.dp)
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 393,
    heightDp = 852
)
@Composable
private fun ConfirmationScreenPreview() {
    ConfirmationScreen(
        onVoltarLogin = {}
    )
}