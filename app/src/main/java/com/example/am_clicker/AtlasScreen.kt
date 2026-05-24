package com.example.am_clicker

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// Klasa pomocnicza dla naszych 16 obiektów
data class CelestialBody(
    val id: Int,
    val name: String,
    val cost: Long,
    val imageResId: Int // ID obrazka z folderu drawable
)

@Composable
fun AtlasScreen(
    viewModel: GameViewModel,
    onNavigateBack: () -> Unit
) {
    val gameState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentCash = gameState.currentCash

    // Odczytujemy prawdziwe odblokowane planety z bazy danych
    val unlockedBodies by viewModel.unlockedPlanets.collectAsStateWithLifecycle()

    // Lista 16 obiektów do kupienia (8 planet + 8 innych)
    val celestialBodies = remember {
        listOf(
            CelestialBody(1, "Merkury", 100L, android.R.drawable.ic_menu_help), // Podmień ikony na swoje R.drawable.merkury itd.
            CelestialBody(2, "Wenus", 500L, android.R.drawable.ic_menu_help),
            CelestialBody(3, "Ziemia", 1500L, android.R.drawable.ic_menu_help),
            CelestialBody(4, "Mars", 4000L, android.R.drawable.ic_menu_help),
            CelestialBody(5, "Jowisz", 10000L, android.R.drawable.ic_menu_help),
            CelestialBody(6, "Saturn", 25000L, android.R.drawable.ic_menu_help),
            CelestialBody(7, "Uran", 60000L, android.R.drawable.ic_menu_help),
            CelestialBody(8, "Neptun", 150000L, android.R.drawable.ic_menu_help),
            CelestialBody(9, "Księżyc", 300000L, android.R.drawable.ic_menu_help),
            CelestialBody(10, "Słońce", 1000000L, android.R.drawable.ic_menu_help),
            CelestialBody(11, "Syriusz", 5000000L, android.R.drawable.ic_menu_help),
            CelestialBody(12, "Czarna Dziura", 15000000L, android.R.drawable.ic_menu_help),
            CelestialBody(13, "Kwazar", 50000000L, android.R.drawable.ic_menu_help),
            CelestialBody(14, "Pulsar", 100000000L, android.R.drawable.ic_menu_help),
            CelestialBody(15, "Mgławica", 500000000L, android.R.drawable.ic_menu_help),
            CelestialBody(16, "Galaktyka", 1000000000L, android.R.drawable.ic_menu_help)
        )
    }

    // Tymczasowy stan odblokowanych planet (później przeniesiemy to do bazy)
//    var unlockedBodies by remember { mutableStateOf(setOf<Int>()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            // Zakładam, że masz zdefiniowane kolory BackgroundDark i BackgroundPurple w swoim motywie
            // Jeśli nie, podmień na wpisane ręcznie kolory np. Color(0xFF130B29)
            .background(Brush.verticalGradient(listOf(Color(0xFF130B29), Color(0xFF4A148C))))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // --- TOP BAR ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, bottom = 16.dp, start = 24.dp, end = 24.dp),
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
                    text = "Kosmiczny Atlas",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.size(28.dp))
            }

            // --- PORTFEL GRACZA ---
            Text(
                text = "Twoja gotówka: $currentCash",
                color = Color(0xFF81C784),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))

            // --- SIATKA OBIEKTÓW ---
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // 2 kolumny obrazków
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(celestialBodies) { body ->
                    val isUnlocked = unlockedBodies.contains(body.id)
                    val canAfford = currentCash >= body.cost

                    AtlasCard(
                        body = body,
                        isUnlocked = isUnlocked,
                        canAfford = canAfford,
                        onClick = {
                            if (!isUnlocked && canAfford) {
                                // Wywołujemy nową funkcję z bazy!
                                viewModel.buyPlanet(body.id, body.cost)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AtlasCard(
    body: CelestialBody,
    isUnlocked: Boolean,
    canAfford: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.85f)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = if (isUnlocked) 2.dp else 1.dp,
                color = if (isUnlocked) Color(0xFFD8B4E2) else Color(0x33FFFFFF),
                shape = RoundedCornerShape(16.dp)
            )
            .background(if (isUnlocked) Color(0xFF4A148C).copy(alpha = 0.6f) else Color(0x33000000))
            .clickable(enabled = !isUnlocked && canAfford, onClick = onClick)
            .padding(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            // Obrazek planety
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (isUnlocked) {
                    Image(
                        painter = painterResource(id = body.imageResId),
                        contentDescription = body.name,
                        modifier = Modifier.fillMaxSize(0.8f)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Zablokowane",
                        tint = Color.Gray,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Nazwa
            Text(
                text = if (isUnlocked) body.name else "???",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            // Cena
            if (!isUnlocked) {
                Text(
                    text = "Koszt: ${body.cost}",
                    color = if (canAfford) Color(0xFF81C784) else Color(0xFFE57373),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            } else {
                Text(
                    text = "Odblokowano!",
                    color = Color(0xFFD8B4E2),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}