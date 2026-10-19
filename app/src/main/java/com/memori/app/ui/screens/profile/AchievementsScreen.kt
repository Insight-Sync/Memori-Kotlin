package com.memori.app.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.memori.app.ui.screens.auth.LocalAuthViewModel
import com.memori.app.ui.theme.RedPrimary
import kotlinx.coroutines.launch

data class Achievement(
    val title: String,
    val rank: String,
    val time: String,
    val emoji: String,
    val color: Color
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AchievementsScreen() {
    val viewModel = LocalAuthViewModel.current
    val usuario by viewModel.usuario.collectAsState()

    val nomeCurto = usuario?.nome
        ?.trim()
        ?.substringBefore(" ")
        ?.takeIf { it.isNotBlank() }
        ?: "Perfil"

    // Conquistas de exemplo: serão integradas à API depois.
    val achievements = remember {
        listOf(
            Achievement(
                "Lanternas Brilhantes",
                "#02 Lugar",
                "00:30:40 h",
                "🏮",
                Color(0xFFFF5252)
            ),
            Achievement(
                "Mestre dos Origami",
                "#01 Lugar",
                "00:15:20 h",
                "🦢",
                Color(0xFF4CAF50)
            ),
            Achievement(
                "Explorador KKKK",
                "#05 Lugar",
                "01:10:00 h",
                "🏛️",
                Color(0xFFFFC107)
            )
        )
    }

    val pagerState = rememberPagerState(
        pageCount = { achievements.size }
    )
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.weight(1f, fill = false),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = BorderStroke(1.dp, RedPrimary),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 6.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFEB3B))
                            .border(1.dp, RedPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("😑", fontSize = 12.sp)

                        Text(
                            text = "💢",
                            modifier = Modifier.align(Alignment.TopEnd),
                            fontSize = 6.sp,
                            color = Color.Red
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = nomeCurto,
                        color = RedPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = BorderStroke(1.dp, RedPrimary),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 6.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Pontuação de exemplo.
                    Text(
                        text = "10",
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.width(4.dp))

                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Estrelas",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        Text(
            text = "Minhas conquistas",
            color = RedPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterEnd
            ) {
                IconButton(
                    onClick = {
                        if (pagerState.currentPage > 0) {
                            scope.launch {
                                pagerState.animateScrollToPage(
                                    pagerState.currentPage - 1
                                )
                            }
                        }
                    },
                    enabled = pagerState.currentPage > 0
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Conquista anterior",
                        tint = if (pagerState.currentPage > 0) {
                            RedPrimary
                        } else {
                            Color.LightGray
                        }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .width(280.dp)
                    .height(450.dp)
            ) { page ->
                AchievementCard(achievements[page])
            }

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                IconButton(
                    onClick = {
                        if (pagerState.currentPage < achievements.lastIndex) {
                            scope.launch {
                                pagerState.animateScrollToPage(
                                    pagerState.currentPage + 1
                                )
                            }
                        }
                    },
                    enabled = pagerState.currentPage < achievements.lastIndex
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForwardIos,
                        contentDescription = "Próxima conquista",
                        tint = if (
                            pagerState.currentPage < achievements.lastIndex
                        ) {
                            RedPrimary
                        } else {
                            Color.LightGray
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(achievements.size) { index ->
                val cor = if (pagerState.currentPage == index) {
                    RedPrimary
                } else {
                    Color.LightGray
                }

                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(cor)
                )
            }
        }
    }
}

@Composable
fun AchievementCard(achievement: Achievement) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        border = BorderStroke(
            1.dp,
            Color.LightGray.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFD54F),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Text(
                text = achievement.title,
                color = Color(0xFF666666),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .background(
                            achievement.color.copy(alpha = 0.1f),
                            CircleShape
                        )
                )

                Text(
                    text = achievement.emoji,
                    fontSize = 80.sp
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = achievement.rank,
                    color = RedPrimary,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = achievement.time,
                    color = Color.LightGray,
                    fontSize = 16.sp
                )
            }
        }
    }
}