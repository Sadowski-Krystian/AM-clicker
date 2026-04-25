package com.example.am_clicker

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CreditsScreen(onNavigateBack: () -> Unit) {
    // Defines the spinning animation
    val infiniteTransition = rememberInfiniteTransition(label = "spin")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing), // 8 seconds per full rotation
            repeatMode = RepeatMode.Restart
        ),
        label = "logo_spin"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BackgroundDark, BackgroundPurple)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- TOP BAR ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, bottom = 24.dp), // Added top padding for status bar area
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { onNavigateBack() }
                )
                Text(
                    text = stringResource(id = R.string.credits_title),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.size(28.dp)) // To keep title perfectly centered
            }

            // --- HEADER ---
            Icon(
                imageVector = Icons.Default.RocketLaunch,
                contentDescription = "Logo",
                tint = Color(0xFFD8B4E2),
                modifier = Modifier
                    .size(80.dp)
                    .rotate(angle) // Applies the spinning animation!
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Asteroid Clicker", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Text(stringResource(id = R.string.app_version), color = Color(0xFFD8B4E2), fontSize = 16.sp)
            Spacer(modifier = Modifier.height(32.dp))

            // --- CARDS ---
            CreditCard(
                title = stringResource(id = R.string.credits_programming),
                icon = Icons.Default.Code,
                items = listOf(
                    stringResource(id = R.string.credits_prog_item1),
                    stringResource(id = R.string.credits_prog_item2),
                    stringResource(id = R.string.credits_prog_item3)
                )
            )

            CreditCard(
                title = stringResource(id = R.string.credits_design),
                icon = Icons.Default.Palette,
                items = listOf(
                    stringResource(id = R.string.credits_design_item1),
                    stringResource(id = R.string.credits_design_item2),
                    stringResource(id = R.string.credits_design_item3)
                )
            )

            CreditCard(
                title = stringResource(id = R.string.credits_tech),
                icon = Icons.Default.Build,
                items = listOf(
                    stringResource(id = R.string.credits_tech_item1),
                    stringResource(id = R.string.credits_tech_item2),
                    stringResource(id = R.string.credits_tech_item3)
                )
            )

            // --- THANK YOU CARD ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(listOf(Color(0xFF8E24AA), Color(0xFF4A148C))))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = Color(0xFFFF4081), modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(stringResource(id = R.string.credits_thanks_title), color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(id = R.string.credits_thanks_desc),
                        color = Color(0xFFD8B4E2),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // --- FOOTER ---
            Spacer(modifier = Modifier.height(32.dp))
            Text(stringResource(id = R.string.credits_made_with), color = Color(0xFF9C27B0), fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(stringResource(id = R.string.credits_copyright), color = Color(0xFF6A1B9A), fontSize = 12.sp, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(16.dp))
            Text(stringResource(id = R.string.credits_hint), color = Color.Yellow, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

// Reusable component for the list cards
@Composable
fun CreditCard(title: String, icon: ImageVector, items: List<String>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(16.dp)) // Subtle border
            .background(Color(0xFF4A148C).copy(alpha = 0.4f)) // Semi-transparent card background
            .padding(20.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF8E24AA)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
            items.forEach { item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
                ) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFFD8B4E2)))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(item, color = Color(0xFFD8B4E2), fontSize = 14.sp)
                }
            }
        }
    }
}