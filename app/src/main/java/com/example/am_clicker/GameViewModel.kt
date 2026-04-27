package com.example.am_clicker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.am_clicker.data.GameRepository
import com.example.am_clicker.data.UserStatsEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GameViewModel(private val repository: GameRepository) : ViewModel() {

    // 1. THIS IS OUR STATE: It automatically updates the UI whenever the database changes
    val uiState: StateFlow<UserStatsEntity> = repository.userStats
        .map { stats ->
            // If the database is completely empty (first time opening app), create a default player
            if (stats == null) {
                val newPlayer = UserStatsEntity()
                repository.saveStats(newPlayer)
                newPlayer
            } else {
                stats
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserStatsEntity()
        )

    init {
        // 2. THE GAME LOOP: This runs continuously in the background for Auto-Mining
        viewModelScope.launch {
            while (true) {
                delay(1000) // Wait exactly 1 second
                val currentStats = uiState.value

                // If the player has auto-mining power, add it to their cash
                if (currentStats.passiveIncomePerSecond > 0) {
                    repository.saveStats(
                        currentStats.copy(
                            currentCash = currentStats.currentCash + currentStats.passiveIncomePerSecond,
                            totalCashEarned = currentStats.totalCashEarned + currentStats.passiveIncomePerSecond
                        )
                    )
                }
            }
        }
    }

    // 3. THE CLICK ACTION: Called whenever the asteroid is tapped
    fun onAsteroidClicked() {
        viewModelScope.launch {
            val currentStats = uiState.value

            repository.saveStats(
                currentStats.copy(
                    currentCash = currentStats.currentCash + currentStats.clickPower,
                    totalCashEarned = currentStats.totalCashEarned + currentStats.clickPower,
                    totalClicks = currentStats.totalClicks + 1
                )
            )
        }
    }
    fun updateSoundSettings(enabled: Boolean) {
        viewModelScope.launch {
            val currentStats = uiState.value
            repository.saveStats(currentStats.copy(isSoundEnabled = enabled))
        }
    }

    fun updateVibrationSettings(enabled: Boolean) {
        viewModelScope.launch {
            val currentStats = uiState.value
            repository.saveStats(currentStats.copy(isVibrationEnabled = enabled))
        }
    }

    fun updateLanguage(langCode: String) {
        viewModelScope.launch {
            val currentStats = uiState.value
            repository.saveStats(currentStats.copy(selectedLanguage = langCode))
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            // Nadpisujemy cały postęp domyślnymi (zerowymi) wartościami
            repository.saveStats(UserStatsEntity())
        }
    }
}

// 4. FACTORY: Because our ViewModel needs the Repository, we need a factory to tell Android how to build it.
class GameViewModelFactory(private val repository: GameRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


