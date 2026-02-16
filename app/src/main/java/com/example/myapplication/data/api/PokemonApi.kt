package com.example.myapplication.data.api

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Path

interface PokemonApi {
    @GET("/pokemon/{id}")
    suspend fun getPokemon(
        @Path("id") id: Int
    ): TyradexPokemonResponse
}

data class TyradexPokemonResponse(
    val pokedex_id: Int,
    val name: PokemonName,
    val types: List<PokemonType>,
    val category: String,
    val sprites: PokemonSprites,

    @SerializedName("evolution")
    val evolutions: PokemonEvolutions?
)

data class PokemonName(
    val fr: String,
    val en: String,
    val jp: String
)

data class PokemonType(
    val name: String,
    val image: String
)

data class PokemonSprites(
    val regular: String?,
    val shiny: String?
)

data class PokemonEvolutions(
    val pre: List<PokemonEvolution>?,
    val next: List<PokemonEvolution>?
)

data class PokemonEvolution(
    val pokedex_id: Int,
    val name: String
)