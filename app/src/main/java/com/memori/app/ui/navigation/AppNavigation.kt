package com.memori.app.ui.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.memori.app.ui.screens.auth.AuthViewModel
import com.memori.app.ui.screens.auth.LocalAuthViewModel
import com.memori.app.ui.screens.auth.LoginScreen
import com.memori.app.ui.screens.auth.RegisterScreen
import com.memori.app.ui.screens.auth.SplashScreen
import com.memori.app.ui.screens.home.MainScreen
import com.memori.app.ui.screens.trail.ARScannerScreen
import com.memori.app.ui.screens.trail.FinalStoryScreen
import com.memori.app.ui.screens.trail.PuzzleScreen
import com.memori.app.ui.screens.trail.RankingScreen
import com.memori.app.ui.screens.trail.TrailStartScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()

    val sessionReady by authViewModel.sessionReady.collectAsState()
    val sessionToken by authViewModel.sessionToken.collectAsState()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val rotaAtual = backStackEntry?.destination?.route

    LaunchedEffect(sessionReady, sessionToken, rotaAtual) {
        val rotasPublicas = setOf(
            Screen.Splash.route,
            Screen.Login.route,
            Screen.Cadastro.route
        )

        if (
            sessionReady &&
            sessionToken == null &&
            rotaAtual != null &&
            rotaAtual !in rotasPublicas
        ) {
            navController.navigate(Screen.Login.route) {
                popUpTo(navController.graph.id) {
                    inclusive = false
                }
                launchSingleTop = true
            }
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
                        authViewModel.resetState()

                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Login.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                    onNavigateToHome = {
                        navController.navigate(Screen.HomeMap.route) {
                            popUpTo(Screen.Login.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Screen.HomeMap.route) {
                MainScreen(
                    onNavigateToTrailStart = {
                        navController.navigate(
                            Screen.Splash.route + "_trail"
                        )
                    },
                    onLogout = {
                        authViewModel.logout {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(navController.graph.id) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }

            composable(Screen.Splash.route + "_trail") {
                TrailStartScreen(
                    onStartAR = {
                        navController.navigate(Screen.ARScanner.route)
                    }
                )
            }

            composable(Screen.ARScanner.route) {
                ARScannerScreen(
                    onFinishedAR = {
                        navController.navigate(Screen.Puzzle.route)
                    }
                )
            }

            composable(Screen.Puzzle.route) {
                PuzzleScreen(
                    onPuzzleCompleted = { time ->
                        navController.navigate("final_story/$time")
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
                        navController.navigate(Screen.Ranking.route)
                    }
                )
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