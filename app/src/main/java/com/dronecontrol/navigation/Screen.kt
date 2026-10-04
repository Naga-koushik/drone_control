package com.dronecontrol.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Top-level destinations in the DroneControl application.
 */
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Dashboard)
    object Connection : Screen("connection", "Connection", Icons.Default.CellTower)
    object Telemetry : Screen("telemetry", "Telemetry", Icons.Default.QueryStats)
    object Map : Screen("map", "Map", Icons.Default.Map)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)

    companion object {
        val items = listOf(Dashboard, Connection, Telemetry, Map, Settings)
    }
}
