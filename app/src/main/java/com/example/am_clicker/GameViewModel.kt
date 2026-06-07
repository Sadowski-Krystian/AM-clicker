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
                    val newPlayer = UserStatsEntity(username = username, lastSavedTimestamp = System.currentTimeMillis())
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
        // MECHANIZM OFFLINE: Reaguje na uruchomienie aplikacji oraz przełączenie profilu
        viewModelScope.launch {
            currentUsername.collect { username ->
                processOfflineEarnings(username)
            }
        }

        // 2. THE GAME LOOP: Modyfikacja - teraz zapisuje też timestamp co sekundę!
        viewModelScope.launch {
            while (true) {
                delay(1000) // Wait exactly 1 second

                val username = currentUsername.value
                val currentStats = repository.getUserStats(username).first() ?: continue

                // Do gotówki dodajemy dochód pasywny, a timestamp ustawiamy na "TERAZ"
                val updatedStats = currentStats.copy(
                    currentCash = currentStats.currentCash + currentStats.passiveIncomePerSecond,
                    totalCashEarned = currentStats.totalCashEarned + currentStats.passiveIncomePerSecond,
                    lastSavedTimestamp = System.currentTimeMillis() // <--- Serce systemu offline
                )
                repository.saveStats(updatedStats)

                // Check achievements using the UPDATED stats
                if (currentStats.passiveIncomePerSecond > 0) {
                    checkAndSaveAchievements(updatedStats)
                }
            }
        }
    }

    // Funkcja licząca zyski za czas spędzony poza grą
    private suspend fun processOfflineEarnings(username: String) {
        val currentStats = repository.getUserStats(username).first()
        val currentTimestamp = System.currentTimeMillis()

        if (currentStats == null) {
            // Jeśli gracz jest nowy, tworzymy mu bazowy profil z czasem startowym
            val newPlayer = UserStatsEntity(username = username, lastSavedTimestamp = currentTimestamp)
            repository.saveStats(newPlayer)
        } else {
            // Jeśli gracz istnieje, sprawdzamy czy ma dochód pasywny i stary timestamp
            if (currentStats.lastSavedTimestamp > 0 && currentStats.passiveIncomePerSecond > 0) {
                // Obliczamy ile sekund minęło (milisekundy / 1000)
                val elapsedSeconds = (currentTimestamp - currentStats.lastSavedTimestamp) / 1000

                if (elapsedSeconds > 0) {
                    val earnedCash = elapsedSeconds * currentStats.passiveIncomePerSecond

                    val updatedStats = currentStats.copy(
                        currentCash = currentStats.currentCash + earnedCash,
                        totalCashEarned = currentStats.totalCashEarned + earnedCash,
                        lastSavedTimestamp = currentTimestamp // Aktualizujemy czas na obecny
                    )

                    repository.saveStats(updatedStats)
                    checkAndSaveAchievements(updatedStats)
                    return
                }
            }
            // Jeżeli nie zarobił (bo np. dopiero zaczął i ma passiveIncome = 0),
            // i tak odświeżamy jego timestamp wejścia do gry
            repository.saveStats(currentStats.copy(lastSavedTimestamp = currentTimestamp))
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
                totalClicks = currentStats.totalClicks + 1,
                lastSavedTimestamp = System.currentTimeMillis() // Aktualizacja przy kliknięciu
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
            val currentUsername = uiState.value.username
            repository.deleteUser(currentUsername)

            val resetStats = UserStatsEntity(username = currentUsername, lastSavedTimestamp = System.currentTimeMillis())
            repository.saveStats(resetStats)
        }
    }

    suspend fun checkAndSaveAchievements(statsToUse: UserStatsEntity? = null) {
        val currentStats = statsToUse ?: repository.getUserStats(currentUsername.value).first() ?: return
        val username = currentStats.username
        var unlockedCount = 0

        AchievementData.list.forEach { achievement ->
            val progressValue = when (achievement.type) {
                AchievementType.CLICKS -> currentStats.totalClicks
                AchievementType.CASH -> currentStats.totalCashEarned
                AchievementType.UPGRADES -> currentStats.totalUpgradesBought.toLong()
            }

            val isUnlocked = progressValue >= achievement.targetValue
            if (isUnlocked) { unlockedCount++ }

            val entity = AchievementEntity(
                username = username,
                id = achievement.id,
                progress = progressValue,
                isUnlocked = isUnlocked
            )
            repository.saveAchievement(entity)
        }

        if (unlockedCount != currentStats.totalAchievementsUnlocked) {
            val updatedStats = currentStats.copy(totalAchievementsUnlocked = unlockedCount)
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
            val activeUser = currentUsername.value
            val currentStats = repository.getUserStats(activeUser).firstOrNull() ?: return@launch

            if (currentStats.currentCash >= cost) {
                val newCash = currentStats.currentCash - cost
                val updatedStats = currentStats.copy(currentCash = newCash, lastSavedTimestamp = System.currentTimeMillis())
                repository.saveStats(updatedStats)
                repository.unlockPlanet(planetId, activeUser)
            }
        }
    }

    // --- UPGRADES ---

    fun buyUpgrade(upgrade: Upgrade) {
        viewModelScope.launch {
            val username = currentUsername.value

            val upgrades = repository.getAllUpgrades(username).first()
            val stats = repository.getUserStats(username).first() ?: return@launch

            val currentLevel = upgrades.find { it.id == upgrade.id }?.level ?: 0
            val cost = (upgrade.baseCost * upgrade.costMultiplier.pow(currentLevel)).toLong()

            if (stats.currentCash >= cost) {
                repository.saveUpgrade(UpgradeEntity(username, upgrade.id, currentLevel + 1))
                recalculateAndSaveStats(cost)
            }
        }
    }

    private suspend fun recalculateAndSaveStats(costToDeduct: Long = 0) {
        val username = currentUsername.value
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

        val updatedStats = stats.copy(
            currentCash = stats.currentCash - costToDeduct,
            totalUpgradesBought = if (costToDeduct > 0) stats.totalUpgradesBought + 1 else stats.totalUpgradesBought,
            clickPower = newClickPower,
            passiveIncomePerSecond = newPassiveIncome,
            lastSavedTimestamp = System.currentTimeMillis() // Aktualizacja przy zakupach
        )
        repository.saveStats(updatedStats)
        checkAndSaveAchievements(updatedStats)
    }
}

// 4. FACTORY
class GameViewModelFactory(private val repository: GameRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}