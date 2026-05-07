package com.example.am_clicker.data


import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

// 1. The Global Player Stats
@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey
    val username: String = "Player1", // Domyślna nazwa

    val currentCash: Long = 0L,       // 0 na start
    val totalCashEarned: Long = 0L,
    val totalClicks: Long = 0L,
    val clickPower: Long = 1L,        // 1, żeby gracz mógł w ogóle klikać!
    val passiveIncomePerSecond: Long = 0L,
    val totalUpgradesBought: Int = 0,
    val totalAchievementsUnlocked: Int = 0,
    val selectedLanguage: String = "system",
    val lastSavedTimestamp: Long = 0L,
    val isSoundEnabled: Boolean = true,
    val isVibrationEnabled: Boolean = true
)

// 2. The Upgrades Inventory
@Entity(
    tableName = "upgrades",
    // Złożony klucz główny: ulepszenie jest unikalne dla pary (nazwa_gracza, id_ulepszenia)
    primaryKeys = ["username", "id"],
    // Relacja: Jeśli usuniesz użytkownika, jego ulepszenia znikną automatycznie (CASCADE)
    foreignKeys = [
        ForeignKey(
            entity = UserStatsEntity::class,
            parentColumns = ["username"],
            childColumns = ["username"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UpgradeEntity(
    val username: String, // <-- Klucz obcy wskazujący na gracza
    val id: String,
    val level: Int
)

// 3. The Achievements Progress
@Entity(
    tableName = "achievement",
    primaryKeys = ["username", "id"],
    foreignKeys = [
        ForeignKey(
            entity = UserStatsEntity::class,
            parentColumns = ["username"],
            childColumns = ["username"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AchievementEntity(
    val username: String, // <-- Klucz obcy wskazujący na gracza
    val id: Int,
    val progress: Long,
    val isUnlocked: Boolean
)