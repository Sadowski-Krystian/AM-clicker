package com.example.am_clicker

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class AchievementType {
    CLICKS,
    CASH,
    UPGRADES
}

data class Achievement(
    val id: Int,
    val name: String,
    val description: String,
    val icon: ImageVector,
    val targetValue: Long,
    val type: AchievementType
)

object AchievementData {
    val list = listOf(
        Achievement(
            id = 1,
            name = "Beginner Clicker",
            description = "Click the asteroid 100 times",
            icon = Icons.Default.TouchApp,
            targetValue = 100L,
            type = AchievementType.CLICKS
        ),
        Achievement(
            id = 2,
            name = "Seasoned Miner",
            description = "Click the asteroid 1,000 times",
            icon = Icons.Default.PanToolAlt,
            targetValue = 1000L,
            type = AchievementType.CLICKS
        ),
        Achievement(
            id = 3,
            name = "Clicking Machine",
            description = "Click the asteroid 10,000 times",
            icon = Icons.Default.PrecisionManufacturing,
            targetValue = 10000L,
            type = AchievementType.CLICKS
        ),
        Achievement(
            id = 4,
            name = "First Profit",
            description = "Earn a total of 1,000 cash",
            icon = Icons.Default.AttachMoney,
            targetValue = 1000L,
            type = AchievementType.CASH
        ),
        Achievement(
            id = 5,
            name = "Rich Miner",
            description = "Earn a total of 100,000 cash",
            icon = Icons.Default.Savings,
            targetValue = 100000L,
            type = AchievementType.CASH
        ),
        Achievement(
            id = 6,
            name = "Millionaire",
            description = "Earn a total of 1,000,000 cash",
            icon = Icons.Default.AccountBalanceWallet,
            targetValue = 1000000L,
            type = AchievementType.CASH
        ),
        Achievement(
            id = 7,
            name = "Getting Stronger",
            description = "Buy your first upgrade",
            icon = Icons.Default.TrendingUp,
            targetValue = 1L,
            type = AchievementType.UPGRADES
        ),
        Achievement(
            id = 8,
            name = "Master Optimizer",
            description = "Buy 50 upgrades in total",
            icon = Icons.Default.AutoFixHigh,
            targetValue = 50L,
            type = AchievementType.UPGRADES
        )
    )
}
