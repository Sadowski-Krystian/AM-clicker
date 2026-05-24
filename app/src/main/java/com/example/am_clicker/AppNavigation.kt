package com.example.am_clicker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.am_clicker.data.GameDatabase
import com.example.am_clicker.data.GameRepository

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val context = LocalContext.current
    val database = remember { GameDatabase.getInstance(context) }
    val repository = remember { GameRepository(database.gameDao) }
    val gameViewModel: GameViewModel = viewModel(factory = GameViewModelFactory(repository))

    NavHost(navController = navController, startDestination = "main_menu") {
        composable("main_menu") {
            val menuViewModel: MainMenuViewModel = viewModel()
            MainMenuScreen(
                viewModel = menuViewModel,
                onNavigateToGame = { navController.navigate("game_screen") },
                onNavigateToProfile = { navController.navigate("profile_screen") },
                onNavigateToAchievements = { navController.navigate("achievements_screen") },
                onNavigateToCredits = { navController.navigate("credits_screen") },
                // Dodajemy nawigację do atlasu (będziesz musiał dodać ten parametr w MainMenuScreen)
                onNavigateToAtlas = { navController.navigate("atlas_screen") }
            )
        }

        composable("game_screen") {
            GameScreen(
                viewModel = gameViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAchievements = { navController.navigate("achievements_screen") }
                // Możesz tu też dodać onNavigateToAtlas, jeśli wolisz sklep wewnątrz gry!
            )
        }

        composable("profile_screen") {
            ProfileScreen(
                viewModel = gameViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("achievements_screen") {
            AchievementsScreen(
                viewModel = gameViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("credits_screen") {
            CreditsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // --- NOWY EKRAN KOSMICZNEGO ATLASU ---
        composable("atlas_screen") {
            AtlasScreen(
                viewModel = gameViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}