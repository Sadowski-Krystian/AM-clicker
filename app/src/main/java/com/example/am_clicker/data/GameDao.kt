package com.example.am_clicker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

    // --- USER STATS ---
    @Upsert
    suspend fun insertOrUpdateUserStats(stats: UserStatsEntity)

    @Query("DELETE FROM user_stats WHERE username = :username")
    suspend fun deleteUser(username: String)

    // Zmiana: Szukamy po username, a nie po sztywnym id = 1
    @Query("SELECT * FROM user_stats WHERE username = :username")
    fun getUserStats(username: String): Flow<UserStatsEntity?>

    // --- UPGRADES ---
    @Upsert
    suspend fun saveUpgrade(upgrade: UpgradeEntity)

    // Zmiana: Pobieramy ulepszenia tylko dla konkretnego gracza
    @Query("SELECT * FROM upgrades WHERE username = :username")
    fun getAllUpgrades(username: String): Flow<List<UpgradeEntity>>

    // DODANO: Bezpośrednie pobieranie listy (nie-Flow) do obliczeń
    @Query("SELECT * FROM upgrades WHERE username = :username")
    suspend fun getAllUpgradesDirect(username: String): List<UpgradeEntity>

    // Zmiana: Szukamy ulepszenia po ID ORAZ po nazwie gracza (złożony klucz)
    @Query("SELECT * FROM upgrades WHERE id = :upgradeId AND username = :username")
    suspend fun getUpgradeById(upgradeId: String, username: String): UpgradeEntity?

    // --- ACHIEVEMENTS ---
    @Upsert
    suspend fun saveAchievement(achievement: AchievementEntity)

    // Zmiana: Pobieramy osiągnięcia tylko dla konkretnego gracza
    @Query("SELECT * FROM achievement WHERE username = :username")
    fun getAllAchievements(username: String): Flow<List<AchievementEntity>>

    // --- ATLAS ---
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun unlockPlanet(atlasEntity: AtlasEntity)

    // Zwraca tylko listę ID odblokowanych planet dla danego gracza
    @Query("SELECT planetId FROM atlas_unlocked WHERE username = :username")
    fun getUnlockedPlanets(username: String): Flow<List<Int>>
}