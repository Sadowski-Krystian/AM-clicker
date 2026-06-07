package com.example.am_clicker

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class UpgradeType {
    CLICK_POWER,
    PASSIVE_INCOME
}

data class Upgrade(
    val id: String,
    val name: String,
    val description: String,
    val icon: ImageVector,
    val baseCost: Long,
    val costMultiplier: Double,
    val effectValue: Long,
    val type: UpgradeType
)

object UpgradeData {
    val list = listOf(
        // Click Power Upgrades
        Upgrade(
            id = "click_1",
            name = "Rusty Pickaxe",
            description = "+1 cash per click",
            icon = Icons.Default.Hardware,
            baseCost = 15L,
            costMultiplier = 1.15,
            effectValue = 1L,
            type = UpgradeType.CLICK_POWER
        ),
        Upgrade(
            id = "click_2",
            name = "Iron Shovel",
            description = "+5 cash per click",
            icon = Icons.Default.Construction,
            baseCost = 100L,
            costMultiplier = 1.2,
            effectValue = 5L,
            type = UpgradeType.CLICK_POWER
        ),
        Upgrade(
            id = "click_3",
            name = "Laser Drill",
            description = "+25 cash per click",
            icon = Icons.Default.BlurOn,
            baseCost = 500L,
            costMultiplier = 1.25,
            effectValue = 25L,
            type = UpgradeType.CLICK_POWER
        ),
        // Passive Income Upgrades
        Upgrade(
            id = "passive_1",
            name = "Auto-Clicker Bot",
            description = "+1 cash per second",
            icon = Icons.Default.SmartToy,
            baseCost = 50L,
            costMultiplier = 1.15,
            effectValue = 1L,
            type = UpgradeType.PASSIVE_INCOME
        ),
        Upgrade(
            id = "passive_2",
            name = "Mining Drone",
            description = "+10 cash per second",
            icon = Icons.Default.Flight,
            baseCost = 300L,
            costMultiplier = 1.2,
            effectValue = 10L,
            type = UpgradeType.PASSIVE_INCOME
        ),
        Upgrade(
            id = "passive_3",
            name = "Nuclear Reactor",
            description = "+50 cash per second",
            icon = Icons.Default.SettingsInputComponent,
            baseCost = 1500L,
            costMultiplier = 1.3,
            effectValue = 50L,
            type = UpgradeType.PASSIVE_INCOME
        )
    )
}
