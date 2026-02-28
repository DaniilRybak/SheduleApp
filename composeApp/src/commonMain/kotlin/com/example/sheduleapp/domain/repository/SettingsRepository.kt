package com.example.scheduleapp.domain.repository

import com.example.scheduleapp.domain.model.SettingsModel
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettings(): SettingsModel
    suspend fun updateSettings(settings: SettingsModel)
    fun subscribeSettings(): Flow<SettingsModel>
}
