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
    val name: Int,
    val description: Int,
    val icon: ImageVector,
    val targetValue: Long,
    val type: AchievementType
)

object AchievementData {
    val list = listOf(
        Achievement(
            id = 1,
            name = R.string.achiev_name_beginner_clicker,
            description = R.string.achiev_desc_beginner_clicker,
            icon = Icons.Default.TouchApp,
            targetValue = 100L,
            type = AchievementType.CLICKS
        ),
        Achievement(
            id = 2,
            name = R.string.achiev_name_seasoned_miner,
            description = R.string.achiev_desc_seasoned_miner,
            icon = Icons.Default.PanToolAlt,
            targetValue = 1000L,
            type = AchievementType.CLICKS
        ),
        Achievement(
            id = 3,
            name = R.string.achiev_name_clicking_machine,
            description = R.string.achiev_desc_clicking_machine,
            icon = Icons.Default.PrecisionManufacturing,
            targetValue = 10000L,
            type = AchievementType.CLICKS
        ),
        Achievement(
            id = 4,
            name = R.string.achiev_name_first_profit,
            description = R.string.achiev_desc_first_profit,
            icon = Icons.Default.AttachMoney,
            targetValue = 1000L,
            type = AchievementType.CASH
        ),
        Achievement(
            id = 5,
            name = R.string.achiev_name_rich_miner,
            description = R.string.achiev_desc_rich_miner,
            icon = Icons.Default.Savings,
            targetValue = 100000L,
            type = AchievementType.CASH
        ),
        Achievement(
            id = 6,
            name = R.string.achiev_name_millionaire,
            description = R.string.achiev_desc_millionaire,
            icon = Icons.Default.AccountBalanceWallet,
            targetValue = 1000000L,
            type = AchievementType.CASH
        ),
        Achievement(
            id = 7,
            name = R.string.achiev_name_getting_stronger,
            description = R.string.achiev_desc_getting_stronger,
            icon = Icons.Default.TrendingUp,
            targetValue = 1L,
            type = AchievementType.UPGRADES
        ),
        Achievement(
            id = 8,
            name = R.string.achiev_name_master_optimizer,
            description = R.string.achiev_desc_master_optimizer,
            icon = Icons.Default.AutoFixHigh,
            targetValue = 50L,
            type = AchievementType.UPGRADES
        )
    )
}
