package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.data.repository.PokemonRepository
import com.example.myapplication.leaderboard.LeaderboardUtils
import com.example.myapplication.navigation.AppNavGraph
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LeaderboardUtils.init(this)

        setContent {
            MyApplicationTheme {

                LaunchedEffect(Unit) {
                    withContext(Dispatchers.IO) {
                        PokemonRepository.fetchAllPokemons()
                    }
                }

                val navController = rememberNavController()
                AppNavGraph(navController)
            }
        }
    }
}
