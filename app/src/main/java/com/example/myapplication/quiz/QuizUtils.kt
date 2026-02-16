package com.example.myapplication.quiz

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.myapplication.data.model.Pokemon

fun generateOptions(pokemons: List<Pokemon>, correct: Pokemon): List<String> {
    val otherNames = pokemons.filter { it.nameFr != correct.nameFr }.map { it.nameFr }.shuffled()
    return (otherNames.take(3) + correct.nameFr).shuffled()
}

@Composable
fun answerColor(
    option: String,
    selected: String?,
    correct: String
): ButtonColors {

    if (selected == null)
        return ButtonDefaults.buttonColors()

    return when {
        option == correct ->
            ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))

        option == selected ->
            ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))

        else ->
            ButtonDefaults.buttonColors()
    }
}