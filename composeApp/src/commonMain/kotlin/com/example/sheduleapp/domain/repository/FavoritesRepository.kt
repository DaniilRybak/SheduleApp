package com.example.scheduleapp.domain.repository

import com.example.scheduleapp.domain.model.AppTheme
import com.example.scheduleapp.domain.model.DisplayMode
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    val favoriteGroupIds: Flow<Set<String>>

    fun observeDisplayMode(): Flow<DisplayMode>
    suspend fun setDisplayMode(mode: DisplayMode)

    fun observeAppTheme(): Flow<AppTheme>
    suspend fun setAppTheme(theme: AppTheme)

    fun observeShowMilitaryLessons(): Flow<Boolean>
    suspend fun setShowMilitaryLessons(show: Boolean)
    suspend fun addFavoriteGroup(groupId: String)
    suspend fun removeFavoriteGroup(groupId: String)
}
