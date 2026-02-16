package com.example.myapplication.data.repository

import androidx.compose.runtime.getValue
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.myapplication.data.api.PokemonApi
import com.example.myapplication.data.model.Pokemon


object PokemonRepository {

    private val api: PokemonApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://tyradex.app/api/v1")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PokemonApi::class.java)
    }

    // ---------- CACHE ----------
    private val pokemonCache = mutableMapOf<Int, Pokemon>()
    private val listCache = mutableMapOf<Pair<Int, Int>, List<Pokemon>>()

    // ---------- SINGLE ----------
    suspend fun fetchPokemon(id: Int): Pokemon {

        // cache hit
        pokemonCache[id]?.let { return it }

        val response = api.getPokemon(id)

        val pokemon = Pokemon(
            id = response.pokedex_id,
            nameFr = response.name.fr,
            nameEn = response.name.en,
            nameJp = response.name.jp,
            typeNames = response.types.map { it.name },
            typeImages = response.types.map { it.image },
            category = response.category,
            sprite = response.sprites.regular,
            shinySprite = response.sprites.shiny,
            evolutionsPre = response.evolutions?.pre?.map { it.pokedex_id } ?: emptyList(),
            evolutionsNext = response.evolutions?.next?.map { it.pokedex_id } ?: emptyList()
        )

        pokemonCache[id] = pokemon
        return pokemon
    }

    // ---------- LIST ----------
    suspend fun fetchAllPokemons(
        minId: Int = 1,
        maxId: Int = 1
    ): List<Pokemon> = coroutineScope {

        val key = minId to maxId

        // cache hit
        listCache[key]?.let { return@coroutineScope it }

        val list = (minId..maxId).map { id ->
            async { fetchPokemon(id) } // utilise déjà le cache single
        }.awaitAll()

        listCache[key] = list
        list
    }
}