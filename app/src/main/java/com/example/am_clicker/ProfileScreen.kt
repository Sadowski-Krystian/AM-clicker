package com.example.am_clicker

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.am_clicker.data.GameDatabase
import com.example.am_clicker.data.GameRepository
import com.example.am_clicker.GameViewModel
import com.example.am_clicker.GameViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onNavigateBack: () -> Unit) {
    // 1. Inicjalizacja bazy i viewmodelu
    val context = LocalContext.current
    val database = remember { GameDatabase.getInstance(context) }
    val repository = remember { GameRepository(database.gameDao) }
    val viewModel: GameViewModel = viewModel(factory = GameViewModelFactory(repository))

    // 2. Pobieranie stanu z viewmodelu
    val gameState by viewModel.uiState.collectAsStateWithLifecycle()

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF2A1055), Color(0xFF130B29))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
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
                text = stringResource(R.string.screen_profile_title),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.size(28.dp))
        }

        // --- STATS GRID ---
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ProfileStatCard(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.resources_label),
                value = gameState.currentCash.toString(),
                titleColor = Color(0xFFD8B4E2),
                bgBrush = Brush.horizontalGradient(listOf(Color(0xFF4B1D82), Color(0xFF7A1E5D)))
            )
            ProfileStatCard(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.profile_clicks),
                value = gameState.totalClicks.toString(),
                titleColor = Color(0xFF8AB4F8),
                backgroundColor = Color(0xFF283593)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ProfileStatCard(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.profile_upgrades),
                value = gameState.totalUpgradesBought.toString(),
                titleColor = Color(0xFF81C784),
                backgroundColor = Color(0xFF2A1055),
                borderColor = Color(0xFF388E3C)
            )
            ProfileStatCard(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.profile_achievements),
                value = gameState.totalAchievementsUnlocked.toString(),
                titleColor = Color(0xFFFFD54F),
                backgroundColor = Color(0xFF4A148C),
                borderColor = Color(0xFFFBC02D)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- USTAWIENIA ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF321A65))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = stringResource(R.string.profile_settings),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Dźwięk
                SettingRow(
                    icon = { Icon(Icons.Default.VolumeUp, contentDescription = null, tint = Color(0xFF8AB4F8)) },
                    title = stringResource(R.string.profile_sound),
                    control = {
                        Switch(
                            checked = gameState.isSoundEnabled,
                            onCheckedChange = { viewModel.updateSoundSettings(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF8AB4F8))
                        )
                    }
                )

                // Wibracje
                SettingRow(
                    icon = { Icon(Icons.Default.Vibration, contentDescription = null, tint = Color(0xFFD8B4E2)) },
                    title = stringResource(R.string.profile_vibration),
                    control = {
                        Switch(
                            checked = gameState.isVibrationEnabled,
                            onCheckedChange = { viewModel.updateVibrationSettings(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFFD8B4E2))
                        )
                    }
                )

                // Język (Dropdown)
                var languageDropdownExpanded by remember { mutableStateOf(false) }
                val languageOptions = listOf(
                    "system" to "AUTO",
                    "pl" to "Polski",
                    "en" to "English"
                )
                val selectedOptionText = languageOptions.find { it.first == gameState.selectedLanguage }?.second ?: "AUTO"

                SettingRow(
                    icon = { Icon(Icons.Default.Language, contentDescription = null, tint = Color(0xFF81C784)) },
                    title = stringResource(R.string.profile_language),
                    control = {
                        ExposedDropdownMenuBox(
                            expanded = languageDropdownExpanded,
                            onExpandedChange = { languageDropdownExpanded = !languageDropdownExpanded }
                        ) {
                            TextField(
                                value = selectedOptionText,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = languageDropdownExpanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .width(130.dp),
                                colors = ExposedDropdownMenuDefaults.textFieldColors(
                                    focusedContainerColor = Color(0xFF4A148C),
                                    unfocusedContainerColor = Color(0xFF4A148C),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )

                            ExposedDropdownMenu(
                                expanded = languageDropdownExpanded,
                                onDismissRequest = { languageDropdownExpanded = false },
                                modifier = Modifier.background(Color(0xFF321A65))
                            ) {
                                languageOptions.forEach { (code, label) ->
                                    DropdownMenuItem(
                                        text = { Text(label, color = Color.White) },
                                        onClick = {
                                            viewModel.updateLanguage(code)
                                            val localeList = if (code == "system") {
                                                LocaleListCompat.getEmptyLocaleList()
                                            } else {
                                                LocaleListCompat.forLanguageTags(code)
                                            }
                                            AppCompatDelegate.setApplicationLocales(localeList)
                                            languageDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- STREFA NIEBEZPIECZNA ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF2C1625))
                .border(1.dp, Color(0xFFB3261E), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = stringResource(R.string.profile_danger_zone),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = stringResource(R.string.profile_danger_desc),
                    color = Color(0xFFE57373),
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Button(
                    onClick = { viewModel.clearAllData() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.profile_clear_data), color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Composable
fun ProfileStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    titleColor: Color,
    backgroundColor: Color = Color.Transparent,
    bgBrush: Brush? = null,
    borderColor: Color = Color(0x1AFFFFFF)
) {
    val boxModifier = modifier
        .clip(RoundedCornerShape(12.dp))
        .border(1.dp, borderColor, RoundedCornerShape(12.dp))
        .then(
            if (bgBrush != null) Modifier.background(bgBrush)
            else Modifier.background(backgroundColor)
        )
        .padding(vertical = 20.dp, horizontal = 8.dp)

    Box(
        modifier = boxModifier,
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = title, color = titleColor, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SettingRow(icon: @Composable () -> Unit, title: String, control: @Composable () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        control()
    }
}