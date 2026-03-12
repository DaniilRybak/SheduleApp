package com.example.sheduleapp

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import com.example.sheduleapp.presentation.screen.GroupSearchScreen
import com.example.sheduleapp.presentation.screen.ScheduleScreen
import com.example.sheduleapp.presentation.screen.SettingsScreen
import com.example.sheduleapp.ui.theme.ScheduleAppTheme
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings



sealed class Screen(val title: String, val Icons: ImageVector) {
    data object Schedule : Screen("Расписание", Icons.Filled.DateRange)
    data object GroupSearch : Screen("Группа", Icons.Filled.Search)
    data object Settings : Screen("Настройки", Icons.Filled.Settings)
}

@Composable
fun App() {
    ScheduleAppTheme {
        var currentScreen by remember { mutableStateOf<Screen>(Screen.Schedule) }

        Scaffold(
            bottomBar = {
                NavigationBar {
                    val items = listOf(Screen.Schedule, Screen.GroupSearch, Screen.Settings)
                    items.forEach { screen ->
                        NavigationBarItem(
                            selected = currentScreen == screen,
                            onClick = { currentScreen = screen },
                            icon = {
                                Icon(
                                    imageVector = screen.Icons,
                                    contentDescription = screen.title )
                            },
                            label = { Text(screen.title) }
                        )
                    }
                }
            }
        ) {
            Box() {
                when (currentScreen) {
                    Screen.Schedule -> ScheduleScreenWithNavigation(
                        onOpenGroupSearch = { currentScreen = Screen.GroupSearch },
                        onOpenSettings = { currentScreen = Screen.Settings }
                    )

                    Screen.GroupSearch -> GroupSearchScreenWithNavigation(
                        onBack = { currentScreen = Screen.Schedule }
                    )

                    Screen.Settings -> SettingsScreenWithNavigation(
                        onBack = { currentScreen = Screen.Schedule }
                    )
                }
            }
        }
    }
}

@Composable
private fun ScheduleScreenWithNavigation(
    onOpenGroupSearch: () -> Unit,
    onOpenSettings: () -> Unit) {
    ScheduleScreen(
        onOpenGroupSearch = onOpenGroupSearch,
        onOpenSettings = onOpenSettings )
}

@Composable
private fun GroupSearchScreenWithNavigation(onBack: () -> Unit) {
    GroupSearchScreen(onBack = onBack)
}

@Composable
private fun SettingsScreenWithNavigation(onBack: () -> Unit) {
    SettingsScreen(onBack = onBack)
}
