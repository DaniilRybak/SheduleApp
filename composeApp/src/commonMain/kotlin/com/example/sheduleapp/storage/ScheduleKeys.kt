package com.example.sheduleapp.storage

import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object ScheduleKeys {
    private fun normalize(cacheKey: String): String =
        cacheKey.replace(Regex("[^A-Za-z0-9_.-]"), "_")

    fun scheduleJson(cacheKey: String) =
        stringPreferencesKey("schedule_json_${normalize(cacheKey)}")

    fun lastUpdateTime(cacheKey: String) =
        longPreferencesKey("schedule_last_update_${normalize(cacheKey)}")
}