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
import com.example.am_clicker.data.UpgradeEntity
import kotlinx.coroutines.flow.first
import kotlin.math.pow

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

    @OptIn(ExperimentalCoroutinesApi::class)
    val ownedUpgrades: StateFlow<List<UpgradeEntity>> = currentUsername
        .flatMapLatest { username ->
            repository.getAllUpgrades(username)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList<UpgradeEntity>()
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
                
                val username = currentUsername.value
                val currentStats = repository.getUserStats(username).first() ?: continue

                // If the player has auto-mining power, add it to their cash
                if (currentStats.passiveIncomePerSecond > 0) {
                    val updatedStats = currentStats.copy(
                        currentCash = currentStats.currentCash + currentStats.passiveIncomePerSecond,
                        totalCashEarned = currentStats.totalCashEarned + currentStats.passiveIncomePerSecond
                    )
                    repository.saveStats(updatedStats)
                    // Check achievements using the UPDATED stats
                    checkAndSaveAchievements(updatedStats)
                }
            }
        }
    }

    // 3. THE CLICK ACTION: Called whenever the asteroid is tapped
    fun onAsteroidClicked() {
        viewModelScope.launch {
            val username = currentUsername.value
            val currentStats = repository.getUserStats(username).first() ?: return@launch

            val updatedStats = currentStats.copy(
                currentCash = currentStats.currentCash + currentStats.clickPower,
                totalCashEarned = currentStats.totalCashEarned + currentStats.clickPower,
                totalClicks = currentStats.totalClicks + 1
            )
            repository.saveStats(updatedStats)
            checkAndSaveAchievements(updatedStats)
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

            // 1. Usuwamy gracza z bazy ( CASCADE usunie też ulepszenia i osiągnięcia )
            repository.deleteUser(currentUsername)

            // 2. Tworzymy czysty obiekt ze statystykami i zapisujemy go (reinkarnacja gracza)
            val resetStats = UserStatsEntity(username = currentUsername)
            repository.saveStats(resetStats)
        }
    }

    suspend fun checkAndSaveAchievements(statsToUse: UserStatsEntity? = null) {
        val currentStats = statsToUse ?: repository.getUserStats(currentUsername.value).first() ?: return
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

    // --- UPGRADES ---

    fun buyUpgrade(upgrade: Upgrade) {
        viewModelScope.launch {
            val username = currentUsername.value
            
            // 1. Fetch the absolute latest level and stats from the DB
            val upgrades = repository.getAllUpgrades(username).first()
            val stats = repository.getUserStats(username).first() ?: return@launch

            val currentLevel = upgrades.find { it.id == upgrade.id }?.level ?: 0
            val cost = (upgrade.baseCost * upgrade.costMultiplier.pow(currentLevel)).toLong()

            android.util.Log.d("GameDebug", "Buying ${upgrade.name}. Current Level: $currentLevel, Cost: $cost, Cash: ${stats.currentCash}")

            if (stats.currentCash >= cost) {
                // 2. Save the new level immediately
                repository.saveUpgrade(UpgradeEntity(username, upgrade.id, currentLevel + 1))

                // 3. Trigger recalculation which will pull the new level from DB AND deduct cost
                recalculateAndSaveStats(cost)
            } else {
                android.util.Log.d("GameDebug", "Insufficient funds for ${upgrade.name}")
            }
        }
    }

    private suspend fun recalculateAndSaveStats(costToDeduct: Long = 0) {
        val username = currentUsername.value
        // Fetch DIRECTLY from DB to avoid Flow delay
        val currentUpgrades = repository.getAllUpgradesDirect(username)
        val stats = repository.getUserStats(username).first() ?: return

        var newClickPower = 1L
        var newPassiveIncome = 0L

        currentUpgrades.forEach { entity ->
            val definition = UpgradeData.list.find { it.id == entity.id }
            if (definition != null) {
                when (definition.type) {
                    UpgradeType.CLICK_POWER -> newClickPower += definition.effectValue * entity.level
                    UpgradeType.PASSIVE_INCOME -> newPassiveIncome += definition.effectValue * entity.level
                }
            }
        }

        android.util.Log.d("GameDebug", "Recalculated for $username: Power=$newClickPower, Passive=$newPassiveIncome, Count=${currentUpgrades.size}, Levels=${currentUpgrades.joinToString { "${it.id}:${it.level}" }}")

        val updatedStats = stats.copy(
            currentCash = stats.currentCash - costToDeduct,
            totalUpgradesBought = if (costToDeduct > 0) stats.totalUpgradesBought + 1 else stats.totalUpgradesBought,
            clickPower = newClickPower,
            passiveIncomePerSecond = newPassiveIncome
        )
        repository.saveStats(updatedStats)

        // 4. Check achievements using the UPDATED stats
        checkAndSaveAchievements(updatedStats)
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