package com.example.myapplication.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import com.example.myapplication.ui.theme.LocalExtraColors

@Composable
fun ImageCard(
    onGoToPokemon: () -> Unit,
    onGoToQuiz: () -> Unit,
    onGoToLeaderboard: () -> Unit
) {
    val extra = LocalExtraColors.current

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(modifier = Modifier.fillMaxSize()) {

            // ⚫ Haut noir
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(extra.topColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Le fameux quizz pokémon",
                    modifier = Modifier.padding(24.dp),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                // 🟢 cercle vert décoratif
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .height(70.dp)
                        .clip(
                            RoundedCornerShape(
                                bottomStartPercent = 100,
                                bottomEndPercent = 100
                            )
                        )
                        .background(extra.greenAccent)
                )

            }

            // ⚫ Bas noir/gris foncé
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(extra.bottomColor),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(onClick = onGoToQuiz) {
                        Text("L'heure du quizz")
                    }
                    Button(onClick = onGoToPokemon) {
                        Text("Apprendre les pokémons")
                    }
                    Button(onClick = onGoToLeaderboard) {
                        Text("Leaderboard")
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(70.dp)
                        .clip(
                            RoundedCornerShape(
                                topStartPercent = 100,
                                topEndPercent = 100
                            )
                        )
                        .background(extra.greenAccent)
                )

            }
        }
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(width = 160.dp, height = 240.dp)
                    .background(extra.greenAccent, CircleShape)
            )


        // ⚫ Bande centrale
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(14.dp)
                .background(extra.bandeOuter)
        )


        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(72.dp)
                .background(extra.boutonOuter, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(extra.boutonInter, CircleShape)
            )
        }
    }
}