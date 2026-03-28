package com.example.sheduleapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.scheduleapp.domain.model.AppTheme
import com.example.sheduleapp.presentation.SettingsViewModel
import com.example.sheduleapp.presentation.screen.GroupSearchScreen
import com.example.sheduleapp.presentation.screen.ScheduleScreen
import com.example.sheduleapp.presentation.screen.SettingsScreen
import com.example.sheduleapp.ui.theme.BottomBarBorderDark
import com.example.sheduleapp.ui.theme.BottomBarBorderLight
import com.example.sheduleapp.ui.theme.BottomBarContainerDark
import com.example.sheduleapp.ui.theme.BottomBarContainerLight
import com.example.sheduleapp.ui.theme.BottomBarSelectedDark
import com.example.sheduleapp.ui.theme.BottomBarSelectedLight
import com.example.sheduleapp.ui.theme.BottomBarUnselectedDark
import com.example.sheduleapp.ui.theme.BottomBarUnselectedLight
import com.example.sheduleapp.ui.theme.ScheduleAppTheme
import androidx.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.offset


sealed class Screen(val title: String, val Icons: ImageVector) {
    data object Schedule : Screen("Расписание", Icons.Filled.DateRange)
    data object GroupSearch : Screen("Группа", Icons.Filled.Search)
    data object Settings : Screen("Настройки", Icons.Filled.Settings)
}

@Composable
fun App() {
    val settingsViewModel: SettingsViewModel = koinInject()
    val settings by settingsViewModel.settings.collectAsState()
    val systemIsDark = isSystemInDarkTheme()

    val darkTheme = when (settings.theme) {
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
        AppTheme.SYSTEM -> systemIsDark
    }

    ScheduleAppTheme(darkTheme = darkTheme) {
        var currentScreen by remember { mutableStateOf<Screen>(Screen.Schedule) }

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                FloatingBottomBar(
                    darkTheme = darkTheme,
                    currentScreen = currentScreen,
                    onScreenSelected = { currentScreen = it }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(bottom = innerPadding.calculateBottomPadding())
            ) {
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

@Composable
private fun FloatingBottomBar(
    darkTheme: Boolean,
    currentScreen: Screen,
    onScreenSelected: (Screen) -> Unit
) {
    val navShape = RoundedCornerShape(38.dp)
    val bottomBarContainer = if (darkTheme) BottomBarContainerDark else BottomBarContainerLight
    val bottomBarBorder = if (darkTheme) BottomBarBorderDark else BottomBarBorderLight
    val selectedColor = if (darkTheme) BottomBarSelectedDark else BottomBarSelectedLight
    val unselectedColor = if (darkTheme) BottomBarUnselectedDark else BottomBarUnselectedLight

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 30.dp, end = 30.dp, top = 8.dp, bottom = 30.dp)
    ) {
        Row(
            modifier = Modifier
                .clip(navShape)
                .background(bottomBarContainer)
                .border(BorderStroke(1.dp, bottomBarBorder), navShape)
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val items = listOf(Screen.Schedule, Screen.GroupSearch, Screen.Settings)
            items.forEach { screen ->
                val isSelected = currentScreen == screen
                val contentColor = if (isSelected) selectedColor else unselectedColor

                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(30.dp))
                        .clickable { onScreenSelected(screen) }
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = screen.Icons,
                        contentDescription = screen.title,
                        tint = contentColor,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = screen.title,
                        color = contentColor,
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun FloatingBottomBarLightPreview() {
    ScheduleAppTheme(darkTheme = false) {
        FloatingBottomBar(
            darkTheme = false,
            currentScreen = Screen.Schedule,
            onScreenSelected = {}
        )
    }
}

@Preview
@Composable
private fun FloatingBottomBarDarkPreview() {
    ScheduleAppTheme(darkTheme = true) {
        FloatingBottomBar(
            darkTheme = true,
            currentScreen = Screen.Settings,
            onScreenSelected = {}
        )
    }
}
