package com.example.am_clicker.data


import androidx.room.Entity
import androidx.room.PrimaryKey

// 1. The Global Player Stats
@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey val id: Int = 1,
    val currentCash: Long = 0L,
    val totalCashEarned: Long = 0L,
    val totalClicks: Long = 0L,
    val clickPower: Long = 1L,
    val passiveIncomePerSecond: Long = 0L,
    val totalUpgradesBought: Int = 0,
    val totalAchievementsUnlocked: Int = 0,
    val isSoundEnabled: Boolean = true,
    val isVibrationEnabled: Boolean = true,
    val selectedLanguage: String = "system",
    val lastSavedTimestamp: Long = System.currentTimeMillis()
)

// 2. The Upgrades Inventory
@Entity(tableName = "upgrades")
data class UpgradeEntity(
    @PrimaryKey val id: String,
    val level: Int = 0
)

// 3. The Achievements Progress
@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: String,
    val progress: Long = 0L,
    val isUnlocked: Boolean = false
)