package com.example.myapplication.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.myapplication.quiz.answerColor
import com.example.myapplication.quiz.generateOptions
import com.example.myapplication.navigation.Screen
import com.example.myapplication.ui.theme.LocalExtraColors
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.leaderboard.LeaderboardUtils
import com.example.myapplication.leaderboard.ScoreDto
import com.example.myapplication.ui.viewmodel.QuizViewModel
import com.example.myapplication.ui.viewmodel.QuizViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizPokemon(
    navController: NavController,
    gen: Int,
    viewModel: QuizViewModel = viewModel(factory = QuizViewModelFactory(gen))
) {
    val context = LocalContext.current
    val extra = LocalExtraColors.current

    val currentPokemon by viewModel.currentPokemon.collectAsState()
    val options by viewModel.options.collectAsState()
    val selectedOption by viewModel.selectedOption.collectAsState()
    val isAnswered by viewModel.isAnswered.collectAsState()
    val indiceType by viewModel.indiceType.collectAsState()
    val indiceFifty by viewModel.indiceFifty.collectAsState()
    val disabledOptions by viewModel.disabledOptions.collectAsState()
    val timeLeft by viewModel.timeLeft.collectAsState()
    val currentIndex by viewModel.currentIndex.collectAsState()
    val score by viewModel.score.collectAsState()
    val quizFinished by viewModel.quizFinished.collectAsState()

    // navigation vers écran résultat
    LaunchedEffect(quizFinished) {
        if (quizFinished) {
            navController.navigate(Screen.QuizDone.route.replace("{score}", score.toString())) {
                popUpTo(Screen.QuizPokemon.route) { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("C'est le quizz !") },
                navigationIcon = {
                    IconButton({ navController.navigate(Screen.Home.route) }) {
                        Text("←")
                    }
                }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            Column(modifier = Modifier.fillMaxSize()) {

                // ===================== HAUT
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(extra.topColor),
                    contentAlignment = Alignment.Center
                ) {

                    // arc vert haut
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth()
                            .height(70.dp)
                            .clip(
                                RoundedCornerShape(
                                    bottomStartPercent = 100,
                                    bottomEndPercent = 100
                                )
                            )
                            .background(extra.greenAccent)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        Text(
                            "Question ${currentIndex}/10",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(Modifier.height(16.dp))

                        currentPokemon?.let { pokemon ->
                            AsyncImage(
                                model = pokemon.sprite,
                                contentDescription = pokemon.nameFr,
                                modifier = Modifier.size(200.dp),
                                colorFilter = if (isAnswered) null else ColorFilter.tint(extra.shadow)
                            )
                        }
                    }
                }

                // ===================== BAS
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(extra.bottomColor),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // ---------- réponses ----------
                        options.chunked(2).forEach { rowOptions ->
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                rowOptions.forEach { option ->

                                    val disabledByFifty = indiceFifty && option in disabledOptions && !isAnswered

                                    Button(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(6.dp),
                                        enabled = !disabledByFifty,
                                        colors = if (disabledByFifty) {
                                            ButtonDefaults.buttonColors(
                                                disabledContainerColor = Color.LightGray
                                            )
                                        } else {
                                            answerColor(
                                                option,
                                                selectedOption,
                                                currentPokemon?.nameFr ?: ""
                                            )
                                        },
                                        onClick = { viewModel.answerQuestion(option) }
                                    ) {
                                        Text(option)
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // ---------- indices ----------
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {

                            Button(
                                onClick = { viewModel.useTypeHint() },
                                enabled = !isAnswered && !indiceType && timeLeft <= 0.75f
                            ) {
                                Text(if (timeLeft <= 0.75f) "Type" else "Type 🔒")
                            }

                            Button(
                                onClick = { viewModel.useFiftyFifty() },
                                enabled = !isAnswered && !indiceFifty && timeLeft <= 0.5f
                            ) {
                                Text(if (timeLeft <= 0.5f) "50/50" else "50/50 🔒")
                            }
                        }

                        if (indiceType) {
                            Spacer(Modifier.height(8.dp))
                            currentPokemon?.typeNames?.forEachIndexed { i, name ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    AsyncImage(
                                        model = currentPokemon!!.typeImages.getOrNull(i),
                                        contentDescription = name,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(name, color = extra.shadow)
                                }
                            }
                        }
                    }

                    // arc vert bas
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(70.dp)
                            .clip(
                                RoundedCornerShape(
                                    topStartPercent = 100,
                                    topEndPercent = 100
                                )
                            )
                            .background(extra.greenAccent)
                    )
                }
            }

            // ===================== BANDE TIMER
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .height(18.dp)
                    .background(extra.bandeOuter)
            ) {
                LinearProgressIndicator(
                    progress = timeLeft,
                    modifier = Modifier.fillMaxSize(),
                    color = extra.greenAccent,
                    trackColor = Color.DarkGray
                )
            }

            // ===================== BOUTON CENTRAL
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(72.dp)
                    .background(extra.boutonOuter, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(extra.boutonInter, CircleShape)
                )
            }
        }
    }
}

@Composable
fun QuizDone(
    navController: NavController,
    score: Int
) {
    val scope = rememberCoroutineScope()

    var username by remember { mutableStateOf("") }
    var topScores by remember { mutableStateOf<List<ScoreDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var scoreSent by remember { mutableStateOf(false) }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Ton score est de $score points",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))

            // -------------------------------
            // Zone pseudo
            // -------------------------------
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Ton pseudo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // -------------------------------
            // Bouton envoyer score
            // -------------------------------
            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        errorMsg = null
                        try {
                            LeaderboardUtils.pushScore(username, score)
                            val result = LeaderboardUtils.loadFirstPage()
                            topScores = result.getOrElse { emptyList() }.take(5)
                            scoreSent = true
                        } catch (e: Exception) {
                            errorMsg = e.message
                        } finally {
                            isLoading = false
                        }
                    }
                },
                enabled = username.isNotBlank() && !scoreSent,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (scoreSent) "Score envoyé !" else "Envoyer mon score")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // -------------------------------
            // Top 5 leaderboard
            // -------------------------------
            if (isLoading) {
                CircularProgressIndicator()
            } else if (errorMsg != null) {
                Text("Erreur : $errorMsg", color = Color.Red)
            } else if (topScores.isNotEmpty()) {
                Text("Top 5 :", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                topScores.forEachIndexed { index, s ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${index + 1}. ${s.username}")
                        Text("${s.score}")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // -------------------------------
            // Boutons navigation
            // -------------------------------
            Button(
                onClick = { navController.navigate(Screen.Home.route) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Retour à l'accueil")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { navController.navigate(Screen.GenQuiz.route) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Rejouer")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenQuiz(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("C'est le quizz !") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Screen.Home.route) }) {
                        Text("←")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Quelle génération ?",
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            for (i in 1..10) {
                Button(onClick = { navController.navigate(Screen.QuizPokemon.route + "/$i") }) {
                    if (i != 10) Text("Génération n°$i") else Text("Tous les pokémons")
                }
            }
        }
    }
}