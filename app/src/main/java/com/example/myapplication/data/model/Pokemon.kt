package com.example.myapplication.data.model

data class Pokemon(
    val id: Int,
    val nameFr: String,
    val nameEn: String,
    val nameJp: String,
    val typeNames: List<String>,
    val typeImages: List<String>,
    val category: String,
    val sprite: String?,
    val shinySprite: String?,
    val evolutionsPre: List<Int>,
    val evolutionsNext: List<Int>
)