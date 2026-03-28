package com.example.scheduleapp.domain.repository

import com.example.scheduleapp.domain.model.DisplayMode
import kotlinx.coroutines.flow.Flow

interface DisplaySettingsRepository {
    fun observeDisplayMode(): Flow<DisplayMode>
    suspend fun setDisplayMode(mode: DisplayMode)

    fun observeShowMilitaryLessons(): Flow<Boolean>
    suspend fun setShowMilitaryLessons(show: Boolean)
}

