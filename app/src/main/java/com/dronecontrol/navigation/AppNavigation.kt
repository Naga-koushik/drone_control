package com.dronecontrol.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dronecontrol.ui.screens.ConnectionScreen
import com.dronecontrol.ui.screens.DashboardScreen
import com.dronecontrol.ui.screens.MapScreen
import com.dronecontrol.ui.screens.SettingsScreen
import com.dronecontrol.ui.screens.TelemetryScreen
import com.dronecontrol.ui.theme.GcsCardBackground
import com.dronecontrol.ui.theme.GcsCardBorder
import com.dronecontrol.ui.theme.GcsCyan
import com.dronecontrol.ui.theme.GcsDarkBackground
import com.dronecontrol.ui.theme.GcsTextMuted
import com.dronecontrol.viewmodel.DroneViewModel

@Composable
fun AppNavigation(
    viewModel: DroneViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = GcsDarkBackground,
        bottomBar = {
            NavigationBar(
                containerColor = GcsCardBackground,
                modifier = Modifier.border(width = 1.dp, color = GcsCardBorder)
            ) {
                Screen.items.forEach { screen ->
                    val isSelected = currentRoute == screen.route
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = screen.title.uppercase(),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        selected = isSelected,
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = GcsCyan,
                            selectedTextColor = GcsCyan,
                            unselectedIconColor = GcsTextMuted,
                            unselectedTextColor = GcsTextMuted,
                            indicatorColor = GcsCyan.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Dashboard.route,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(Screen.Dashboard.route) {
                    DashboardScreen(uiState = uiState, viewModel = viewModel)
                }
                composable(Screen.Connection.route) {
                    ConnectionScreen(uiState = uiState, viewModel = viewModel)
                }
                composable(Screen.Telemetry.route) {
                    TelemetryScreen(uiState = uiState)
                }
                composable(Screen.Map.route) {
                    MapScreen(uiState = uiState)
                }
                composable(Screen.Settings.route) {
                    SettingsScreen(uiState = uiState)
                }
            }
        }
    }
}
