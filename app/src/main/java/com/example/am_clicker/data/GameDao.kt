package com.example.am_clicker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

    // --- USER STATS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUserStats(stats: UserStatsEntity)

    // Using Flow means your UI will instantly update when stats change!
    @Query("SELECT * FROM user_stats WHERE id = 1")
    fun getUserStats(): Flow<UserStatsEntity?>

    // --- UPGRADES ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUpgrade(upgrade: UpgradeEntity)

    @Query("SELECT * FROM upgrades")
    fun getAllUpgrades(): Flow<List<UpgradeEntity>>

    @Query("SELECT * FROM upgrades WHERE id = :upgradeId")
    suspend fun getUpgradeById(upgradeId: String): UpgradeEntity?

    // --- ACHIEVEMENTS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAchievement(achievement: AchievementEntity)

    @Query("SELECT * FROM achievements")
    fun getAllAchievements(): Flow<List<AchievementEntity>>
}