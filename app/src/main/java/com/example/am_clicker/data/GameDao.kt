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

    // Zmiana: Szukamy po username, a nie po sztywnym id = 1
    @Query("SELECT * FROM user_stats WHERE username = :username")
    fun getUserStats(username: String): Flow<UserStatsEntity?>

    // --- UPGRADES ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUpgrade(upgrade: UpgradeEntity)

    // Zmiana: Pobieramy ulepszenia tylko dla konkretnego gracza
    @Query("SELECT * FROM upgrades WHERE username = :username")
    fun getAllUpgrades(username: String): Flow<List<UpgradeEntity>>

    // Zmiana: Szukamy ulepszenia po ID ORAZ po nazwie gracza (złożony klucz)
    @Query("SELECT * FROM upgrades WHERE id = :upgradeId AND username = :username")
    suspend fun getUpgradeById(upgradeId: String, username: String): UpgradeEntity?

    // --- ACHIEVEMENTS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAchievement(achievement: AchievementEntity)

    // Zmiana: Pobieramy osiągnięcia tylko dla konkretnego gracza
    @Query("SELECT * FROM achievement WHERE username = :username")
    fun getAllAchievements(username: String): Flow<List<AchievementEntity>>
}