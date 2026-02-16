package com.example.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.myapplication.ui.screen.GenQuiz
import com.example.myapplication.ui.screen.HomeScreen
import com.example.myapplication.ui.screen.PokemonDetailScreen
import com.example.myapplication.ui.screen.PokemonListScreen
import com.example.myapplication.ui.screen.QuizDone
import com.example.myapplication.ui.screen.QuizPokemon
import com.example.myapplication.ui.screen.LeaderboardScreen


sealed class Screen(val route: String) {
    object Home : Screen("home")
    object PokemonList : Screen("pokemon_list")
    object QuizPokemon : Screen("quiz_pokpok")
    object PokemonDetail : Screen("pokemon_detail")
    object QuizDone : Screen("quiz_done/{score}")
    object GenQuiz : Screen("gen_quiz")
    object Leaderboard : Screen("leaderboard")
}

@Composable
fun AppNavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {

        // =====================
        // HOME
        // =====================

        composable(Screen.Home.route) {
            HomeScreen(navController)
        }

        // =====================
        // LISTE POKEMON
        // =====================

        composable(Screen.PokemonList.route) {
            PokemonListScreen(navController)
        }

        // =====================
        // QUIZ — génération
        // =====================

        composable(Screen.GenQuiz.route) {
            GenQuiz(navController)
        }

        // =====================
        // QUIZ — écran jeu
        // =====================

        composable(
            route = Screen.QuizPokemon.route + "/{gen}",
            arguments = listOf(
                navArgument("gen") { type = NavType.IntType }
            )
        ) { backStackEntry ->

            val gen = backStackEntry.arguments?.getInt("gen") ?: 1

            QuizPokemon(
                navController = navController,
                gen = gen
            )
        }

        // =====================
        // DETAIL POKEMON
        // =====================

        composable(
            route = Screen.PokemonDetail.route + "/{pokemonId}",
            arguments = listOf(
                navArgument("pokemonId") { type = NavType.IntType }
            )
        ) { backStackEntry ->

            val id = backStackEntry.arguments?.getInt("pokemonId") ?: 1

            PokemonDetailScreen(
                navController = navController,
                pokemonId = id
            )
        }

        // =====================
        // QUIZ DONE
        // =====================

        composable(
            route = Screen.QuizDone.route,
            arguments = listOf(
                navArgument("score") { type = NavType.IntType }
            )
        ) { backStackEntry ->

            val score = backStackEntry.arguments?.getInt("score") ?: 0

            QuizDone(
                navController = navController,
                score = score
            )
        }

        composable(Screen.Leaderboard.route) {
            LeaderboardScreen(navController)
        }
    }
}