package com.example.am_clicker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource


@Composable
fun MainMenuScreen(
    viewModel: MainMenuViewModel,
    onNavigateToGame: () -> Unit,
    onNavigateToProfile: () -> Unit,
    // 1. ADD THE TWO NEW CALLBACKS HERE:
    onNavigateToAchievements: () -> Unit,
    onNavigateToCredits: () -> Unit
) {
    // 2. UPDATE THIS LISTENER TO HANDLE ALL 4 EVENTS:
    LaunchedEffect(Unit) {
        viewModel.menuEvents.collect { event ->
            when (event) {
                is MainMenuEvent.NavigateToGame -> onNavigateToGame()
                is MainMenuEvent.NavigateToProfile -> onNavigateToProfile()
                is MainMenuEvent.NavigateToAchievements -> onNavigateToAchievements()
                is MainMenuEvent.NavigateToCredits -> onNavigateToCredits()
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(BackgroundDark, BackgroundPurple))),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
            Icon(Icons.Default.RocketLaunch, contentDescription = "Logo", tint = Color(0xFFD8B4E2), modifier = Modifier.size(80.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Asteroid", color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.Bold)
            Text("Clicker", color = Color(0xFFD8B4E2), fontSize = 20.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(48.dp))

            GradientButton(stringResource(id = R.string.menu_play), Icons.Default.PlayArrow, GradientPlay) { viewModel.onPlayClicked() }
            Spacer(modifier = Modifier.height(16.dp))
            GradientButton(stringResource(id = R.string.menu_profile), Icons.Default.Person, GradientProfile) { viewModel.onProfileClicked() }
            Spacer(modifier = Modifier.height(16.dp))
            GradientButton(stringResource(id = R.string.menu_achievements), Icons.Default.Star, GradientAchievements) { viewModel.onAchievementsClicked() }
            Spacer(modifier = Modifier.height(16.dp))
            GradientButton(stringResource(id = R.string.menu_credits), Icons.Default.Info, GradientCredits) { viewModel.onCreditsClicked() }

            Spacer(modifier = Modifier.height(48.dp))
            Text(stringResource(id = R.string.menu_subtitle), color = Color.LightGray, fontSize = 12.sp)
        }
    }
}

@Composable
fun GradientButton(text: String, icon: ImageVector, colors: List<Color>, onClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(16.dp))
            .background(Brush.horizontalGradient(colors)).clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}