package com.example.am_clicker
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main_menu") {
        composable("main_menu") {
            val menuViewModel: MainMenuViewModel = viewModel()
            MainMenuScreen(
                viewModel = menuViewModel,
                onNavigateToGame = { navController.navigate("game_screen") },
                onNavigateToProfile = { navController.navigate("profile_screen") },
                // 1. ADD THE ROUTES FOR THE NEW BUTTONS
                onNavigateToAchievements = { navController.navigate("achievements_screen") },
                onNavigateToCredits = { navController.navigate("credits_screen") }
            )
        }

        composable("game_screen") { GameScreenPlaceholder() }
        composable("profile_screen") { ProfileScreenPlaceholder() }

        // 2. ADD THE NEW ROUTES TO THE NAVHOST
        composable("achievements_screen") { AchievementsScreenPlaceholder() }
        composable("credits_screen") {
            CreditsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

// Existing Placeholders
@Composable
fun GameScreenPlaceholder() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Game Screen", fontSize = 24.sp, color = Color.White)
    }
}

@Composable
fun ProfileScreenPlaceholder() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Profile Screen", fontSize = 24.sp, color = Color.White)
    }
}

// 3. ADD THE NEW PLACEHOLDER SCREENS AT THE BOTTOM
@Composable
fun AchievementsScreenPlaceholder() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Osiągnięcia Screen", fontSize = 24.sp, color = Color.White)
    }
}

@Composable
fun CreditsScreenPlaceholder() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Kredyty Screen", fontSize = 24.sp, color = Color.White)
    }
}