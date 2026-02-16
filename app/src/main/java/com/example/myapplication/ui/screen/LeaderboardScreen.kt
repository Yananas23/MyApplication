package com.example.myapplication.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.myapplication.leaderboard.LeaderboardUtils
import com.example.myapplication.leaderboard.ScoreDto
import com.example.myapplication.navigation.Screen
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    var scores by remember { mutableStateOf<List<ScoreDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    // -----------------------------
    // Charger top 10 au démarrage
    // -----------------------------
    LaunchedEffect(Unit) {
        isLoading = true
        errorMsg = null
        try {
            val result = LeaderboardUtils.loadFirstPage()
            scores = result.getOrElse { emptyList() }
        } catch (e: Exception) {
            errorMsg = e.message
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pokémons") },
                navigationIcon = {
                    IconButton({
                        navController.navigate(Screen.Home.route)
                    }) { Text("←") }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // -----------------------------
            // Contenu leaderboard
            // -----------------------------
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (errorMsg != null) {
                Text("Erreur : $errorMsg", color = Color.Red)
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(scores) { index, score ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${index + 1}. ${score.username}")
                            Text("${score.score}")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // -----------------------------
                // Bouton afficher plus
                // -----------------------------
                Button(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            try {
                                val result = LeaderboardUtils.loadMore()
                                scores = result.getOrElse { scores }
                            } catch (e: Exception) {
                                errorMsg = e.message
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Afficher plus")
                }
            }
        }
    }
}