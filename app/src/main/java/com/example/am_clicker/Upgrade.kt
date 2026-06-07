package com.example.am_clicker

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class UpgradeType {
    CLICK_POWER,
    PASSIVE_INCOME
}

data class Upgrade(
    val id: String,
    @StringRes val nameResId: Int,         // Zmieniono ze String na Int
    @StringRes val descriptionResId: Int,  // Zmieniono ze String na Int
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
            nameResId = R.string.upgrade_pickaxe_name,
            descriptionResId = R.string.upgrade_pickaxe_desc,
            icon = Icons.Default.Hardware,
            baseCost = 15L,
            costMultiplier = 1.15,
            effectValue = 1L,
            type = UpgradeType.CLICK_POWER
        ),
        Upgrade(
            id = "click_2",
            nameResId = R.string.upgrade_shovel_name,
            descriptionResId = R.string.upgrade_shovel_desc,
            icon = Icons.Default.Construction,
            baseCost = 100L,
            costMultiplier = 1.2,
            effectValue = 5L,
            type = UpgradeType.CLICK_POWER
        ),
        Upgrade(
            id = "click_3",
            nameResId = R.string.upgrade_drill_name,
            descriptionResId = R.string.upgrade_drill_desc,
            icon = Icons.Default.BlurOn,
            baseCost = 500L,
            costMultiplier = 1.25,
            effectValue = 25L,
            type = UpgradeType.CLICK_POWER
        ),
        // Passive Income Upgrades
        Upgrade(
            id = "passive_1",
            nameResId = R.string.upgrade_bot_name,
            descriptionResId = R.string.upgrade_bot_desc,
            icon = Icons.Default.SmartToy,
            baseCost = 50L,
            costMultiplier = 1.15,
            effectValue = 1L,
            type = UpgradeType.PASSIVE_INCOME
        ),
        Upgrade(
            id = "passive_2",
            nameResId = R.string.upgrade_drone_name,
            descriptionResId = R.string.upgrade_drone_desc,
            icon = Icons.Default.Flight,
            baseCost = 300L,
            costMultiplier = 1.2,
            effectValue = 10L,
            type = UpgradeType.PASSIVE_INCOME
        ),
        Upgrade(
            id = "passive_3",
            nameResId = R.string.upgrade_reactor_name,
            descriptionResId = R.string.upgrade_reactor_desc,
            icon = Icons.Default.SettingsInputComponent,
            baseCost = 1500L,
            costMultiplier = 1.3,
            effectValue = 50L,
            type = UpgradeType.PASSIVE_INCOME
        )
    )
}