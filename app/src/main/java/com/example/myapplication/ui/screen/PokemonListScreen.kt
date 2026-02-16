package com.example.myapplication.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.data.model.Pokemon
import com.example.myapplication.data.repository.PokemonRepository
import com.example.myapplication.navigation.Screen
import com.example.myapplication.ui.component.PokeCard
import kotlinx.coroutines.launch
import java.text.Normalizer
import androidx.compose.foundation.layout.FlowRow

// ----------------------------
// Accent normalize
// ----------------------------
fun normalizeText(input: String): String {
    return Normalizer.normalize(input, Normalizer.Form.NFD)
        .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
        .lowercase()
}

// ----------------------------
// Générations
// ----------------------------
val generationRanges = mapOf(
    1 to (1..151),
    2 to (152..251),
    3 to (252..386),
    4 to (387..493),
    5 to (494..649),
    6 to (650..721),
    7 to (722..809),
    8 to (810..905),
    9 to (906..1025)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonListScreen(navController: NavController) {

    // ---------------- STATE
    var searchText by remember { mutableStateOf("") }
    var selectedTypes by remember { mutableStateOf(setOf<String>()) }
    var selectedGeneration by remember { mutableStateOf<Int?>(null) }

    var showFilters by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // ---------------- DATA
    val allPokemons by produceState(initialValue = emptyList<Pokemon>()) {
        value = PokemonRepository.fetchAllPokemons()
    }

    val allTypes = remember(allPokemons) {
        allPokemons.flatMap { it.typeNames }.distinct().sorted()
    }

    // ---------------- FILTER
    val normalizedSearch = normalizeText(searchText)

    val filteredPokemons = remember(
        allPokemons,
        normalizedSearch,
        selectedTypes,
        selectedGeneration
    ) {
        allPokemons.filter { p ->

            val nameMatch =
                normalizeText(p.nameFr).contains(normalizedSearch)

            val typeMatch =
                selectedTypes.isEmpty() ||
                        selectedTypes.all { it in p.typeNames }

            val genMatch =
                selectedGeneration == null ||
                        generationRanges[selectedGeneration]
                            ?.contains(p.id) == true

            nameMatch && typeMatch && genMatch
        }
    }

    val showTopButton by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 5 }
    }

    // ---------------- UI
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
        },

        floatingActionButton = {
            if (showTopButton) {
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            listState.animateScrollToItem(0)
                        }
                    }
                ) { Text("↑") }
            }
        }

    ) { padding ->

        LazyColumn(
            state = listState,
            modifier = Modifier.padding(padding)
        ) {

            // 🔎 Search + filter button
            item {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        label = { Text("Recherche nom FR") },
                        modifier = Modifier.weight(1f)
                    )

                    Button(
                        onClick = { showFilters = true }
                    ) {
                        Text("Filtrer")
                    }
                }
            }

            items(filteredPokemons) {
                PokeCard(it, navController)
            }
        }
    }

    // ---------------- FILTER SHEET
    if (showFilters) {
        ModalBottomSheet(
            onDismissRequest = { showFilters = false }
        ) {

            Column(Modifier.padding(16.dp)) {

                Text("Génération", style = MaterialTheme.typography.titleMedium)

                Spacer(Modifier.height(8.dp))

                GenerationChips(
                    selectedGeneration = selectedGeneration,
                    onSelect = { selectedGeneration = it }
                )

                Spacer(Modifier.height(16.dp))

                Text("Types", style = MaterialTheme.typography.titleMedium)

                Spacer(Modifier.height(8.dp))

                TypeChips(
                    types = allTypes,
                    selectedTypes = selectedTypes,
                    onToggle = { type ->
                        selectedTypes =
                            if (type in selectedTypes)
                                selectedTypes - type
                            else
                                selectedTypes + type
                    }
                )

                Spacer(Modifier.height(24.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    OutlinedButton(
                        onClick = {
                            selectedTypes = emptySet()
                            selectedGeneration = null
                        }
                    ) {
                        Text("Reset")
                    }

                    Button(
                        onClick = { showFilters = false }
                    ) {
                        Text("Appliquer")
                    }
                }
            }
        }
    }
}

// ----------------------------
// Generation chips
// ----------------------------
@Composable
fun GenerationChips(
    selectedGeneration: Int?,
    onSelect: (Int?) -> Unit
) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

        FilterChip(
            selected = selectedGeneration == null,
            onClick = { onSelect(null) },
            label = { Text("Toutes") }
        )

        (1..9).forEach { gen ->
            FilterChip(
                selected = selectedGeneration == gen,
                onClick = { onSelect(gen) },
                label = { Text("Gen $gen") }
            )
        }
    }
}

// ----------------------------
// Type chips
// ----------------------------
@Composable
fun TypeChips(
    types: List<String>,
    selectedTypes: Set<String>,
    onToggle: (String) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        types.forEach { type ->
            FilterChip(
                selected = type in selectedTypes,
                onClick = { onToggle(type) },
                label = { Text(type) }
            )
        }
    }
}