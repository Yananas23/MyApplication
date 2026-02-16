package com.example.myapplication.leaderboard

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

data class ScoreDto(
    val id: Int? = null,
    val username: String,
    val score: Int
)

object LeaderboardUtils {

    private const val PAGE_SIZE = 10
    private const val FILE_NAME = "leaderboard_local.json"

    private var currentOffset = 0
    private val cache = mutableListOf<ScoreDto>()
    private val allScores = mutableListOf<ScoreDto>()

    private lateinit var file: File
    private val gson = Gson()

    // Initialisation
    fun init(context: Context) {

        file = File(context.filesDir, FILE_NAME)

        // Si le fichier n'existe pas encore → copier depuis assets
        if (!file.exists()) {
            val json = context.assets.open("leaderboard.json")
                .bufferedReader()
                .use { it.readText() }

            file.writeText(json)
        }

        loadFromFile()
    }

    private fun loadFromFile() {
        val json = file.readText()
        val type = object : TypeToken<List<ScoreDto>>() {}.type
        val list: List<ScoreDto> = gson.fromJson(json, type)

        allScores.clear()
        allScores.addAll(list.sortedByDescending { it.score })
    }

    private fun saveToFile() {
        file.writeText(gson.toJson(allScores))
    }

    fun pushScore(username: String, score: Int): Result<Unit> {
        return try {
            val newId = (allScores.maxOfOrNull { it.id ?: 0 } ?: 0) + 1
            val newScore = ScoreDto(newId, username, score)

            allScores.add(newScore)
            allScores.sortByDescending { it.score }

            saveToFile() // 🔥 sauvegarde persistante

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun resetPagination() {
        currentOffset = 0
        cache.clear()
    }

    fun loadFirstPage(): Result<List<ScoreDto>> {
        return try {
            resetPagination()
            val data = allScores.take(PAGE_SIZE)
            cache.addAll(data)
            currentOffset = data.size
            Result.success(cache.toList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun loadMore(): Result<List<ScoreDto>> {
        return try {
            val next = allScores.drop(currentOffset).take(PAGE_SIZE)
            cache.addAll(next)
            currentOffset += next.size
            Result.success(cache.toList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCached(): List<ScoreDto> = cache.toList()
}
