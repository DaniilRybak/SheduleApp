package com.example.scheduleapp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.example.scheduleapp.domain.model.DisplayMode
import com.example.scheduleapp.domain.repository.DisplaySettingsRepository
import com.example.sheduleapp.storage.ScheduleKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DisplaySettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : DisplaySettingsRepository {

    override fun observeDisplayMode(): Flow<DisplayMode> =
        dataStore.data.map { prefs ->
            val raw = prefs[ScheduleKeys.displayMode]
            DisplayMode.entries.firstOrNull { it.name == raw } ?: DisplayMode.NORMAL
        }

    override suspend fun setDisplayMode(mode: DisplayMode) {
        dataStore.edit { prefs ->
            prefs[ScheduleKeys.displayMode] = mode.name
        }
    }

    override fun observeShowMilitaryLessons(): Flow<Boolean> =
        dataStore.data.map { prefs -> prefs[ScheduleKeys.showMilitaryLessons] ?: false }

    override suspend fun setShowMilitaryLessons(show: Boolean) {
        dataStore.edit { prefs ->
            prefs[ScheduleKeys.showMilitaryLessons] = show
        }
    }
}

