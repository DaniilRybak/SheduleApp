package com.example.sheduleapp.storage

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey

object ScheduleKeys {
    private fun normalize(cacheKey: String): String =
        cacheKey.replace(Regex("[^A-Za-z0-9_.-]"), "_")

    fun scheduleJson(cacheKey: String) =
        stringPreferencesKey("schedule_json_${normalize(cacheKey)}")

    fun lastUpdateTime(cacheKey: String) =
        longPreferencesKey("schedule_last_update_${normalize(cacheKey)}")

    val displayMode = stringPreferencesKey("display_mode")
    val appTheme = stringPreferencesKey("app_theme")
    val showMilitaryLessons = booleanPreferencesKey("show_military_lessons")

    val favoriteGroupIds = stringSetPreferencesKey("favorite_group_ids")
}