package com.memori.app.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.memori.app.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object DestinoMenuMemori {
    private val pendente = MutableStateFlow<String?>(null)
    val destino = pendente.asStateFlow()

    fun solicitar(destino: String) {
        if (destino in listOf("map", "profile", "achievements")) pendente.value = destino
    }

    fun consumir() {
        pendente.value = null
    }
}

@Composable
fun MenuMemori(
    selecionado: String,
    avatar: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val recurso = when (selecionado) {
        "profile" -> R.drawable.perfil_flutuante2
        "achievements" -> R.drawable.perfil_flutuante3
        else -> R.drawable.perfil_flutuante1
    }
    val imagem = painterResource(recurso)
    val proporcao = imagem.intrinsicSize.let {
        if (it.width.isFinite() && it.height.isFinite() && it.height > 0) it.width / it.height else 2.8f
    }
    BoxWithConstraints(modifier.aspectRatio(proporcao)) {
        val larguraAvatar = maxWidth * 0.21f
        val deslocamento = maxHeight * 0.12f
        Image(imagem, null, Modifier.fillMaxSize(), contentScale = ContentScale.FillBounds)
        Box(
            Modifier.align(Alignment.TopCenter).offset(y = deslocamento)
                .size(larguraAvatar).clip(CircleShape).background(Color.White)
                .padding(larguraAvatar * 0.12f),
            contentAlignment = Alignment.Center
        ) {
            HomeAvatar(avatar, Modifier.fillMaxSize())
        }
        Row(Modifier.fillMaxSize()) {
            listOf("map" to "Mapa", "profile" to "Meu perfil", "achievements" to "Minhas conquistas").forEach { (rota, nome) ->
                Box(
                    Modifier.weight(1f).fillMaxHeight()
                        .clickable(role = Role.Tab) { onNavigate(rota) }
                        .semantics {
                            contentDescription = nome
                            selected = selecionado == rota
                        }
                )
            }
        }
    }
}
