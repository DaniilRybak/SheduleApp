package com.example.scheduleapp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.example.scheduleapp.domain.repository.FavoritesRepository
import com.example.sheduleapp.storage.ScheduleKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : FavoritesRepository {

    override val favoriteGroupIds: Flow<Set<String>> =
        dataStore.data.map { prefs -> prefs[ScheduleKeys.favoriteGroupIds] ?: emptySet() }

    override suspend fun addFavoriteGroup(groupId: String) {
        dataStore.edit { prefs ->
            val updated = (prefs[ScheduleKeys.favoriteGroupIds] ?: emptySet()).toMutableSet()
            updated.add(groupId)
            prefs[ScheduleKeys.favoriteGroupIds] = updated
        }
    }

    override suspend fun removeFavoriteGroup(groupId: String) {
        dataStore.edit { prefs ->
            val updated = (prefs[ScheduleKeys.favoriteGroupIds] ?: emptySet()).toMutableSet()
            updated.remove(groupId)
            prefs[ScheduleKeys.favoriteGroupIds] = updated
        }
    }
}

