package com.example.am_clicker.data
import kotlinx.coroutines.flow.Flow

class GameRepository(private val dao: GameDao) {

    // Stats - Zmieniono z `val` na `fun`
    fun getUserStats(username: String = "Player1"): Flow<UserStatsEntity?> {
        return dao.getUserStats(username)
    }

    suspend fun saveStats(stats: UserStatsEntity) {
        dao.insertOrUpdateUserStats(stats)
    }

    // Upgrades - Zmieniono z `val` na `fun`
    fun getAllUpgrades(username: String = "Player1"): Flow<List<UpgradeEntity>> {
        return dao.getAllUpgrades(username)
    }

    suspend fun saveUpgrade(upgrade: UpgradeEntity) {
        dao.saveUpgrade(upgrade)
    }

    suspend fun getUpgradeLevel(upgradeId: String, username: String = "Player1"): Int {
        return dao.getUpgradeById(upgradeId, username)?.level ?: 0
    }

    // Achievements - Zmieniono z `val` na `fun`
    fun getAllAchievements(username: String = "Player1"): Flow<List<AchievementEntity>> {
        return dao.getAllAchievements(username)
    }

    suspend fun saveAchievement(achievement: AchievementEntity) {
        dao.saveAchievement(achievement)
    }

    fun getUnlockedPlanets(username: String = "Player1"): Flow<List<Int>> {
        return dao.getUnlockedPlanets(username)
    }

    suspend fun unlockPlanet(planetId: Int, username: String = "Player1") {
        dao.unlockPlanet(AtlasEntity(username, planetId))
    }
}