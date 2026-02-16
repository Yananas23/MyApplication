package com.example.myapplication.leaderboard

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

// -----------------------------
// DTO
// -----------------------------
data class ScoreDto(
    val id: Int? = null,
    val username: String,
    val score: Int
)

// -----------------------------
// API interface
// -----------------------------
interface LeaderboardApi {

    @POST("score")
    suspend fun pushScore(@Body score: ScoreDto)

    @GET("leaderboard")
    suspend fun getLeaderboard(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): List<ScoreDto>
}

// -----------------------------
// LeaderboardUtils
// -----------------------------
object LeaderboardUtils {

    private const val PAGE_SIZE = 10

    private val api: LeaderboardApi by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/") // ton backend local
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LeaderboardApi::class.java)
    }

    private var currentOffset = 0
    private val cache = mutableListOf<ScoreDto>()

    // Push un score avec pseudo
    suspend fun pushScore(username: String, score: Int): Result<Unit> {
        return try {
            api.pushScore(ScoreDto(username = username, score = score))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Reset pagination
    fun resetPagination() {
        currentOffset = 0
        cache.clear()
    }

    // Charger top 10
    suspend fun loadFirstPage(): Result<List<ScoreDto>> {
        return try {
            resetPagination()
            val data = api.getLeaderboard(
                offset = currentOffset,
                limit = PAGE_SIZE
            )
            cache.addAll(data)
            currentOffset += data.size
            Result.success(cache.toList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Charger 10 de plus
    suspend fun loadMore(): Result<List<ScoreDto>> {
        return try {
            val data = api.getLeaderboard(
                offset = currentOffset,
                limit = PAGE_SIZE
            )
            if (data.isNotEmpty()) {
                cache.addAll(data)
                currentOffset += data.size
            }
            Result.success(cache.toList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Récupérer le cache actuel
    fun getCached(): List<ScoreDto> = cache.toList()
}