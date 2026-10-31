package com.memori.app.ui.navigation

import android.content.pm.ApplicationInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.memori.app.ui.screens.auth.AuthViewModel
import com.memori.app.ui.screens.auth.ConfirmationScreen
import com.memori.app.ui.screens.auth.LocalAuthViewModel
import com.memori.app.ui.screens.auth.LoginScreen
import com.memori.app.ui.screens.auth.RegisterScreen
import com.memori.app.ui.screens.auth.SplashScreen
import com.memori.app.ui.screens.home.HomeMapScreen
import com.memori.app.ui.screens.home.MainScreen
import com.memori.app.ui.screens.trail.ARScannerScreen
import com.memori.app.ui.screens.trail.BuscaGuiaOverlay
import com.memori.app.ui.screens.trail.FinalStoryScreen
import com.memori.app.ui.screens.trail.PuzzleScreen
import com.memori.app.ui.screens.trail.RankingScreen
import com.memori.app.ui.screens.trail.StoryScreen
import com.memori.app.ui.screens.trail.dialogosPostAR
import com.memori.app.ui.screens.trail.mensagensGuiaLanterna

private const val CONFIRMATION_ROUTE = "confirmation"
private const val STORY_POST_AR_ROUTE = "story_post_ar"
private const val GUIA_LANTERNA_ROUTE = "guia_lanterna"

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val context = LocalContext.current

    val modoDesenvolvimento =
        (context.applicationInfo.flags and
                ApplicationInfo.FLAG_DEBUGGABLE) != 0

    val sessionReady by authViewModel.sessionReady.collectAsState()
    val sessionToken by authViewModel.sessionToken.collectAsState()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val rotaAtual = backStackEntry?.destination?.route

    fun voltarParaLogin() {
        authViewModel.resetState()

        navController.navigate(Screen.Login.route) {
            popUpTo(navController.graph.id) {
                inclusive = false
            }
            launchSingleTop = true
        }
    }

    LaunchedEffect(
        sessionReady,
        sessionToken,
        rotaAtual
    ) {
        val rotasPublicas = setOf(
            Screen.Splash.route,
            Screen.Login.route,
            Screen.Cadastro.route,
            CONFIRMATION_ROUTE
        )

        if (
            sessionReady &&
            sessionToken == null &&
            !modoDesenvolvimento &&
            rotaAtual != null &&
            rotaAtual !in rotasPublicas
        ) {
            voltarParaLogin()
        }
    }

    CompositionLocalProvider(
        LocalAuthViewModel provides authViewModel
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route
        ) {
            composable(Screen.Splash.route) {
                var splashFinished by remember {
                    mutableStateOf(false)
                }

                SplashScreen(
                    onSplashFinished = {
                        splashFinished = true
                    }
                )

                LaunchedEffect(
                    splashFinished,
                    sessionReady,
                    sessionToken
                ) {
                    if (splashFinished && sessionReady) {
                        val destino = if (sessionToken != null) {
                            Screen.HomeMap.route
                        } else {
                            Screen.Login.route
                        }

                        navController.navigate(destino) {
                            popUpTo(Screen.Splash.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                }
            }

            composable(Screen.Login.route) {
                LoginScreen(
                    viewModel = authViewModel,
                    onNavigateToHome = {
                        navController.navigate(Screen.HomeMap.route) {
                            popUpTo(Screen.Login.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                    onNavigateToCadastro = {
                        authViewModel.resetState()

                        navController.navigate(Screen.Cadastro.route) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Screen.Cadastro.route) {
                RegisterScreen(
                    viewModel = authViewModel,
                    onNavigateToLogin = {
                        voltarParaLogin()
                    },
                    onNavigateToHome = {
                        navController.navigate(Screen.HomeMap.route) {
                            popUpTo(Screen.Login.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                    onCadastroConcluido = {
                        authViewModel.resetState()

                        navController.navigate(CONFIRMATION_ROUTE) {
                            popUpTo(Screen.Cadastro.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(CONFIRMATION_ROUTE) {
                ConfirmationScreen(
                    onVoltarLogin = {
                        voltarParaLogin()
                    }
                )
            }

            composable(Screen.HomeMap.route) {
                MainScreen(
                    onNavigateToTrailStart = {
                        navController.navigate(Screen.ARScanner.route) {
                            launchSingleTop = true
                        }
                    },
                    onLogout = {
                        authViewModel.logout {
                            voltarParaLogin()
                        }
                    }
                )
            }

            composable(Screen.ARScanner.route) {
                fun abrirHistoria() {
                    val rota = navController.currentBackStackEntry
                        ?.destination
                        ?.route

                    if (rota == Screen.ARScanner.route) {
                        navController.navigate(STORY_POST_AR_ROUTE) {
                            popUpTo(Screen.ARScanner.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                }

                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    ARScannerScreen(
                        onFinishedAR = {
                            abrirHistoria()
                        }
                    )

                    if (modoDesenvolvimento) {
                        Button(
                            onClick = {
                                abrirHistoria()
                            },
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .navigationBarsPadding()
                                .padding(24.dp)
                        ) {
                            Text("Pular câmera")
                        }
                    }
                }
            }

            composable(STORY_POST_AR_ROUTE) {
                Box(modifier = Modifier.fillMaxSize()) {
                    HomeMapScreen(onNavigateToProfile = {})

                    StoryScreen(
                        dialogos = dialogosPostAR,
                        onFinished = {
                            navController.navigate(Screen.Puzzle.route) {
                                popUpTo(STORY_POST_AR_ROUTE) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }

            composable(Screen.Puzzle.route) {
                PuzzleScreen(
                    onPuzzleCompleted = { time ->
                        navController.navigate("final_story/$time") {
                            launchSingleTop = true
                        }
                    },
                    onExit = {
                        navController.navigate(Screen.HomeMap.route) {
                            popUpTo(Screen.HomeMap.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable("final_story/{time}") { entry ->
                val time = entry.arguments
                    ?.getString("time")
                    ?.toIntOrNull()
                    ?: 0

                FinalStoryScreen(
                    timeElapsed = time,
                    onNavigateToRanking = {
                        navController.navigate(GUIA_LANTERNA_ROUTE) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            // Último balão da Yumi, em cima do mapa, antes do ranking
            composable(GUIA_LANTERNA_ROUTE) {
                Box(modifier = Modifier.fillMaxSize()) {
                    HomeMapScreen(onNavigateToProfile = {})

                    BuscaGuiaOverlay(
                        mensagens = mensagensGuiaLanterna,
                        onEntendido = {
                            navController.navigate(Screen.Ranking.route) {
                                popUpTo(GUIA_LANTERNA_ROUTE) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }

            composable(Screen.Ranking.route) {
                RankingScreen(
                    onBackToHome = {
                        navController.navigate(Screen.HomeMap.route) {
                            popUpTo(Screen.HomeMap.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}