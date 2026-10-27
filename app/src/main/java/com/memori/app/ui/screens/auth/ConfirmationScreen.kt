package com.memori.app.ui.screens.auth

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.memori.app.R

@Composable
fun ConfirmationScreen(
    onVoltarLogin: () -> Unit
) {
    BackHandler {
        onVoltarLogin()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AuthBackground)
            .safeDrawingPadding()
    ) {
        IconButton(
            onClick = onVoltarLogin,
            modifier = Modifier
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
                    .padding(
                        start = 28.dp,
                        end = 28.dp,
                        top = 24.dp,
                        bottom = 80.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.logo_rabiscado),
                    contentDescription = "MEMORI",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(190.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Recebemos a sua solicitação!",
                    color = AuthCoral,
                    fontSize = 20.sp,
                    lineHeight = 26.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

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