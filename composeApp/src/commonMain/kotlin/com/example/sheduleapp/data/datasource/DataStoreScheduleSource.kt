package com.example.sheduleapp.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.example.sheduleapp.data.ScheduleLocalDataSource
import com.example.sheduleapp.storage.ScheduleKeys
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlin.time.Clock

class DataStoreScheduleSource(
    private val dataStore: DataStore<Preferences>
) : ScheduleLocalDataSource {

    override suspend fun saveSchedule(cacheKey: String, json: String) {
        dataStore.edit { prefs ->
            prefs[ScheduleKeys.scheduleJson(cacheKey)] = json
            prefs[ScheduleKeys.lastUpdateTime(cacheKey)] = Clock.System.now().toEpochMilliseconds()
        }
    }

    override suspend fun getScheduleJson(cacheKey: String): String? =
        dataStore.data.map { it[ScheduleKeys.scheduleJson(cacheKey)] }.first()

    override suspend fun getLastUpdateTime(cacheKey: String): Long? =
        dataStore.data.map { it[ScheduleKeys.lastUpdateTime(cacheKey)] }.first()
}