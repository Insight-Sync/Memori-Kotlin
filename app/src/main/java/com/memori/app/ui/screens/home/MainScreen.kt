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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
    ) {
        // O mapa ocupa o fundo inteiro.
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

        // Menu flutuante sobre o conteúdo.
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 24.dp)
                .widthIn(max = 320.dp)
                .fillMaxWidth(0.78f)
                .height(108.dp)
        ) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(76.dp),
                shape = RoundedCornerShape(40.dp),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.width(84.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(
                            onClick = {
                                navegarPara("map")
                            },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Explore,
                                contentDescription = "Explorar mapa",
                                tint = if (rotaAtual == "map") {
                                    HomeCoral
                                } else {
                                    HomePink
                                },
                                modifier = Modifier.size(38.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .width(16.dp)
                                .height(4.dp)
                                .background(
                                    color = if (rotaAtual == "map") {
                                        HomeCoral
                                    } else {
                                        Color.Transparent
                                    },
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                    }

                    Spacer(modifier = Modifier.width(72.dp))

                    Column(
                        modifier = Modifier.width(84.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(
                            onClick = {
                                navegarPara("achievements")
                            },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Collections,
                                contentDescription = "Minhas conquistas",
                                tint = if (rotaAtual == "achievements") {
                                    HomeCoral
                                } else {
                                    HomePink
                                },
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .width(16.dp)
                                .height(4.dp)
                                .background(
                                    color = if (
                                        rotaAtual == "achievements"
                                    ) {
                                        HomeCoral
                                    } else {
                                        Color.Transparent
                                    },
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                    }
                }
            }

            // Avatar real da pessoa, elevado no centro.
            Surface(
                onClick = {
                    navegarPara("profile")
                },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .size(88.dp),
                shape = CircleShape,
                color = Color.White,
                border = BorderStroke(
                    width = 4.dp,
                    color = if (rotaAtual == "profile") {
                        HomeCoral
                    } else {
                        HomePink
                    }
                ),
                shadowElevation = 5.dp
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    HomeAvatar(
                        avatar = usuario?.avatar,
                        modifier = Modifier.size(55.dp)
                    )
                }
            }
        }
    }

    if (showTrailDialog) {
        Dialog(
            onDismissRequest = {
                showTrailDialog = false
            }
        ) {
            Card(
                modifier = Modifier
                    .widthIn(max = 340.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Lanternas Brilhantes",
                        color = HomeCoral,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Image(
                        painter = painterResource(R.drawable.lanterna),
                        contentDescription = "Lanternas Brilhantes",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(180.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Ajude Yumi a encontrar as Lanternas " +
                                "Brilhantes a tempo do evento Tooro Nagashi!",
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            showTrailDialog = false
                            onNavigateToTrailStart()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(27.dp),
                        border = BorderStroke(3.dp, HomePink),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HomeCoral,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Jogar",
                            fontSize = 18.sp
                        )
                    }

                    TextButton(
                        onClick = {
                            showTrailDialog = false
                        }
                    ) {
                        Text(
                            text = "Voltar ao mapa",
                            color = HomeCoral
                        )
                    }
                }
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