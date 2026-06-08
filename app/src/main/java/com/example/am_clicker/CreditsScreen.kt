package com.example.am_clicker

import android.net.Uri
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

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

    // Zmienne do odtwarzacza wideo i audio
    var isFullscreen by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val videoUri = remember {
        Uri.parse("android.resource://${context.packageName}/${R.raw.mp4}")
    }
    // NOWOŚĆ: Ścieżka do pliku audio (upewnij się, że masz plik res/raw/audio)
    val audioUri = remember {
        Uri.parse("android.resource://${context.packageName}/${R.raw.mp3}")
    }

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
                    .padding(top = 48.dp, bottom = 24.dp),
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
                Spacer(modifier = Modifier.size(28.dp))
            }

            // --- HEADER ---
            Icon(
                imageVector = Icons.Default.RocketLaunch,
                contentDescription = "Logo",
                tint = Color(0xFFD8B4E2),
                modifier = Modifier
                    .size(80.dp)
                    .rotate(angle)
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

            // --- WIDEO PROMOCYJNE ---
            Spacer(modifier = Modifier.height(32.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PlayCircleOutline, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Trailer", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (!isFullscreen) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(16.dp))
                        .background(Color.Black)
                ) {
                    PromoVideoPlayer(videoUri = videoUri)

                    IconButton(
                        onClick = { isFullscreen = true },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    ) {
                        Icon(Icons.Default.Fullscreen, contentDescription = "Pełny ekran", tint = Color.White)
                    }
                }
            }

            // --- NOWOŚĆ: ODTWARZACZ AUDIO ---
            Spacer(modifier = Modifier.height(32.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ścieżka dźwiękowa", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))

            PromoAudioPlayer(audioUri = audioUri)

            // --- FOOTER ---
            Spacer(modifier = Modifier.height(48.dp))
            Text(stringResource(id = R.string.credits_made_with), color = Color(0xFF9C27B0), fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(stringResource(id = R.string.credits_copyright), color = Color(0xFF6A1B9A), fontSize = 12.sp, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(16.dp))
            Text(stringResource(id = R.string.credits_hint), color = Color.Yellow, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(48.dp))
        }
    }

    // --- LOGIKA PEŁNEGO EKRANU ---
    if (isFullscreen) {
        Dialog(
            onDismissRequest = { isFullscreen = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                PromoVideoPlayer(videoUri = videoUri)

                IconButton(
                    onClick = { isFullscreen = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Default.FullscreenExit, contentDescription = "Wyjdź", tint = Color.White)
                }
            }
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
            .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(16.dp))
            .background(Color(0xFF4A148C).copy(alpha = 0.4f))
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

// Komponent odtwarzacza wideo Media3
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun PromoVideoPlayer(videoUri: Uri) {
    val context = LocalContext.current

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUri))
            prepare()
            playWhenReady = false
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                useController = true
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

// --- NOWOŚĆ: KOMPONENT ODTWARZACZA AUDIO ---
@Composable
fun PromoAudioPlayer(audioUri: Uri) {
    val context = LocalContext.current

    // Inicjalizacja ExoPlayer dla audio
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(audioUri))
            prepare()
            playWhenReady = false
        }
    }

    var isPlaying by remember { mutableStateOf(false) }

    // Słuchacz stanu odtwarzacza, aby Compose wiedział, kiedy muzyka gra
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }
        }
        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    // Wygląd odtwarzacza dopasowany do stylu Twoich kart
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(16.dp))
            .background(Color(0xFF4A148C).copy(alpha = 0.4f))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Ikonka nuty w fioletowym boksie
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF8E24AA)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Opis utworu
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Main Theme OST",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Naciśnij graj, aby posłuchać",
                    color = Color(0xFFD8B4E2),
                    fontSize = 13.sp
                )
            }

            // Okrągły przycisk Play / Pause
            IconButton(
                onClick = {
                    if (isPlaying) exoPlayer.pause() else exoPlayer.play()
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFF8E24AA), CircleShape)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pauza" else "Graj",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}