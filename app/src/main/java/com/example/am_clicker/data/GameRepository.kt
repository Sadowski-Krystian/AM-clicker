package com.example.am_clicker.data
import kotlinx.coroutines.flow.Flow

class GameRepository(private val dao: GameDao) {

    // Stats
    val userStats: Flow<UserStatsEntity?> = dao.getUserStats()

    suspend fun saveStats(stats: UserStatsEntity) {
        dao.insertOrUpdateUserStats(stats)
    }

    // Upgrades
    val allUpgrades: Flow<List<UpgradeEntity>> = dao.getAllUpgrades()

    suspend fun saveUpgrade(upgrade: UpgradeEntity) {
        dao.saveUpgrade(upgrade)
    }

    suspend fun getUpgradeLevel(upgradeId: String): Int {
        return dao.getUpgradeById(upgradeId)?.level ?: 0
    }

    // Achievements
    val allAchievements: Flow<List<AchievementEntity>> = dao.getAllAchievements()

    suspend fun saveAchievement(achievement: AchievementEntity) {
        dao.saveAchievement(achievement)
    }
}