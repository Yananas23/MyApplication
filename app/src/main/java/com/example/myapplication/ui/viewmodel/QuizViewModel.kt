package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.Pokemon
import com.example.myapplication.data.repository.PokemonRepository
import com.example.myapplication.quiz.generateOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class QuizViewModel(private val gen: Int) : ViewModel() {

    // ---------- constants ----------
    private val questionDurationMs = 10_000L
    private val min = listOf(1, 152, 252, 387, 494, 650, 722, 810, 906, 1)
    private val max = listOf(151, 251, 386, 493, 649, 721, 809, 905, 1025, 1025)
    private val maxQuestions = 10

    // ---------- state ----------
    private val _pokemons = MutableStateFlow<List<Pokemon>>(emptyList())
    val pokemons: StateFlow<List<Pokemon>> = _pokemons

    private val _currentIndex = MutableStateFlow(1)
    val currentIndex: StateFlow<Int> = _currentIndex

    private val _currentPokemon = MutableStateFlow<Pokemon?>(null)
    val currentPokemon: StateFlow<Pokemon?> = _currentPokemon

    private val _options = MutableStateFlow<List<String>>(emptyList())
    val options: StateFlow<List<String>> = _options

    private val _selectedOption = MutableStateFlow<String?>(null)
    val selectedOption: StateFlow<String?> = _selectedOption

    private val _isAnswered = MutableStateFlow(false)
    val isAnswered: StateFlow<Boolean> = _isAnswered

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score

    private val _indiceType = MutableStateFlow(false)
    val indiceType: StateFlow<Boolean> = _indiceType

    private val _indiceFifty = MutableStateFlow(false)
    val indiceFifty: StateFlow<Boolean> = _indiceFifty

    private val _disabledOptions = MutableStateFlow<List<String>>(emptyList())
    val disabledOptions: StateFlow<List<String>> = _disabledOptions

    private val _timeLeft = MutableStateFlow(1f)
    val timeLeft: StateFlow<Float> = _timeLeft

    private val _quizFinished = MutableStateFlow(false)
    val quizFinished: StateFlow<Boolean> = _quizFinished

    private var questionDeadline = 0L

    init {
        loadPokemons()
    }

    private fun loadPokemons() {
        viewModelScope.launch {
            _pokemons.value = try {
                PokemonRepository.fetchAllPokemons(min[gen - 1], max[gen - 1])
            } catch (e: Exception) {
                emptyList()
            }

            nextQuestion()
        }
    }

    private fun nextQuestion() {
        if (_currentIndex.value >= maxQuestions) {
            _quizFinished.value = true
            return
        }

        val list = _pokemons.value
        if (list.isEmpty()) return

        val pokemon = list.random()
        _currentPokemon.value = pokemon
        _options.value = generateOptions(list, pokemon)
        _selectedOption.value = null
        _isAnswered.value = false
        _indiceType.value = false
        _indiceFifty.value = false
        _disabledOptions.value = emptyList()

        questionDeadline = System.currentTimeMillis() + questionDurationMs
        _timeLeft.value = 1f

        viewModelScope.launch {
            while (!_isAnswered.value && !_quizFinished.value) {
                val now = System.currentTimeMillis()
                val remaining = questionDeadline - now
                _timeLeft.value = (remaining.toFloat() / questionDurationMs).coerceIn(0f, 1f)

                if (remaining <= 0) {
                    answerQuestion(pokemon.nameFr, timeout = true)
                    break
                }

                delay(50)
            }
        }
    }

    fun useTypeHint() {
        if (!_isAnswered.value && !_indiceType.value) {
            _indiceType.value = true
        }
    }

    fun useFiftyFifty() {
        if (!_isAnswered.value && !_indiceFifty.value) {
            val correct = _currentPokemon.value?.nameFr ?: return
            val keepWrong = _options.value.filter { it != correct }.random()
            _disabledOptions.value = _options.value.filter { it != correct && it != keepWrong }
            _indiceFifty.value = true
        }
    }

    fun answerQuestion(answer: String, timeout: Boolean = false) {
        if (_isAnswered.value) return
        _isAnswered.value = true
        _selectedOption.value = answer

        val pokemon = _currentPokemon.value ?: return

        // ---------- calculate score ----------
        var questionScore = 1000
        if (!timeout) {
            val percentLeft = (_timeLeft.value * 10).toInt() // 0-10
            questionScore = (questionScore * (percentLeft / 10f)).toInt()
        } else {
            questionScore = 0
        }

        if (_indiceType.value) questionScore = (questionScore * 0.75f).toInt()
        if (_indiceFifty.value) questionScore = (questionScore * 0.75f).toInt()

        if (answer == pokemon.nameFr) {
            _score.value += questionScore
        }

        viewModelScope.launch {
            delay(2000)
            _currentIndex.value += 1
            nextQuestion()
        }
    }
}