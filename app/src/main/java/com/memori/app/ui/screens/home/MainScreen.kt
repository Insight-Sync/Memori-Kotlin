package com.memori.app.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.memori.app.R
import com.memori.app.ui.screens.auth.LocalAuthViewModel
import com.memori.app.ui.screens.profile.AchievementsScreen
import com.memori.app.ui.screens.profile.ProfileScreen

internal val HomeCoral = Color(0xFFFF666B)
internal val HomePink = Color(0xFFFFB3B7)

@Composable
fun MainScreen(
    onNavigateToTrailStart: () -> Unit,
    onLogout: () -> Unit
) {
    val bottomNavController = rememberNavController()
    val authViewModel = LocalAuthViewModel.current
    val usuario by authViewModel.usuario.collectAsState()

    val backStackEntry by bottomNavController
        .currentBackStackEntryAsState()

    val rotaAtual = backStackEntry?.destination?.route ?: "map"

    var showTrailDialog by remember {
        mutableStateOf(false)
    }

    fun navegarPara(rota: String) {
        if (rotaAtual == rota) return

        bottomNavController.navigate(rota) {
            popUpTo(bottomNavController.graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
    ) {
        NavHost(
            navController = bottomNavController,
            startDestination = "map",
            modifier = Modifier.fillMaxSize()
        ) {
            composable("map") {
                HomeMapScreen(
                    onNavigateToProfile = {
                        navegarPara("profile")
                    },
                    onOpenTrail = {
                        showTrailDialog = true
                    }
                )
            }

            composable("profile") {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding()
                        .padding(bottom = 132.dp)
                ) {
                    ProfileScreen(
                        onLogout = onLogout
                    )
                }
            }

            composable("achievements") {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding()
                        .padding(bottom = 132.dp)
                ) {
                    AchievementsScreen()
                }
            }
        }

        MenuFlutuante(
            rotaAtual = rotaAtual,
            avatar = usuario?.avatar,
            onMapa = {
                navegarPara("map")
            },
            onPerfil = {
                navegarPara("profile")
            },
            onConquistas = {
                navegarPara("achievements")
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 24.dp)
                .widthIn(max = 320.dp)
                .fillMaxWidth(0.82f)
        )
    }

    if (showTrailDialog) {
        Dialog(
            onDismissRequest = {
                showTrailDialog = false
            },
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .widthIn(max = 380.dp)
                    .fillMaxWidth(0.94f)
                    .aspectRatio(1024f / 1862f)
            ) {
                Image(
                    painter = painterResource(R.drawable.card_jogavel),
                    contentDescription =
                        "Lanternas Brilhantes. Ajude Yumi a encontrar " +
                                "as lanternas a tempo do evento Tooro Nagashi.",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .offset(
                            x = maxWidth * 0.145f,
                            y = maxHeight * 0.738f
                        )
                        .width(maxWidth * 0.72f)
                        .height(maxHeight * 0.115f)
                        .clip(RoundedCornerShape(100.dp))
                        .clickable(
                            role = Role.Button,
                            onClickLabel = "Jogar"
                        ) {
                            showTrailDialog = false
                            onNavigateToTrailStart()
                        }
                        .semantics {
                            contentDescription = "Jogar"
                        }
                )
            }
        }
    }
}

@Composable
private fun MenuFlutuante(
    rotaAtual: String,
    avatar: String?,
    onMapa: () -> Unit,
    onPerfil: () -> Unit,
    onConquistas: () -> Unit,
    modifier: Modifier = Modifier
) {
    val recurso = when (rotaAtual) {
        "profile" -> R.drawable.perfil_flutuante2
        "achievements" -> R.drawable.perfil_flutuante3
        else -> R.drawable.perfil_flutuante1
    }

    val imagem = painterResource(recurso)
    val tamanho = imagem.intrinsicSize

    val proporcao = if (
        tamanho.width.isFinite() &&
        tamanho.height.isFinite() &&
        tamanho.width > 0f &&
        tamanho.height > 0f
    ) {
        tamanho.width / tamanho.height
    } else {
        2.8f
    }

    BoxWithConstraints(
        modifier = modifier.aspectRatio(proporcao)
    ) {
        val larguraMenu = maxWidth
        val alturaMenu = maxHeight

        Image(
            painter = imagem,
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = alturaMenu * 0.12f)
                .size(larguraMenu * 0.21f)
                .clip(CircleShape)
                .background(Color.White)
                .padding(larguraMenu * 0.025f),
            contentAlignment = Alignment.Center
        ) {
            HomeAvatar(
                avatar = avatar,
                modifier = Modifier.fillMaxSize()
            )
        }

        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable(
                        role = Role.Tab,
                        onClickLabel = "Explorar mapa",
                        onClick = onMapa
                    )
                    .semantics {
                        contentDescription = "Explorar mapa"
                    }
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable(
                        role = Role.Tab,
                        onClickLabel = "Meu perfil",
                        onClick = onPerfil
                    )
                    .semantics {
                        contentDescription = "Meu perfil"
                    }
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable(
                        role = Role.Tab,
                        onClickLabel = "Minhas conquistas",
                        onClick = onConquistas
                    )
                    .semantics {
                        contentDescription = "Minhas conquistas"
                    }
            )
        }
    }
}

@Composable
internal fun HomeAvatar(
    avatar: String?,
    modifier: Modifier = Modifier
) {
    val imagem = when (avatar) {
        "azul" -> R.drawable.avatar_azul
        "vermelho" -> R.drawable.avatar_vermelho
        else -> R.drawable.avatar_amarelo
    }

    Image(
        painter = painterResource(imagem),
        contentDescription = "Avatar do usuário",
        contentScale = ContentScale.Fit,
        modifier = modifier
    )
}