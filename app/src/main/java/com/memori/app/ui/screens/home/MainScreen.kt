package com.memori.app.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.memori.app.R
import com.memori.app.ui.screens.auth.LocalAuthViewModel
import com.memori.app.ui.screens.profile.AchievementsScreen
import com.memori.app.ui.screens.profile.ProfileScreen
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics

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
            popUpTo(
                bottomNavController.graph.startDestinationId
            )
            launchSingleTop = true
        }
    }

    val destinoPendente by DestinoMenuMemori.destino.collectAsState()
    LaunchedEffect(destinoPendente) {
        val destino = destinoPendente
        if (destino != null) {
            navegarPara(destino)
            DestinoMenuMemori.consumir()
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

        MenuMemori(
            selecionado = rotaAtual,
            avatar = usuario?.avatar,
            onNavigate = { navegarPara(it) },
            modifier = Modifier.align(Alignment.BottomCenter).navigationBarsPadding()
                .padding(bottom = 24.dp).widthIn(max = 320.dp).fillMaxWidth(0.82f)
        )
    }
    if (showTrailDialog) {
        Dialog(
            onDismissRequest = {
                showTrailDialog = false
            },
            properties = androidx.compose.ui.window.DialogProperties(
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
                    contentDescription = "Lanternas Brilhantes. Ajude Yumi a encontrar as lanternas a tempo do evento Tooro Nagashi.",
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
                        .clickable {
                            showTrailDialog = false
                            onNavigateToTrailStart()
                        }
                        .semantics {
                            contentDescription = "Jogar"
                            role = androidx.compose.ui.semantics.Role.Button
                        }
                )
            }
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