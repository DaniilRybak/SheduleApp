package com.example.scheduleapp.domain.model

data class SettingsModel(
    val theme: AppTheme = AppTheme.SYSTEM,
    val widgetGroup: ScheduleEntry? = null,
    val showWindows: Boolean = false,
    val isWeeklyMode: Boolean = true,
    val favoriteGroups: List<ScheduleEntry> = emptyList()
)

enum class AppTheme(val localName: String) {
    LIGHT("Светлая"),
    DARK("Тёмная"),
    SYSTEM("Системная")
}
