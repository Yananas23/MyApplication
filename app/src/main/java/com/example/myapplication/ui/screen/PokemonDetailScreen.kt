package com.example.myapplication.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.myapplication.data.model.Pokemon
import com.example.myapplication.data.repository.PokemonRepository
import com.example.myapplication.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailScreen(
    navController: NavController,
    pokemonId: Int
) {
    var isShiny by remember { mutableStateOf(false) }

    var pokemon by remember { mutableStateOf<Pokemon?>(null) }
    var evolutionPokemons by remember { mutableStateOf<List<Pokemon>>(emptyList()) }

    // Chargement du Pokémon principal
    LaunchedEffect(pokemonId) {
        pokemon = runCatching {
            PokemonRepository.fetchPokemon(pokemonId)
        }.getOrNull()
    }

    // Chargement de la ligne évolutive
    LaunchedEffect(pokemon) {
        val pok = pokemon ?: return@LaunchedEffect

        val evolutionIds = buildSet {
            add(pok.id)
            addAll(pok.evolutionsPre)
            addAll(pok.evolutionsNext)
        }

        evolutionPokemons = evolutionIds
            .sorted()
            .mapNotNull { id ->
                runCatching { PokemonRepository.fetchPokemon(id) }.getOrNull()
            }
    }

    // État de chargement
    if (pokemon == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val pok = pokemon!!

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(pok.nameFr) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Screen.PokemonList.route) }) {
                        Text("←")
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Sprite + shiny
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = if (isShiny) pok.shinySprite else pok.sprite,
                            contentDescription = pok.nameFr,
                            modifier = Modifier.size(200.dp),
                            contentScale = ContentScale.Fit
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(onClick = { isShiny = !isShiny }) {
                            Text(if (isShiny) "Normal" else "Shiny ✨")
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Navigation précédent / suivant
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    if (pokemonId > 1) {
                        Button(
                            onClick = {
                                navController.navigate(
                                    Screen.PokemonDetail.route + "/${pokemonId - 1}"
                                ) {
                                    popUpTo(Screen.PokemonDetail.route) { inclusive = true }
                                }
                            }
                        ) {
                            Text("Pokémon précédent")
                        }
                    }

                    if (pokemonId < 1025) {
                        Button(
                            onClick = {
                                navController.navigate(
                                    Screen.PokemonDetail.route + "/${pokemonId + 1}"
                                ) {
                                    popUpTo(Screen.PokemonDetail.route) { inclusive = true }
                                }
                            }
                        ) {
                            Text("Pokémon suivant")
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Infos générales
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("N° Pokédex : ${pok.id}")
                        Text("Nom (EN) : ${pok.nameEn}")
                        Text("Nom (JP) : ${pok.nameJp}")
                        Text("Catégorie : ${pok.category}")

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            "Types",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        pok.typeNames.forEachIndexed { index, name ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = pok.typeImages.getOrNull(index),
                                    contentDescription = name,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(name)
                            }
                        }
                    }
                }
            }

            // Ligne évolutive
            if (evolutionPokemons.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Ligne évolutive",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(evolutionPokemons) { evo ->
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.clickable {
                                            navController.navigate(
                                                Screen.PokemonDetail.route + "/${evo.id}"
                                            ) {
                                                popUpTo(Screen.PokemonDetail.route) {
                                                    inclusive = true
                                                }
                                            }
                                        }
                                    ) {
                                        AsyncImage(
                                            model = evo.sprite,
                                            contentDescription = evo.nameFr,
                                            modifier = Modifier.size(80.dp),
                                            contentScale = ContentScale.Fit
                                        )
                                        Text(
                                            evo.nameFr,
                                            fontWeight = if (evo.id == pok.id)
                                                FontWeight.Bold
                                            else
                                                FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}