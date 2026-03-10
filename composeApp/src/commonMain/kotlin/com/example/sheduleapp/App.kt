package com.example.sheduleapp

import androidx.compose.runtime.*
import com.example.sheduleapp.presentation.screen.GroupSearchScreen
import com.example.sheduleapp.presentation.screen.ScheduleScreen
import com.example.sheduleapp.ui.theme.ScheduleAppTheme

sealed class Screen {
    data object Schedule : Screen()
    data object GroupSearch : Screen()
}

@Composable
fun App() {
    ScheduleAppTheme {
        var currentScreen by remember { mutableStateOf<Screen>(Screen.Schedule) }

        when (currentScreen) {
            Screen.Schedule -> ScheduleScreenWithNavigation(
                onOpenGroupSearch = {
                    println("DEBUG: Opening GroupSearch")
                    currentScreen = Screen.GroupSearch
                }
            )
            Screen.GroupSearch -> GroupSearchScreenWithNavigation(
                onBack = {
                    println("DEBUG: Going back to Schedule")
                    currentScreen = Screen.Schedule
                }
            )
        }
    }
}

@Composable
private fun ScheduleScreenWithNavigation(onOpenGroupSearch: () -> Unit) {
    ScheduleScreen(onOpenGroupSearch = onOpenGroupSearch)
}

@Composable
private fun GroupSearchScreenWithNavigation(onBack: () -> Unit) {
    GroupSearchScreen(onBack = onBack)
}

