package com.example.scheduleapp.domain.model

data class SettingsModel(
    val theme: AppTheme = AppTheme.SYSTEM,
    val widgetGroup: ScheduleEntry? = null,
    val showWindows: Boolean = true,
    val isWeeklyMode: Boolean = true,
    val displayMode: DisplayMode = DisplayMode.NORMAL,
    val favoriteGroups: List<ScheduleEntry> = emptyList()
)

enum class DisplayMode(val localName: String) {
    NORMAL("Обычный вид"),
    GRID("Сетка"),
    COMPACT("Компактный")
}

enum class AppTheme(val localName: String) {
    LIGHT("Светлая"),
    DARK("Тёмная"),
    SYSTEM("Системная")
}
