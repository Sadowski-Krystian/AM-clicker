package com.example.am_clicker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.am_clicker.data.AchievementEntity
import com.example.am_clicker.data.GameRepository
import com.example.am_clicker.data.UserStatsEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GameViewModel(private val repository: GameRepository) : ViewModel() {

    // Nowa zmienna przechowująca AKTUALNĄ nazwę zalogowanego gracza.
    // Domyślnie ładujemy "Player1"
    private val currentUsername = MutableStateFlow("Player1")

    // 1. THIS IS OUR STATE
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<UserStatsEntity> = currentUsername
        .flatMapLatest { username ->
            // Kiedy currentUsername się zmieni, automatycznie pobieramy nowe dane z bazy!
            repository.getUserStats(username).map { stats ->
                if (stats == null) {
                    val newPlayer = UserStatsEntity(username = username)
                    repository.saveStats(newPlayer)
                    newPlayer
                } else {
                    stats
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserStatsEntity(username = "Player1")
        )

    // NOWA FUNKCJA: Wywołasz ją z ekranu profilu po kliknięciu "Zmień"
    fun switchUser(newUsername: String) {
        val trimmedName = newUsername.trim()
        if (trimmedName.isNotBlank() && trimmedName != currentUsername.value) {
            currentUsername.value = trimmedName
        }
    }

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
            // Pobieramy nazwę aktualnie zalogowanego gracza
            val currentUsername = uiState.value.username

            // Tworzymy czysty obiekt ze statystykami, ale wymuszamy nazwę obecnego gracza
            val resetStats = UserStatsEntity(username = currentUsername)

            // Zapisujemy wyzerowane dane pod konkretnego gracza
            repository.saveStats(resetStats)
        }
    }

    fun checkAndSaveAchievements() {
        viewModelScope.launch {
            val currentStats = uiState.value
            val username = currentStats.username

            // Zmienna do zliczania odblokowanych osiągnięć w tej sesji sprawdzania
            var unlockedCount = 0

            // 1. Sprawdzamy i zapisujemy osiągnięcia
            AchievementData.list.forEach { achievement ->
                val progressValue = when (achievement.type) {
                    AchievementType.CLICKS -> currentStats.totalClicks
                    AchievementType.CASH -> currentStats.totalCashEarned
                    AchievementType.UPGRADES -> currentStats.totalUpgradesBought.toLong()
                }

                val isUnlocked = progressValue >= achievement.targetValue

                // Jeśli odblokowane, zwiększamy nasz licznik
                if (isUnlocked) {
                    unlockedCount++
                }

                val entity = AchievementEntity(
                    username = username,
                    id = achievement.id,
                    progress = progressValue,
                    isUnlocked = isUnlocked
                )

                repository.saveAchievement(entity)
            }

            // 2. Aktualizujemy statystyki gracza, jeśli zdobył nowe osiągnięcie
            if (unlockedCount != currentStats.totalAchievementsUnlocked) {
                // Tworzymy kopię obecnych statystyk ze zaktualizowaną liczbą osiągnięć
                val updatedStats = currentStats.copy(
                    totalAchievementsUnlocked = unlockedCount
                )
                // Zapisujemy nowe statystyki do bazy danych
                repository.saveStats(updatedStats)
            }
        }
    }

    // --- ATLAS / PLANETY ---

    @OptIn(ExperimentalCoroutinesApi::class)
    val unlockedPlanets: StateFlow<Set<Int>> = currentUsername
        .flatMapLatest { username ->
            repository.getUnlockedPlanets(username)
        }
        .map { it.toSet() }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptySet())

    fun buyPlanet(planetId: Int, cost: Long) {
        viewModelScope.launch {
            // Zapisujemy bieżącego użytkownika w zmiennej
            val activeUser = currentUsername.value

            // Pobieramy aktualne statystyki TEGO KONKRETNEGO gracza prosto z bazy
            val currentStats = repository.getUserStats(activeUser).firstOrNull() ?: return@launch

            if (currentStats.currentCash >= cost) {
                // 1. Odejmujemy koszt od obecnej gotówki
                val newCash = currentStats.currentCash - cost

                // 2. Aktualizujemy statystyki w bazie (gracz traci pieniądze)
                val updatedStats = currentStats.copy(currentCash = newCash)
                repository.saveStats(updatedStats)

                // 3. Dodajemy planetę do bazy jako odblokowaną z poprawną nazwą użytkownika
                repository.unlockPlanet(planetId, activeUser)
            }
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