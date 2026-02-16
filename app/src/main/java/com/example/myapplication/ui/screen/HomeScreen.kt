package com.example.myapplication.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.myapplication.navigation.Screen
import com.example.myapplication.ui.component.ImageCard

@Composable
fun HomeScreen(navController: NavController) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            ImageCard(
                onGoToPokemon = {
                    navController.navigate(Screen.PokemonList.route)
                },
                onGoToQuiz = {
                    navController.navigate(Screen.GenQuiz.route)
                },
                onGoToLeaderboard = {
                    navController.navigate(Screen.Leaderboard.route)
                }
            )
        }
    }
}